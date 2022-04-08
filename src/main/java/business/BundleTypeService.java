package business;

import dataAccess.cache.IBundleTypeCache;
import dataAccess.entity.BundleType;
import dataAccess.repository.IBundleTypeRepo;

import java.util.Iterator;
import java.util.List;

public class BundleTypeService implements IBundleTypeService
{
	private IBundleTypeRepo  repo;
	private IBundleTypeCache cache;
	private BundleType       client;

	public BundleTypeService(IBundleTypeRepo iBundleTypeRepo, IBundleTypeCache iBundleTypeCache)
	{
		this.repo  = iBundleTypeRepo;
		this.cache = iBundleTypeCache;

		List<BundleType> res= repo.get();
		Iterator<BundleType> iterator = res.iterator();
		while (iterator.hasNext())
		{
			cache.put(iterator.next());
		}
	}

	@Override
	public void add()
	{
		repo.save(client);
		cache.put(client);
	}

	@Override
	public List<BundleType> get()
	{
		if (cache.isEmpty())
		{
			List<BundleType>     res      = repo.get();
			Iterator<BundleType> iterator = res.iterator();
			while (iterator.hasNext())
			{
				cache.put(iterator.next());
			}
			return res;
		}
		return cache.get();
	}

	@Override
	public BundleType get(long id)
	{
		if(cache.contains(id))
		{
			return cache.get(id);
		}
		BundleType res = repo.get(id);
		cache.put(res);
		return res;
	}

	@Override
	public void update(String name)
	{
		client.setName(name);
		repo.save(client);
	}

	@Override
	public void delete()
	{
		repo.delete(client);
		cache.delete(client.getId());
	}

	@Override
	public BundleType getClient()
	{
		return client;
	}

	@Override
	public void setClient(BundleType client)
	{
		this.client = client;
	}
}
