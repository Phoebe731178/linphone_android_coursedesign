package com.linphone;

import android.app.Application;
import android.widget.Toast;
import com.linphone.util.LinphoneContext;
import com.linphone.util.LinphoneManager;
import org.linphone.core.Core;

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
        Core core = LinphoneManager.getCore();
        if (core.getProxyConfigList().length > 0)
        {
            Toast.makeText(this, "检测到已有账号", Toast.LENGTH_SHORT).show();
        }
    }
}
