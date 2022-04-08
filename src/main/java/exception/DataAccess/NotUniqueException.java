package exception.DataAccess;

public class NotUniqueException extends RuntimeException
{
	private String message = "Отправленные данные есть в системе";

	public NotUniqueException()
	{
		super();
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
