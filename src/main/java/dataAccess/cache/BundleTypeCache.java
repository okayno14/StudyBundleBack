package dataAccess.cache;

import dataAccess.entity.BundleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BundleTypeCache implements IBundleTypeCache
{
	private Map<Long, BundleType> cache = new HashMap<>();

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
	public BundleType get(long id)
	{
		if (contains(id))
		{
			return cache.get(id);
		}
		else
		{
			return new BundleType();
		}
	}

	@Override
	public List<BundleType> get()
	{
		return new ArrayList<BundleType>(cache.values());
	}

	@Override
	public void put(BundleType bundleType)
	{
		if(!contains(bundleType.getId()))
		{
			cache.put(bundleType.getId(),bundleType);
		}
	}

	@Override
	public void delete(long id)
	{
		if(contains(id))
		{
			cache.remove(id);
		}
	}
}
