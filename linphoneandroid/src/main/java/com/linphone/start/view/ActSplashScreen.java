package com.linphone.start.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import com.linphone.R;
import com.linphone.addressbook.AddressBookPresenter;
import com.linphone.login.view.LoginPhoneActivity;
import com.linphone.util.LinphoneManager;


public class ActSplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        new AddressBookPresenter(this);
        // 去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_splash_screen);
        // 闪屏核心代码
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (LinphoneManager.getCore().getProxyConfigList().length > 0)
                {
                    return;
                }
                Intent intent = new Intent(ActSplashScreen.this,
                        LoginPhoneActivity.class);
                // 从启动动画切换到主界面
                startActivity(intent);
                ActSplashScreen.this.finish();// 结束动画
            }
        }, 3000);
    }

}
