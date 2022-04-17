package dataAccess.cache;

import dataAccess.entity.Requirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequirementCache implements IRequirementCache
{
	private Map<Long,Requirement> cache = new HashMap<>();

	@Override
	public boolean contains(long id)
	{
		return cache.containsKey(id);
	}

	@Override
	public boolean contains(Requirement req)
	{
		return cache.containsValue(req);
	}

	@Override
	public boolean isEmpty()
	{
		return cache.isEmpty();
	}

	@Override
	public Requirement get(long id)
	{
		return cache.get(id);
	}

	@Override
	public List<Requirement> get()
	{
		return new ArrayList<>(cache.values());
	}

	@Override
	public void put(Requirement req)
	{
		cache.put(req.getId(),req);
	}

	@Override
	public void delete(long id)
	{
		cache.remove(id);
	}
}
