package com.linphone.login;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.linphone.login.view.LoginPhoneActivity;
import com.linphone.util.LinphoneManager;
import org.linphone.core.AccountCreator;
import org.linphone.core.AccountCreatorListener;

public class PhoneAccountCreatorListener implements AccountCreatorListener
{
    @Override
    public void onActivateAccount(AccountCreator creator, AccountCreator.Status status, String response) {

    }

    @Override
    public void onActivateAlias(AccountCreator creator, AccountCreator.Status status, String response) {

    }

    @Override
    public void onIsAccountLinked(AccountCreator creator, AccountCreator.Status status, String response) {

    }

    @Override
    public void onLinkAccount(AccountCreator creator, AccountCreator.Status status, String response) {

    }

    @Override
    public void onIsAliasUsed(AccountCreator creator, AccountCreator.Status status, String response) {

    }

    @Override
    public void onIsAccountActivated(AccountCreator creator, AccountCreator.Status status, String response)
    {
        Log.i("IsAccountActivated", status.name());
    }

    @Override
    public void onLoginLinphoneAccount(AccountCreator creator, AccountCreator.Status status, String response)
    {
        creator = PhoneLoginHandler.getInstance().getAccountCreator();
        Message message = new Message();
        Bundle bundle = new Bundle();
        if (response == null)
            response = "";
        if (status == AccountCreator.Status.RequestOk)
        {
            Log.i("LoginLinphoneAccount", "Account for " + creator.getPhoneNumber() + " is login");
            LinphoneManager.getCore().clearProxyConfig();
            creator.createProxyConfig();
        }
        Log.i("LoginLinphoneAccount", "status [" + status.name() + "]");
        Log.i("LoginLinphoneAccount", "response: \n" + response);
        bundle.putString("status", status.name());
        message.what = LoginHandler.LOGIN_LINPHONE_ACCOUNT;
        message.setData(bundle);
        LoginPhoneActivity.getHandler().sendMessage(message);
    }

    @Override
    public void onIsAccountExist(AccountCreator creator, AccountCreator.Status status, String response)
    {
        Log.i("AccountExist", status.name());
    }

    @Override
    public void onUpdateAccount(AccountCreator creator, AccountCreator.Status status, String response) {

    }

    @Override
    public void onRecoverAccount(AccountCreator creator, AccountCreator.Status status, String response)
    {
        Message message = new Message();
        Bundle bundle = new Bundle();
        creator = PhoneLoginHandler.getInstance().getAccountCreator();
        if (response == null)
            response = "";
        switch (status)
        {
            case PhoneNumberInvalid: Log.i("RecoverAccount", "Invalid cellphone number:" + response); break;
            case RequestOk:
                Log.i("RecoverAccount", "Account for " + creator.getPhoneNumber() + " is recovered"); break;
            case AccountNotExist:
                {
                    Log.i("RecoverAccount", "Account " + creator.getUsername() + " does not exist, creating account.");
                    creator.createAccount();
                    creator.recoverAccount();
                    break;
                }
        }
        Log.i("RecoverAccount", "status [" + status.name() + "]");
        Log.i("RecoverAccount", "response: \n" + response);
        bundle.putString("status", status.name());
        message.setData(bundle);
        message.what = LoginHandler.RECOVER_ACCOUNT;
        LoginPhoneActivity.getHandler().sendMessage(message);
    }

    @Override
    public void onCreateAccount(AccountCreator creator, AccountCreator.Status status, String response)
    {
        creator = PhoneLoginHandler.getInstance().getAccountCreator();
        Message message = new Message();
        Bundle bundle = new Bundle();
        if (response == null)
            response = "";
        if (status == AccountCreator.Status.PhoneNumberInvalid)
        {
            Log.i("CreateAccount", "Invalid cellphone number:" + response);
        } else if (status == AccountCreator.Status.AccountCreated)
        {
            Log.i("CreateAccount", "Account for " + creator.getPhoneNumber() + " is created");
        }
        Log.i("CreateAccount", "status [" + status.name() + "]");
        Log.i("CreateAccount", "response: \n" + response);
        bundle.putString("status", status.name());
        message.setData(bundle);
        message.what = LoginHandler.CREATE_ACCOUNT;
        LoginPhoneActivity.getHandler().sendMessage(message);
    }
}
