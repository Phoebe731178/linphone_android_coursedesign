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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;
import com.linphone.MainApplication;
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.addressbook.view.AddressBookImpl;
import com.linphone.addressbook.view.ContactDetail;
import com.linphone.call.LinphoneCallImpl;
import com.linphone.call.view.CallActivity;
import com.linphone.call.view.CallIncomingActivity;
import com.linphone.call.view.CallOutgoingActivity;
import com.linphone.call.view.Dial;
import com.linphone.util.compatibility.Compatibility;
import com.linphone.vo.Contact;
import org.linphone.core.*;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.Version;
import com.linphone.util.settings.LinphonePreferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class LinphoneContext
{
    private static LinphoneContext sInstance = null;

    private Context mContext;
    private CoreListener mListener;

    private final LoggingServiceListener mJavaLoggingService =
            new LoggingServiceListener() {
                @Override
                public void onLogMessageWritten(
                        LoggingService logService, String domain, LogLevel lev, String message) {
                    switch (lev) {
                        case Debug:
                            android.util.Log.d(domain, message);
                            break;
                        case Message:
                            android.util.Log.i(domain, message);
                            break;
                        case Warning:
                            android.util.Log.w(domain, message);
                            break;
                        case Error:
                            android.util.Log.e(domain, message);
                            break;
                        case Fatal:
                        default:
                            android.util.Log.wtf(domain, message);
                            break;
                    }
                }
            };
    private LinphoneManager mLinphoneManager;

    public static boolean isReady() {
        return sInstance != null;
    }

    public static LinphoneContext instance() {
        if (sInstance == null) {
            throw new RuntimeException("[Context] Linphone Context not available!");
        }
        return sInstance;
    }

    public LinphoneContext(Context context) {
        mContext = context;

        LinphonePreferences.instance().setContext(context);
        mListener = new CoreListenerStub()
        {
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                android.util.Log.i("listener", state.name());
                if(state == Call.State.IncomingReceived){
                    onIncomingStarted(call);
                } else if (state == Call.State.Connected) {
                    onCallStarted();
                } else if(state == Call.State.OutgoingInit){
                    onOutgoingStarted();
                } else if (call.getErrorInfo().getReason() == Reason.Declined) {
                    Toast.makeText(mContext, "Declined", Toast.LENGTH_SHORT).show();
                    onCallHangUp(call);
                } else if (call.getErrorInfo().getReason() == Reason.NotFound) {
                    Toast.makeText(mContext, "NotFound", Toast.LENGTH_SHORT).show();
                    onCallHangUp(call);
                } else if (call.getErrorInfo().getReason() == Reason.NotAcceptable) {
                    Toast.makeText(mContext, "NotAcceptable", Toast.LENGTH_SHORT).show();
                    onCallHangUp(call);
                } else if (call.getErrorInfo().getReason() == Reason.Busy) {
                    Toast.makeText(mContext, "Busy", Toast.LENGTH_SHORT).show();
                    onCallHangUp(call);
                } else if (state == Call.State.End) {
                    onCallHangUp(call);
                }
            }

            @Override
            public void onRegistrationStateChanged(Core core, ProxyConfig proxyConfig, RegistrationState state, String message)
            {
                switch (state)
                {
                    case Failed:
                        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                        break;
                    case Progress:
                        Toast.makeText(mContext, "正在登录，请稍后", Toast.LENGTH_SHORT).show();
                        break;
                    case Ok:
                        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, AddressBookImpl.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        break;
                    default:
                        android.util.Log.i("RegistrationStateChanged", "Unhandled state [" + state.name() + "]");
                }
            }
        };

        // Dump some debugging information to the logs
        dumpDeviceInformation();
        dumpLinphoneInformation();

        sInstance = this;
        Log.i("[Context] Ready");

        mLinphoneManager = new LinphoneManager(context);

    }

    public void start()
    {
        Log.i("[Context] Starting");
        mLinphoneManager.startLibLinphone(false, mListener);
    }

    public void destroy() {
        Log.i("[Context] Destroying");
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
            core = null; // To allow the gc calls below to free the Core
        }

        // Destroy the LinphoneManager second to last to ensure any getCore() call will work
        if (mLinphoneManager != null) {
            mLinphoneManager.destroy();
        }

        // Wait for every other object to be destroyed to make LinphoneService.instance() invalid
        sInstance = null;

        if (LinphonePreferences.instance().useJavaLogger()) {
            Factory.instance().getLoggingService().removeListener(mJavaLoggingService);
        }
        LinphonePreferences.instance().destroy();
    }

    public void updateContext(Context context) {
        mContext = context;
    }

    public Context getApplicationContext() {
        return mContext;
    }

    /* Managers accessors */

    public LoggingServiceListener getJavaLoggingService() {
        return mJavaLoggingService;
    }

    public LinphoneManager getLinphoneManager() {
        return mLinphoneManager;
    }

    /* Log device related information */

    private void dumpDeviceInformation() {
        Log.i("==== Phone information dump ====");
        Log.i("DISPLAY NAME=" + Compatibility.getDeviceName(mContext));
        Log.i("DEVICE=" + Build.DEVICE);
        Log.i("MODEL=" + Build.MODEL);
        Log.i("MANUFACTURER=" + Build.MANUFACTURER);
        Log.i("ANDROID SDK=" + Build.VERSION.SDK_INT);
        StringBuilder sb = new StringBuilder();
        sb.append("ABIs=");
        for (String abi : Version.getCpuAbis()) {
            sb.append(abi).append(", ");
        }
        Log.i(sb.substring(0, sb.length() - 2));
    }

    private void dumpLinphoneInformation() {
        Log.i("==== Linphone information dump ====");
        Log.i("VERSION NAME=" + org.linphone.core.BuildConfig.VERSION_NAME);
        Log.i("VERSION CODE=" + org.linphone.core.BuildConfig.VERSION_CODE);
        Log.i("PACKAGE=" + 1);
        Log.i("BUILD TYPE=" + org.linphone.core.BuildConfig.BUILD_TYPE);
        Log.i("SDK VERSION=" + mContext.getString(R.string.linphone_sdk_version));
        Log.i("SDK BRANCH=" + mContext.getString(R.string.linphone_sdk_branch));
    }

    private void onOutgoingStarted(){
        Intent intent = new Intent(mContext, CallOutgoingActivity.class);
        android.util.Log.i("coreListener", "OutgoingInit");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void onCallStarted(){
        android.util.Log.i("coreListener", "Connected");
        Intent intent = new Intent(mContext, CallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void onIncomingStarted(final Call call){
        Intent intent = new Intent(mContext, CallIncomingActivity.class);
        android.util.Log.i("coreListener", "IncomingInit");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void onCallHangUp(Call call){
        android.util.Log.i("coreListener", "hangup");
        Intent intent = null;
        try{
            if(MainApplication.activities != null){
                LinkedList<Activity> activitiesCopy = new LinkedList<>(MainApplication.activities);
                Collections.reverse(activitiesCopy);
                for(Activity activity: activitiesCopy){
                    android.util.Log.i("activityCheck", "now " + activity.getClass().getName());
                    if(activity.getClass().getName().equals("com.linphone.call.view.CallActivity")
                                    || activity.getClass().getName().equals("com.linphone.call.view.CallOutgoingActivity")
                                    || activity.getClass().getName().equals("com.linphone.call.view.CallIncomingActivity")){
                        continue;
                    }
                    if(activity.getClass().getName().equals("com.linphone.addressbook.view.ContactDetail")){
                        String phone = call.getRemoteAddress().getUsername().substring(3);
                        String name = new AddressBookModelImpl(mContext).findNameFromPhone(phone);
                        Contact contact = new Contact(name, Arrays.asList(phone));
                        intent = new Intent(mContext, ContactDetail.class);
                        intent.putExtra("contact", contact);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        break;

                    } else {
                        intent = new Intent(mContext, Dial.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        break;
                    }

                }
            }
        } catch (Exception ignore){}
    }

}
