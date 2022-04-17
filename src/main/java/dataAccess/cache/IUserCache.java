package dataAccess.cache;

import dataAccess.entity.User;

public interface IUserCache
{
	boolean contains(long id);
	boolean contains(String token);
	boolean isEmpty();
	User get(long id);
	User get(String token);
	//добавить в общую коллекцию
	void put(User user);
	//скопировать пользователя из общей коллекции в список аутентифицированных
	void authenticate(long id);
	//Удаляем только неаутент. пользователей из кеша
	void delete(long id);
	//Удаляем только аутент. пользователей
	void delete(String token);
	void cleanNonAuth();
	void cleanAuth();
}
