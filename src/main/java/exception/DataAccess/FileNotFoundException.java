package exception.DataAccess;

import dataAccess.entity.Bundle;

public class FileNotFoundException extends RuntimeException
{
	private String message = "У запрошенной работы нет файла.";
	private Bundle bundle;

	public FileNotFoundException(Bundle bundle)
	{
		super();
		this.bundle = bundle;
	}

	@Override
	public String getMessage()
	{
		return message.concat("Bundle.id="+bundle.getId());
	}
}