package presenter.login.exceptions;

public class AccountNotExistException extends LoginException
{
    public AccountNotExistException()
    {
        super();
    }
    public AccountNotExistException(String message)
    {
        super(message);
    }
    public AccountNotExistException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
