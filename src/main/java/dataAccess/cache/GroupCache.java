package dataAccess.cache;

import dataAccess.entity.Group;

import java.util.HashMap;
import java.util.Map;

public class GroupCache implements IGroupCache
{
	private Map<Long, Group> cache = new HashMap<>();
	private CacheController cacheController;

	public GroupCache(CacheController cacheController)
	{
		this.cacheController = cacheController;
	}

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
	public Group get(long id)
	{
		return cache.get(id);
	}

	@Override
	public void put(Group group)
	{
		cache.put(group.getId(),group);
	}

	@Override
	public void delete(long id)
	{
		cache.remove(id);
	}

	@Override
	public void clean()
	{
		cache.clear();
	}
}
