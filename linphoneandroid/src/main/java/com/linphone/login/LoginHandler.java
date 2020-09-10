package com.linphone.login;

import com.linphone.login.exceptions.LoginException;

public interface LoginHandler
{
    void login(String userName, String authCode) throws LoginException;
}
