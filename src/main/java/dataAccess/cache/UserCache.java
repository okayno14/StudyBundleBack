package dataAccess.cache;

import dataAccess.entity.User;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;
import model.Core;

import java.util.*;

public class UserCache implements IUserCache
{
	private CacheController   cacheController;
	private Map<Long, User>   allUsers           = new HashMap<>();

	public UserCache(CacheController cacheController)
	{
		this.cacheController = cacheController;
	}

	@Override
	public boolean contains(long id)
	{
		return allUsers.containsKey(id);
	}

	@Override
	public boolean isEmpty()
	{
		return allUsers.isEmpty();
	}

	@Override
	public User get(long id)
	{
		return allUsers.get(id);
	}

	@Override
	public User getByEmail(String email)
	{
		Iterator<User> iterator = allUsers.values().iterator();
		User user=null;
		while(iterator.hasNext())
		{
			user=iterator.next();
			if(user.getEmail().equals(email))
			{
				return user;
			}
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public void put(User user)
	{
		allUsers.put(user.getId(), user);
		cacheController.added(user);
	}

	@Override
	public void delete(long id)
	{
		allUsers.remove(id);
	}

	@Override
	public void cleanNonAuth()
	{
		Iterator it = allUsers.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Long,User> pair = (Map.Entry<Long,User>)it.next();
			User user = pair.getValue();
			if(user.getToken()==null)
			{
				it.remove();
			}
		}
	}
}
