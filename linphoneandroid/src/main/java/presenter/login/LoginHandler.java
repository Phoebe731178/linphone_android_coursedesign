package presenter.login;

import presenter.login.exceptions.LoginException;

public interface LoginHandler
{
    void login(String userName, String authCode) throws LoginException;
}
