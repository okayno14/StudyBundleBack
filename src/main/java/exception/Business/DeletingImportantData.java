package exception.Business;

public class DeletingImportantData extends RuntimeException
{
	String message;

	public DeletingImportantData(String message)
	{
		this.message=message;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
