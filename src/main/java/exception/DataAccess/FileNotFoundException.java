package exception.DataAccess;

import dataAccess.entity.Bundle;

public class FileNotFoundException extends RuntimeException
{
	private String message = "Файл работы не найден.";
	private Bundle bundle;

	public FileNotFoundException(Bundle bundle)
	{
		super();
		this.bundle = bundle;
	}

	public FileNotFoundException(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return message+" Bundle.id = "+Long.toString(bundle.getId());
	}
}
