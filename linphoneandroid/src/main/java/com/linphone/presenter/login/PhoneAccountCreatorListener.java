package com.linphone.presenter.login;

import android.util.Log;
import com.linphone.presenter.login.exceptions.*;
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
        if (response == null)
            response = "";
        switch (status)
        {
            case RequestFailed:
            case ServerError: throw new NetworkException(status.name() + response);
            case UnexpectedError: throw new LoginException("Unexpected error:" + response);
            case RequestOk:
                Log.i("LoginLinphoneAccount", "Account for " + creator.getPhoneNumber() + " is login");
                break;
            case WrongActivationCode: throw new InvalidAuthCodeException(response);
            case AccountNotExist: throw new AccountNotActivatedException(response);
            case AccountNotActivated: throw new AccountNotActivatedException(response);
            default: Log.i("LoginLinphoneAccount", "Unhandled status [" + status.name() + "]");
        }
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
        creator = PhoneLoginHandler.getInstance().getAccountCreator();
        if (response == null)
            response = "";
        switch (status)
        {
            case PhoneNumberInvalid: throw new InvalidUserNameException("Invalid cellphone number:" + response);
            case PhoneNumberOverused: throw new PhoneNumberOverusedException(response);
            case RequestFailed:
            case ServerError: throw new NetworkException(status.name() + response);
            case RequestOk:
                Log.i("RecoverAccount", "Account for " + creator.getPhoneNumber() + " is recovered");
                break;
            case UnexpectedError: throw new LoginException("Unexpected error:" + response);
            case AccountNotExist:
                {
                    Log.i("RecoverAccount", "Account " + creator.getUsername() + " does not exist, creating account.");
                    creator.createAccount();
                    creator.recoverAccount();
                    break;
                }
            default: Log.i("RecoverAccount", "Unhandled status [" + status.name() + "]");
        }
    }

    @Override
    public void onCreateAccount(AccountCreator creator, AccountCreator.Status status, String response)
    {
        creator = PhoneLoginHandler.getInstance().getAccountCreator();
        if (response == null)
            response = "";
        switch (status)
        {
            case PhoneNumberInvalid: throw new InvalidUserNameException("Invalid cellphone number:" + response);
            case PhoneNumberOverused: throw new PhoneNumberOverusedException(response);
            case RequestFailed:
            case ServerError: throw new NetworkException(status.name() + response);
            case RequestOk:
            case AccountCreated: Log.i("CreateAccount", "Account for " + creator.getPhoneNumber() + " is created");
                break;
            case UnexpectedError: throw new LoginException("Unexpected error:" + response);
            default: Log.i("CreateAccount", "Unhandled status [" + status.name() + "]");
        }
    }
}
