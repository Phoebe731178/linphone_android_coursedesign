/*
 * Copyright (c) 2010-2019 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.linphone.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import org.linphone.core.*;
import org.linphone.core.tools.Log;
import com.linphone.util.settings.LinphonePreferences;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;

/** Handles Linphone's Core lifecycle */
public class LinphoneManager implements SensorEventListener {
    private final String mBasePath;
    private final String mRingSoundFile;
    private final String mCallLogDatabaseFile;
    private final String mFriendsDatabaseFile;
    private final String mUserCertsPath;

    private final Context mContext;
    private final PowerManager mPowerManager;
    private final ConnectivityManager mConnectivityManager;
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;
    private WakeLock mProximityWakelock;
    private final SensorManager mSensorManager;
    private final Sensor mProximity;
    private Timer mTimer;

    private final LinphonePreferences mPrefs;
    private Core mCore;
    private CoreListenerStub mCoreListener;
    private AccountCreator mAccountCreator;
    private AccountCreatorListenerStub mAccountCreatorListener;

    private boolean mExited;
    private boolean mCallGsmON;
    private boolean mProximitySensingEnabled;
    private boolean mHasLastCallSasBeenRejected;
    private Runnable mIterateRunnable;

    public LinphoneManager(Context c) {
        mExited = false;
        mContext = c;
        mBasePath = c.getFilesDir().getAbsolutePath();
        mCallLogDatabaseFile = mBasePath + "/linphone-log-history.db";
        mFriendsDatabaseFile = mBasePath + "/linphone-friends.db";
        mRingSoundFile = mBasePath + "/share/sounds/linphone/rings/notes_of_the_optimistic.mkv";
        mUserCertsPath = mBasePath + "/user-certs";

        mPrefs = LinphonePreferences.instance();
        mPowerManager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        mConnectivityManager =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        mSensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mTelephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);

        Log.i("[Manager] Registering phone state listener");
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        mHasLastCallSasBeenRejected = false;

