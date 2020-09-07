package com.linphone;

import android.app.Application;
import com.linphone.util.LinphoneContext;

public class MainApplication extends Application
{
    private LinphoneContext linphoneContext;

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
        linphoneContext.start();
    }
}
