package com.linphone.login.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.util.LinphoneContext;
import com.linphone.login.PhoneLoginHandler;
import com.linphone.login.exceptions.LoginException;

public class LoginPhoneActivity extends Activity
{
    private PhoneLoginHandler loginHandler;
    private EditText phoneEditText;
    private EditText authCodeEditText;
    private Button confirmPhoneButton;
    private Button confirmAuthCodeButton;
    private LinphoneContext linphoneContext;

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
