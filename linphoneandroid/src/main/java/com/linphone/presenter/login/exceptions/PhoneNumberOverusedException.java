package com.linphone.presenter.login.exceptions;

public class PhoneNumberOverusedException extends LoginException
{
    public PhoneNumberOverusedException()
    {
        super();
    }
    public PhoneNumberOverusedException(String message)
    {
        super(message);
    }
    public PhoneNumberOverusedException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
