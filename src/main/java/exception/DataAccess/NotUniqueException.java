package exception.DataAccess;

public class NotUniqueException extends RuntimeException
{
	private String message = "Отправленные данные есть в системе";

	public NotUniqueException(String message, Throwable cause)
	{
		super(cause);
		this.message=message;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
