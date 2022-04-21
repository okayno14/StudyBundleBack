package exception.DataAccess;

public class NotValidException extends RuntimeException
{
	public NotValidException(String message)
	{
		super(message);
	}

	@Override
	public String getMessage()
	{
		return super.getMessage();
	}
}
