package com.linphone.login.exceptions;

public class LoginException extends RuntimeException
{
    public LoginException()
    {
        super();
    }
    public LoginException(String message)
    {
        super(message);
    }
    public LoginException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
