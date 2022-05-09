package exception.DataAccess;

public class ZipDamaged extends RuntimeException
{
	private String message="Загруженный архив повреждён";

	public ZipDamaged()
	{
		super();
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
