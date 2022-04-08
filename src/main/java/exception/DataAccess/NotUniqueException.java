package exception.DataAccess;

public class NotUniqueException extends RuntimeException
{
	private String message = "Отправленные данные есть в системе";

	public NotUniqueException(Throwable cause)
	{
		super(cause);
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
