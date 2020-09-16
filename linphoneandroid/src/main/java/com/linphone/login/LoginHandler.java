package com.linphone.login;

import com.linphone.login.exceptions.LoginException;

public interface LoginHandler
{
    void login(String userName, String authCode) throws LoginException;
    int ACTIVATE_ACCOUNT = 0;
    int ACTIVATE_ALIAS = 1;
    int IS_ACCOUNT_LINKED = 2;
    int LINK_ACCOUNT = 3;
    int IS_ALIAS_USED = 4;
    int IS_ACCOUNT_ACTIVATED = 5;
    int LOGIN_LINPHONE_ACCOUNT = 6;
    int IS_ACCOUNT_EXIST = 7;
    int UPDATE_ACCOUNT = 8;
    int RECOVER_ACCOUNT = 9;
    int CREATE_ACCOUNT = 10;
}
