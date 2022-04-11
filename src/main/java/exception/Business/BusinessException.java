package exception.Business;

public class BusinessException extends RuntimeException
{
	public BusinessException(Throwable cause)
	{
		super(cause);
	}
}
