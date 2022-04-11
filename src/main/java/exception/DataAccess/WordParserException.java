package exception.DataAccess;

public class WordParserException extends RuntimeException
{
	public WordParserException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
