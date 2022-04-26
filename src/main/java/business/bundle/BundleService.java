package business.bundle;

import dataAccess.cache.IBundleCache;
import dataAccess.entity.Bundle;
import dataAccess.repository.IBundleRepo;
import dataAccess.repository.IBundleRepoFile;

import java.util.List;

public class BundleService implements IBundleService
{
	private IBundleRepoFile bundleRepoFile;
	private IBundleRepo     bundleRepo;
	private IBundleCache    bundleCache;

	public BundleService(IBundleRepoFile bundleRepoFile, IBundleRepo bundleRepo,
						 IBundleCache bundleCache)
	{
		this.bundleRepoFile = bundleRepoFile;
		this.bundleRepo     = bundleRepo;
		this.bundleCache    = bundleCache;
	}

	@Override
	public void add(List<Bundle> bundles)
	{
		if (bundles.size() == 0)
		{
			return;
		}
		bundleRepo.save(bundles);
		for(Bundle b:bundles)
		{
			bundleCache.put(b);
		}
	}
}
