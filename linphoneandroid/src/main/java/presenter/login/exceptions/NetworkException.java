package presenter.login.exceptions;

public class NetworkException extends LoginException
{
    public NetworkException()
    {
        super();
    }
    public NetworkException(String message)
    {
        super(message);
    }
    public NetworkException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
