package exception.Controller;

public class ControllerException extends RuntimeException
{
	public ControllerException(Throwable cause)
	{
		super(cause);
	}

	@Override
	public synchronized Throwable getCause()
	{
		return super.getCause();
	}
}
