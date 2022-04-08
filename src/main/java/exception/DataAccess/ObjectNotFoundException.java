package exception.DataAccess;

public class ObjectNotFoundException extends RuntimeException
{
	private String message="Объекта нет в базе";

	private ObjectNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ObjectNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public ObjectNotFoundException()
	{
		super();
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
