package com.linphone.login.view;

import android.animation.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.login.PhoneLoginHandler;
import com.linphone.login.exceptions.*;
import com.linphone.util.LinphoneContext;
import com.linphone.login.LoginHandler;
import com.linphone.menu.view.MenuActivity;
import org.linphone.core.AccountCreator;

import java.lang.ref.WeakReference;

public class LoginPhoneActivity extends Activity
{
    private PhoneLoginHandler loginHandler;
    private EditText phoneEditText;
    private EditText authCodeEditText;
    private Button confirmPhoneButton;
    private TextView confirmAuthCodeButton;
    private LinphoneContext linphoneContext;
    private View progress;
    private View mInputLayout;
    private float mWidth, mHeight;
    private LinearLayout mPhone, mCode;
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
                case LoginHandler.CREATE_ACCOUNT:
                    if(status.equals(AccountCreator.Status.RequestOk.name()))
                        Toast.makeText(mActivity, "创建账户,已发送验证码" , Toast.LENGTH_LONG).show();
                    if(status.equals(AccountCreator.Status.RequestFailed.name()))
                        Toast.makeText(mActivity, "网络错误", Toast.LENGTH_LONG).show();

                    break;
                case LoginHandler.RECOVER_ACCOUNT:
                    if(status.equals(AccountCreator.Status.RequestOk.name()))
                        Toast.makeText(mActivity, "已发送验证码", Toast.LENGTH_LONG).show();
                    if(status.equals(AccountCreator.Status.RequestFailed.name()))
                        Toast.makeText(mActivity, "网络错误", Toast.LENGTH_LONG).show();
                    if(status.equals(AccountCreator.Status.PhoneNumberOverused.name()))
                        Toast.makeText(mActivity, "登录过于频繁", Toast.LENGTH_LONG).show();
                    break;
                case LoginHandler.LOGIN_LINPHONE_ACCOUNT: {
                    if(status.equals(AccountCreator.Status.WrongActivationCode.name())){
                        mActivity.recovery();
                        Toast.makeText(mActivity, "验证码错误", Toast.LENGTH_LONG).show();
                        //mActivity.startActivity(new Intent(mActivity, MenuActivity.class));
                    }
                    if(status.equals(AccountCreator.Status.RequestOk.name()))
                        mActivity.startActivity(new Intent(mActivity, MenuActivity.class));
                    if(status.equals(AccountCreator.Status.RequestFailed.name())) {
                        mActivity.recovery();
                        Toast.makeText(mActivity, "网络错误", Toast.LENGTH_LONG).show();
                        //mActivity.startActivity(new Intent(mActivity, MenuActivity.class));
                    }
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        initView();
    }

    private void initView() {
        phoneEditText = findViewById(R.id.phoneEditText);
        authCodeEditText = findViewById(R.id.authCodeEditText);
        confirmPhoneButton = findViewById(R.id.confirmPhoneButton);
        confirmAuthCodeButton = findViewById(R.id.confirmAuthCodeButton);
        mInputLayout = findViewById(R.id.input_layout);
        mPhone = (LinearLayout) findViewById(R.id.input_layout_phone);
        mCode = (LinearLayout) findViewById(R.id.input_layout_authcode);
        progress = findViewById(R.id.layout_progress);
        loginHandler = PhoneLoginHandler.getInstance();
        handler = new MyHandler(LoginPhoneActivity.this);
        confirmPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loginHandler.getAuthCode(phoneEditText.getText().toString());
                } catch (LoginException e) {
                    e.printStackTrace();
                    if(e instanceof InvalidUserNameException){
                        Toast.makeText(LoginPhoneActivity.this,"请输入正确的手机号" ,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        confirmAuthCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //计算控件的高与宽
                mWidth = confirmAuthCodeButton.getMeasuredWidth();
                mHeight = confirmAuthCodeButton.getMeasuredHeight();
                //隐藏输入框
                mPhone.setVisibility(View.INVISIBLE);
                mCode.setVisibility(View.INVISIBLE);

                inputAnimator(mInputLayout, mWidth, mHeight);
                try {
                    loginHandler.login(phoneEditText.getText().toString(), authCodeEditText.getText().toString());
                    progress.setVisibility(View.VISIBLE);
                    progressAnimator(progress);
                    mInputLayout.setVisibility(View.INVISIBLE);
                } catch (LoginException e) {
                    e.printStackTrace();
                    recovery();
                    if(e instanceof InvalidAuthCodeException){
                        Toast.makeText(LoginPhoneActivity.this,"验证码无效" ,
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    /**
     * 输入框的动画效果
     * @param view
     * 控件
     * @param w
     * 宽
     * @param h
     * 高
     */
    private void inputAnimator(final View view, float w, float h) {

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
/**
 * 动画结束后，先显示加载的动画，然后再隐藏输入框
 */
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        recovery();
                    }
                }, 2200);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

    }

    /**
     * 出现进度动画
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
        animator3.setDuration(1500);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }
    /**
     * 恢复初始状态
     */
    private void recovery() {
        progress.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        mPhone.setVisibility(View.VISIBLE);
        mCode.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f,1f );
        animator2.setDuration(300);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }
}

