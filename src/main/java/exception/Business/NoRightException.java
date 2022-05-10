package exception.Business;

public class NoRightException extends RuntimeException
{
	private String message;
	public NoRightException()
	{
		super();
	}

	public NoRightException(String message)
	{
		this.message=message;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
