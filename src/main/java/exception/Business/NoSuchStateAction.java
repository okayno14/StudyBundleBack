package exception.Business;

import dataAccess.entity.CourseState;

public class NoSuchStateAction extends RuntimeException
{
	private String state;
	private String message;

	public NoSuchStateAction(String state)
	{
		this.state   = state;
		this.message = "Операция недопустима для состояния "+ state;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
