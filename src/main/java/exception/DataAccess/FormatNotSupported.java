package exception.DataAccess;

public class FormatNotSupported extends RuntimeException
{
	String message = "Неподдерживаемый формат:";

	public FormatNotSupported(String message)
	{
		this.message = message.concat(message);
	}

	public FormatNotSupported(Throwable cause, String format)
	{
		super(cause);
		message = message.concat(format);
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}
}
