package com.linphone.presenter.login;

import com.linphone.presenter.login.exceptions.InvalidAuthCodeException;
import com.linphone.presenter.login.exceptions.InvalidUserNameException;
import com.linphone.presenter.login.exceptions.LoginException;
import com.linphone.util.LinphoneManager;
import org.linphone.core.AccountCreator;
import org.linphone.core.AccountCreatorListener;

public class PhoneLoginHandler implements LoginHandler
{
    private final AccountCreator accountCreator;
    private static final String PHONE_NUMBER_REGEX = "^1[3-9]\\d{9}$";
    private static final String AUTH_CODE_REGEX = "^\\d{4}$";
    private static final String COUNTRY_CODE = "86";
    private static final String DOMAIN = "sip.linphone.org";
    private static final AccountCreatorListener listener = new PhoneAccountCreatorListener();

    public PhoneLoginHandler()
    {
        accountCreator = LinphoneManager.getInstance().getAccountCreator();
    }

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
        accountCreator.addListener(listener);
        checkUserName(userName);
        checkAuthCode(authCode);
        accountCreator.setPhoneNumber(userName, COUNTRY_CODE);
        accountCreator.setActivationCode(authCode);
        accountCreator.setUsername(accountCreator.getPhoneNumber());
        accountCreator.setDomain(DOMAIN);
        accountCreator.loginLinphoneAccount();
        accountCreator.removeListener(listener);
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
        accountCreator.addListener(listener);
        checkUserName(userName);
        accountCreator.setPhoneNumber(userName, COUNTRY_CODE);
        accountCreator.recoverAccount();
        accountCreator.removeListener(listener);
    }
}
