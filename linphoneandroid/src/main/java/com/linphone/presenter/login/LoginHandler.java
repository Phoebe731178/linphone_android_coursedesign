package com.linphone.presenter.login;

import com.linphone.presenter.login.exceptions.LoginException;

public interface LoginHandler
{
    void login(String userName, String authCode) throws LoginException;
}
