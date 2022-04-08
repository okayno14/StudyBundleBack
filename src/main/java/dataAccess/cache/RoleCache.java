package dataAccess.cache;

import dataAccess.entity.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleCache implements IRoleCache
{
	private Map<Long, Role> cache = new HashMap<>();

	@Override
	public boolean contains(long id)
	{
		return cache.containsKey(id);
	}

	@Override
	public boolean isEmpty()
	{
		return cache.isEmpty();
	}

	@Override
	public Role get(long id)
	{
		return cache.get(id);
	}

	@Override
	public List<Role> get()
	{
		return new ArrayList<Role>(cache.values());
	}

	@Override
	public void put(Role role)
	{
		cache.put(role.getId(),role);
	}

	@Override
	public void delete(long id)
	{
		cache.remove(id);
	}
}
