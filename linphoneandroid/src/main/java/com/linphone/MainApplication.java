package com.linphone;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.linphone.util.LinphoneContext;
import com.linphone.util.LinphoneManager;
import org.linphone.core.Core;

import java.util.LinkedList;

public class MainApplication extends Application
{
    private LinphoneContext linphoneContext;
    public static LinkedList<Activity> activities;

    private void loadLibLinphone()
    {
        System.loadLibrary("c++_shared");
        System.loadLibrary("bctoolbox");
        System.loadLibrary("ortp");
        System.loadLibrary("mediastreamer");
        System.loadLibrary("linphone");
    }

    public synchronized LinphoneContext getLinphoneContext()
    {
        return linphoneContext;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        loadLibLinphone();
        linphoneContext = new LinphoneContext(getApplicationContext());
        activities = new LinkedList<>();
        linphoneContext.start();
        Core core = LinphoneManager.getCore();
        if (core.getProxyConfigList().length > 0)
        {
            Toast.makeText(this, "检测到已有账号", Toast.LENGTH_SHORT).show();
        }
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                Log.i("activityCheck", activity.getClass().getName());
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }
}