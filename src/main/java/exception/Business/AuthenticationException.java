package exception.Business;

public class AuthenticationException extends RuntimeException
{
	private String message;

	public AuthenticationException(String message)
	{
		this.message=message;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
