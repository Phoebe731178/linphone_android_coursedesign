package com.linphone.login.exceptions;

public class AccountNotActivatedException extends LoginException
{
    public AccountNotActivatedException()
    {
        super();
    }
    public AccountNotActivatedException(String message)
    {
        super(message);
    }
    public AccountNotActivatedException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