        File f = new File(mUserCertsPath);
        if (!f.exists()) {
            if (!f.mkdir()) {
                Log.e("[Manager] " + mUserCertsPath + " can't be created.");
            }
        }

    }

    public static synchronized LinphoneManager getInstance() {
        LinphoneManager manager = LinphoneContext.instance().getLinphoneManager();
        if (manager == null) {
            throw new RuntimeException(
                    "[Manager] Linphone Manager should be created before accessed");
        }
        if (manager.mExited) {
            throw new RuntimeException(
                    "[Manager] Linphone Manager was already destroyed. "
                            + "Better use getCore and check returned value");
        }
        return manager;
    }

    public static synchronized Core getCore() {
        if (!LinphoneContext.isReady()) return null;

        if (getInstance().mExited) {
            // Can occur if the UI thread play a posted event but in the meantime the
            // LinphoneManager was destroyed
            // Ex: stop call and quickly terminate application.
            return null;
        }
        return getInstance().mCore;
    }

    /* End of static */

    public synchronized void destroy() {
        destroyManager();
        // Wait for Manager to destroy everything before setting mExited to true
        // Otherwise some objects might crash during their own destroy if they try to call
        // LinphoneManager.getCore(), for example to unregister a listener
        mExited = true;
    }

    public void restartCore() {
        Log.w("[Manager] Restarting Core");
        mCore.stop();
        mCore.start();
    }

    private void destroyCore() {
        Log.w("[Manager] Destroying Core");
        if (LinphonePreferences.instance() != null) {
            // We set network reachable at false before destroying the Core
            // to not send a register with expires at 0
            if (LinphonePreferences.instance().isPushNotificationEnabled()) {
                Log.w(
                        "[Manager] Setting network reachability to False to prevent unregister and allow incoming push notifications");
                mCore.setNetworkReachable(false);
            }
        }
        mCore.stop();
        mCore.removeListener(mCoreListener);
    }

    private synchronized void destroyManager() {
        Log.w("[Manager] Destroying Manager");
        changeStatusToOffline();

        if (mTelephonyManager != null) {
            Log.i("[Manager] Unregistering phone state listener");
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        if (mTimer != null) mTimer.cancel();

        if (mCore != null) {
            destroyCore();
            mCore = null;
        }
    }

    public synchronized void startLibLinphone(boolean isPush, CoreListener listener) {
        try {
            mCore =
                    Factory.instance()
                            .createCore(
                                    mPrefs.getLinphoneDefaultConfig(),
                                    mPrefs.getLinphoneFactoryConfig(),
                                    mContext);
            mCore.addListener(listener);
            mCore.addListener(mCoreListener);

            if (isPush) {
                Log.w(
                        "[Manager] We are here because of a received push notification, enter background mode before starting the Core");
                mCore.enterBackground();
            }

            mCore.start();

            mIterateRunnable =
                    new Runnable() {
                        @Override
                        public void run() {
                            if (mCore != null) {
                                mCore.iterate();
                            }
                        }
                    };
            configureCore();
        } catch (Exception e) {
            Log.e(e, "[Manager] Cannot start linphone");
        }
    }

    private synchronized void configureCore() {
        Log.i("[Manager] Configuring Core");

        mCore.setZrtpSecretsFile(mBasePath + "/zrtp_secrets");

        String deviceName = mPrefs.getDeviceName(mContext);
        String appName = "linphone_android_courseDesign";
        String androidVersion = org.linphone.core.BuildConfig.VERSION_NAME;
        String userAgent = appName + "/" + androidVersion + " (" + deviceName + ") LinphoneSDK";

        mCore.setUserAgent(
                userAgent,
                "4.5.0"
                        + " ("
                        + "alpha.89+d12833d"
                        + ")");

        mCore.setCallLogsDatabasePath(mCallLogDatabaseFile);
        mCore.setFriendsDatabasePath(mFriendsDatabaseFile);
        mCore.setUserCertificatesPath(mUserCertsPath);
        enableDeviceRingtone(mPrefs.isDeviceRingtoneEnabled());

        int availableCores = Runtime.getRuntime().availableProcessors();
        Log.w("[Manager] MediaStreamer : " + availableCores + " cores detected and configured");

        mCore.migrateLogsFromRcToDb();

        mProximityWakelock =
                mPowerManager.newWakeLock(
                        PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                        mContext.getPackageName() + ";manager_proximity_sensor");

        resetCameraFromPreferences();

        mAccountCreator = mCore.createAccountCreator(LinphonePreferences.instance().getXmlrpcUrl());
        mAccountCreator.setListener(mAccountCreatorListener);
        mCallGsmON = false;

        Log.i("[Manager] Core configured");
    }

    public void resetCameraFromPreferences() {
        Core core = getCore();
        if (core == null) return;

        boolean useFrontCam = LinphonePreferences.instance().useFrontCam();
        String firstDevice = null;
        for (String camera : core.getVideoDevicesList()) {
            if (firstDevice == null) {
                firstDevice = camera;
            }

            if (useFrontCam) {
                if (camera.contains("Front")) {
                    Log.i("[Manager] Found front facing camera: " + camera);
                    core.setVideoDevice(camera);
                    return;
                }
            }
        }

        Log.i("[Manager] Using first camera available: " + firstDevice);
        core.setVideoDevice(firstDevice);
    }

    /* Account linking */

    public AccountCreator getAccountCreator() {
        if (mAccountCreator == null) {
            Log.w("[Manager] Account creator shouldn't be null !");
            mAccountCreator =
                    mCore.createAccountCreator(LinphonePreferences.instance().getXmlrpcUrl());
            mAccountCreator.setListener(mAccountCreatorListener);
        }
        return mAccountCreator;
    }

    public void isAccountWithAlias() {
        if (mCore.getDefaultProxyConfig() != null) {
            long now = new Timestamp(new Date().getTime()).getTime();
            AccountCreator accountCreator = getAccountCreator();
            if (LinphonePreferences.instance().getLinkPopupTime() == null
                    || Long.parseLong(LinphonePreferences.instance().getLinkPopupTime()) < now) {
                accountCreator.reset();
                accountCreator.setUsername(
                        LinphonePreferences.instance()
                                .getAccountUsername(
                                        LinphonePreferences.instance().getDefaultAccountIndex()));
                accountCreator.isAccountExist();
            }
        } else {
            LinphonePreferences.instance().setLinkPopupTime(null);
        }
    }

    /* Presence stuff */

    private boolean isPresenceModelActivitySet() {
        if (mCore != null) {
            return mCore.getPresenceModel() != null
                    && mCore.getPresenceModel().getActivity() != null;
        }
        return false;
    }

    public void changeStatusToOnline() {
        if (mCore == null) return;
        PresenceModel model = mCore.createPresenceModel();
        model.setBasicStatus(PresenceBasicStatus.Open);
        mCore.setPresenceModel(model);
    }

    public void changeStatusToOnThePhone() {
        if (mCore == null) return;

        if (isPresenceModelActivitySet()
                && mCore.getPresenceModel().getActivity().getType()
                        != PresenceActivity.Type.OnThePhone) {
            mCore.getPresenceModel().getActivity().setType(PresenceActivity.Type.OnThePhone);
        } else if (!isPresenceModelActivitySet()) {
            PresenceModel model =
                    mCore.createPresenceModelWithActivity(PresenceActivity.Type.OnThePhone, null);
            mCore.setPresenceModel(model);
        }
    }

    private void changeStatusToOffline() {
        if (mCore != null) {
            PresenceModel model = mCore.getPresenceModel();
            model.setBasicStatus(PresenceBasicStatus.Closed);
            mCore.setPresenceModel(model);
        }
    }

    /* Tunnel stuff */

    public void initTunnelFromConf() {
        if (!mCore.tunnelAvailable()) return;

        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        Tunnel tunnel = mCore.getTunnel();
        tunnel.cleanServers();
        TunnelConfig config = mPrefs.getTunnelConfig();
        if (config.getHost() != null) {
            tunnel.addServer(config);
            manageTunnelServer(info);
        }
    }

    private boolean isTunnelNeeded(NetworkInfo info) {
        if (info == null) {
            Log.i("[Manager] No connectivity: tunnel should be disabled");
            return false;
        }

        String pref = mPrefs.getTunnelMode();

        if ("enabled".equals(pref)) {
            return true;
        }

        if (info.getType() != ConnectivityManager.TYPE_WIFI
                && "enabled".equals(pref)) {
            Log.i("[Manager] Need tunnel: 'no wifi' connection");
            return true;
        }

        return false;
    }

    private void manageTunnelServer(NetworkInfo info) {
        if (mCore == null) return;
        if (!mCore.tunnelAvailable()) return;
        Tunnel tunnel = mCore.getTunnel();

        Log.i("[Manager] Managing tunnel");
        if (isTunnelNeeded(info)) {
            Log.i("[Manager] Tunnel need to be activated");
            tunnel.setMode(Tunnel.Mode.Enable);
        } else {
            Log.i("[Manager] Tunnel should not be used");
            String pref = mPrefs.getTunnelMode();
            tunnel.setMode(Tunnel.Mode.Disable);
            if ("enabled".equals(pref)) {
                tunnel.setMode(Tunnel.Mode.Auto);
            }
        }
    }

    /* Proximity sensor stuff */

    public void enableProximitySensing(boolean enable) {
        if (enable) {
            if (!mProximitySensingEnabled) {
                mSensorManager.registerListener(
                        this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
                mProximitySensingEnabled = true;
            }
        } else {
            if (mProximitySensingEnabled) {
                mSensorManager.unregisterListener(this);
                mProximitySensingEnabled = false;
                // Don't forgeting to release wakelock if held
                if (mProximityWakelock.isHeld()) {
                    mProximityWakelock.release();
                }
            }
        }
    }

    private Boolean isProximitySensorNearby(final SensorEvent event) {
        float threshold = 4.001f; // <= 4 cm is near

        final float distanceInCm = event.values[0];
        final float maxDistance = event.sensor.getMaximumRange();
        Log.d(
                "[Manager] Proximity sensor report ["
                        + distanceInCm
                        + "] , for max range ["
                        + maxDistance
                        + "]");

        if (maxDistance <= threshold) {
            // Case binary 0/1 and short sensors
            threshold = maxDistance;
        }
        return distanceInCm < threshold;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.timestamp == 0) return;
        if (isProximitySensorNearby(event)) {
            if (!mProximityWakelock.isHeld()) {
                mProximityWakelock.acquire();
            }
        } else {
            if (mProximityWakelock.isHeld()) {
                mProximityWakelock.release();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /* Other stuff */

    public void enableDeviceRingtone(boolean use) {
        if (use) {
            mCore.setRing(null);
        } else {
            mCore.setRing(mRingSoundFile);
        }
    }

    public boolean getCallGsmON() {
        return mCallGsmON;
    }

    public void setCallGsmON(boolean on) {
        mCallGsmON = on;
        if (on && mCore != null) {
            mCore.pauseAllCalls();
        }
    }

    private String getString(int key) {
        return mContext.getString(key);
    }

    public boolean hasLastCallSasBeenRejected() {
        return mHasLastCallSasBeenRejected;
    }

    public void lastCallSasRejected(boolean rejected) {
        mHasLastCallSasBeenRejected = rejected;
    }
}
