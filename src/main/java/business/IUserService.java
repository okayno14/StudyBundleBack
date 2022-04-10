package business;

import dataAccess.entity.User;

public interface IUserService
{
	User get(long id);
}
