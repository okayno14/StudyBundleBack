package dataAccess.cache;

import dataAccess.entity.User;
import model.Core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserCache implements IUserCache
{
	private CacheController   cacheController;
	private Map<Long, User>   allUsers           = new HashMap<>();
	private Map<String, User> authenticatedUsers = new HashMap<>();

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
	public boolean contains(String token)
	{
		return authenticatedUsers.containsKey(token);
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
	public User get(String token)
	{
		return authenticatedUsers.get(token);
	}

	@Override
	public void put(User user)
	{
		allUsers.put(user.getId(), user);
		cacheController.added(user);
	}

	@Override
	public void authenticate(long id)
	{
		User user = allUsers.get(id);
		authenticatedUsers.put(user.getToken(), user);
	}

	@Override
	public void delete(long id)
	{
		String token = allUsers.get(id).getToken();
		if (contains(token))
		{
			authenticatedUsers.remove(token);
		}
		allUsers.remove(id);
	}

	@Override
	public void delete(String token)
	{
		authenticatedUsers.remove(token);
	}

	@Override
	public void cleanNonAuth()
	{
		allUsers.clear();
		allUsers = new HashMap<>();
		Iterator<User> i = authenticatedUsers.values().iterator();
		while (i.hasNext())
		{
			User u = i.next();
			allUsers.put(u.getId(), u);
		}
	}

	@Override
	public void cleanAuth()
	{
		Iterator<User> i = authenticatedUsers.values().iterator();
		while (i.hasNext())
		{
			allUsers.remove(i.next().getId());
		}
		authenticatedUsers.clear();
	}
}
