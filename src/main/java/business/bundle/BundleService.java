package business.bundle;

import dataAccess.cache.IBundleCache;
import dataAccess.entity.Bundle;
import dataAccess.repository.IBundleRepo;
import dataAccess.repository.IBundleRepoFile;

public class BundleService implements IBundleService
{
	private IBundleRepoFile bundleRepoFile;
	private IBundleRepo bundleRepo;
	private IBundleCache bundleCache;
	private Bundle client;

	public BundleService(IBundleRepoFile bundleRepoFile, IBundleRepo bundleRepo,
						 IBundleCache bundleCache)
	{
		this.bundleRepoFile = bundleRepoFile;
		this.bundleRepo     = bundleRepo;
		this.bundleCache    = bundleCache;
	}
}
