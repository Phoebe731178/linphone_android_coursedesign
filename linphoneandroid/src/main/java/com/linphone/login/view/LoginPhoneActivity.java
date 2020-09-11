package com.linphone.login.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.addressbook.view.AddressBookImpl;
import com.linphone.login.LoginHandler;
import com.linphone.login.PhoneLoginHandler;
import com.linphone.login.exceptions.LoginException;
import org.linphone.core.AccountCreator;

import java.lang.ref.WeakReference;

public class LoginPhoneActivity extends Activity
{
    private PhoneLoginHandler loginHandler;
    private EditText phoneEditText;
    private EditText authCodeEditText;
    private Button confirmPhoneButton;
    private Button confirmAuthCodeButton;
    private static Handler handler;
    private static class MyHandler extends Handler
    {
        private WeakReference<Activity> mActivityReference;
        public MyHandler(Activity activity)
        {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg)
        {
            LoginPhoneActivity mActivity = (LoginPhoneActivity) mActivityReference.get();
            String status = msg.getData().getString("status");
            Log.i("status", msg.getData().getString("status"));
            switch (msg.what)
            {
                case LoginHandler.CREATE_ACCOUNT: Toast.makeText(mActivity, "CreateAccount: " + status, Toast.LENGTH_LONG).show(); break;
                case LoginHandler.RECOVER_ACCOUNT: Toast.makeText(mActivity, "RecoverAccount: " + status, Toast.LENGTH_LONG).show(); break;
                case LoginHandler.LOGIN_LINPHONE_ACCOUNT:
                {
                    Toast.makeText(mActivity, "LoginLinphoneAccount: " + status, Toast.LENGTH_LONG).show();
                    if (status.equals(AccountCreator.Status.RequestOk.name()))
                        mActivity.startActivity(new Intent(mActivity, AddressBookImpl.class));
                    if (status.equals(AccountCreator.Status.WrongActivationCode.name()))
                        Toast.makeText(mActivity, "错误的验证码", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    }

    public static Handler getHandler()
    {
        return handler;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
        phoneEditText = findViewById(R.id.phoneEditText);
        authCodeEditText = findViewById(R.id.authCodeEditText);
        confirmPhoneButton = findViewById(R.id.confirmPhoneButton);
        confirmAuthCodeButton = findViewById(R.id.confirmAuthCodeButton);
        loginHandler = PhoneLoginHandler.getInstance();
        handler = new MyHandler(LoginPhoneActivity.this);
        confirmPhoneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    loginHandler.getAuthCode(phoneEditText.getText().toString());
                }
                catch (LoginException e)
                {
                    e.printStackTrace();
                }
            }
        });
        confirmAuthCodeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    loginHandler.login(phoneEditText.getText().toString(), authCodeEditText.getText().toString());
                }
                catch (LoginException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
