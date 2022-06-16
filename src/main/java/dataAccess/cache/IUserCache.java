package dataAccess.cache;

import dataAccess.entity.User;

public interface IUserCache
{
	boolean contains(long id);
	boolean isEmpty();
	User get(long id);
	User getByEmail(String email);
	//добавить в общую коллекцию
	void put(User user);
	void delete(long id);
	void cleanNonAuth();
}
