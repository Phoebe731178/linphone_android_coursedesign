package com.linphone.presenter.login;

import android.util.Log;
import com.linphone.util.LinphoneManager;
import org.linphone.core.AccountCreator;
import com.linphone.presenter.login.exceptions.*;

public class PhoneLoginHandler implements LoginHandler
{
    private AccountCreator accountCreator = LinphoneManager.getInstance().getAccountCreator();
    private static final String PHONE_NUMBER_REGEX = "^1[3-9]\\d{9}$";
    private static final String AUTH_CODE_REGEX = "^\\d{4}$";
    private static final String COUNTRY_CODE = "86";

    /**
     *
     * Login the certain user with the specified cellphone number and the authCode.
     * The country code is set to 86 (China).
     * The userName and authCode will be checked before actual login action.
     * @param userName cellphone number
     * @param authCode auth code from SMS
     * @throws LoginException
     */
    @Override
    public void login(String userName, String authCode) throws LoginException
    {
        checkUserName(userName);
        checkAuthCode(authCode);
        accountCreator.setPhoneNumber(userName, COUNTRY_CODE);
        accountCreator.setActivationCode(authCode);
        AccountCreator.Status loginStatus = accountCreator.loginLinphoneAccount();
        if (loginStatus.equals(AccountCreator.Status.WrongActivationCode))
        {
            throw new WrongAuthCodeException();
        }
        if (!loginStatus.equals(AccountCreator.Status.RequestOk))
        {
            throw new NetworkException(loginStatus.name());
        }
        Log.i("login", loginStatus.name());
    }

    /**
     *
     * Check if the username is valid.
     * @param userName cellphone number
     * @throws LoginException
     */
    private void checkUserName(String userName) throws LoginException
    {
        if (!userName.matches(PHONE_NUMBER_REGEX))
        {
            throw new InvalidUserNameException("Not a cellphone number");
        }
        AccountCreator.Status accountActivationStatus = accountCreator.isAccountActivated();
        AccountCreator.Status accountExistenceStatus = accountCreator.isAccountExist();
        if (accountExistenceStatus.equals(AccountCreator.Status.AccountNotExist))
        {
            throw new AccountNotExistException();
        }
        if (accountActivationStatus.equals(AccountCreator.Status.AccountNotActivated))
        {
            throw new AccountNotActivatedException();
        }
    }

    /**
     *
     * Check the if the authCode matches the regex "^\d{4}$"
     * @param authCode auth code from SMS
     * @throws LoginException
     */
    private void checkAuthCode(String authCode) throws LoginException
    {
        if (!authCode.matches(AUTH_CODE_REGEX))
        {
            throw  new InvalidAuthCodeException("Not an auth code from SMS");
        }
    }

    /**
     *
     * The userName will be checked before sending request to the server.
     * @param userName cellphone number
     * @throws LoginException
     */
    public void getAuthCode(String userName) throws LoginException
    {
        checkUserName(userName);
        accountCreator.setPhoneNumber(userName, COUNTRY_CODE);
        AccountCreator.Status status = accountCreator.recoverAccount();
        if (!status.equals(AccountCreator.Status.RequestOk))
        {
            throw new NetworkException(status.name());
        }
    }
}
