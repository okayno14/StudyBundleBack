package exception.DataAccess;

public class ZipFileSizeException extends RuntimeException
{
	int    received;
	int    limitSZ;
	String message;

	public ZipFileSizeException(int received, int limitSZ)
	{
		super();
		this.received = received;
		this.limitSZ  = limitSZ;
		message       = "Превышено ограничение для Архива. Допустимо=" + limitSZ/((int)Math.pow(2,20)) +
				"(МБ). Получено:" + received/((int)Math.pow(2,20)) + "(МБ)";
	}


}
