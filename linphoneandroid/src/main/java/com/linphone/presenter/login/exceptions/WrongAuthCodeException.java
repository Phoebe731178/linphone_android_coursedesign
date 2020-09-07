package com.linphone.presenter.login.exceptions;

public class WrongAuthCodeException extends LoginException
{
    public WrongAuthCodeException()
    {
        super();
    }
    public WrongAuthCodeException(String message)
    {
        super(message);
    }
    public WrongAuthCodeException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
