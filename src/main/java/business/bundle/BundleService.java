package business.bundle;

import dataAccess.cache.IBundleCache;
import dataAccess.entity.Author;
import dataAccess.entity.Bundle;
import dataAccess.entity.Course;
import dataAccess.entity.User;
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
		for (Bundle b : bundles)
		{
			bundleCache.put(b);
		}
	}

	@Override
	public Bundle get(long id)
	{
		Bundle res = bundleCache.get(id);
		if (res != null)
		{
			return res;
		}
		res = bundleRepo.get(id);
		bundleCache.put(res);
		return res;
	}

	@Override
	public List<Bundle> get(String courseName, String groupName, User authorFIO)
	{
		List<Bundle> res = bundleRepo.get(courseName, groupName, authorFIO);
		for (Bundle b : res)
		{
			if (!bundleCache.contains(b.getId()))
			{
				bundleCache.put(b);
			}
		}
		return res;
	}

	@Override
	public List<Bundle> get(Course course, User author)
	{
		return null;
	}

	@Override
	public List<Bundle> getAll(User author)
	{
		List<Bundle> res = bundleRepo.getAll(author);
		for (Bundle b : res)
		{
			if (!bundleCache.contains(b.getId()))
			{
				bundleCache.put(b);
			}
		}
		return res;
	}

	@Override
	public void groupChanged(Bundle client)
	{
		Bundle d = new Bundle(client.getNum(), client.getCourse(), client.getBundleType());
		d.addACE(client.getAuthor(), Author.AUTHOR);
		bundleRepoFile.moveGroupChanged(client, d.getFolder());
	}

	@Override
	public byte[] downloadReport(Bundle client)
	{
		return new byte[0];
	}

	@Override
	public void uploadReport(Bundle client, byte[] document)
	{
		bundleRepoFile.save(client,document);
		//написать алгоритм сверки




		client.accept();
		bundleRepo.save(client);
	}

	@Override
	public void decline(Bundle client)
	{

	}

	@Override
	public void delete(Bundle client)
	{

	}
}
