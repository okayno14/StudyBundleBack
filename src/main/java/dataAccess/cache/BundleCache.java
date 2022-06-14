package dataAccess.cache;

import business.bundle.BundlePredicate;
import dataAccess.entity.Bundle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BundleCache implements IBundleCache
{
	private Map<Long, Bundle> cache = new HashMap<>();
	private CacheController cacheController;

	public BundleCache(CacheController cacheController)
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
	public Bundle get(long id)
	{
		return cache.get(id);
	}

	@Override
	public void put(Bundle bundle)
	{
		cache.put(bundle.getId(),bundle);
		cacheController.added(bundle);
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

	@Override
	public List<Bundle> filter(BundlePredicate bundlePredicate, Bundle sample)
	{
		List<Bundle> bundles = new LinkedList<>(cache.values());
		List<Bundle> res = new LinkedList<>();
		for(Bundle b: bundles)
		{
			if(bundlePredicate.check(sample,b))
			{
				res.add(b);
			}
		}
		return res;
	}
}
