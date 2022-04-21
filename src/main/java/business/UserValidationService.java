package business;

import dataAccess.entity.User;

public class UserValidationService
{
	public boolean check(User client)
	{
		if(client.getPass()==null)
		{
			client.genPass();
		}
		return checkEmail(client);
	}

	private boolean checkEmail(User client)
	{
		return true;
	}
}
