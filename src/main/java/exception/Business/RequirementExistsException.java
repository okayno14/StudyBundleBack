package exception.Business;


public class RequirementExistsException extends RuntimeException
{
	String btName;
	String message;

	private void buildMessage()
	{
		message="У данного курса сещуствует тип работы: "+btName+"\n";
	}

	public RequirementExistsException(String btName)
	{
		this.btName = btName;
	}

	@Override
	public String getMessage()
	{
		buildMessage();
		return message;
	}
}
