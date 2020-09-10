package com.linphone.login.exceptions;

public class InvalidAuthCodeException extends LoginException
{
    public InvalidAuthCodeException()
    {
        super();
    }
    public InvalidAuthCodeException(String message)
    {
        super(message);
    }
    public InvalidAuthCodeException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
