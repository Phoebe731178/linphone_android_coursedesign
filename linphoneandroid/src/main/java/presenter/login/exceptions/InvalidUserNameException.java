package presenter.login.exceptions;

public class InvalidUserNameException extends LoginException
{
    public InvalidUserNameException()
    {
        super();
    }
    public InvalidUserNameException(String message)
    {
        super(message);
    }
    public InvalidUserNameException(String message, Throwable throwable)
    {
        super(message,throwable);
    }
}
