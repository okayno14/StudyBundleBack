package business.bundle;

import business.bundle.matrix.*;
import dataAccess.cache.IBundleCache;
import dataAccess.entity.*;
import dataAccess.repository.IBundleRepo;
import dataAccess.repository.IBundleRepoFile;
import exception.Business.BusinessException;
import exception.Business.NoRightException;
import exception.Business.NoSuchStateAction;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.FileNotFoundException;

import java.util.LinkedList;
import java.util.List;

public class BundleService implements IBundleService
{
	private IBundleRepoFile bundleRepoFile;
	private IBundleRepo     bundleRepo;
	private IBundleCache    bundleCache;

	private GroupVoters groupVoters = new GroupVoters(null);

	private final int   WINDOW       = 5;
	private final float CRITICAL_RES = 0.75f;

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
	public byte[] downloadReport(User initiator, Bundle client)
	{
		if(client.getState()==BundleState.EMPTY)
		{
			throw new BusinessException(
					new NoSuchStateAction(client.getState().toString()));
		}

		return bundleRepoFile.get(client);
	}

	@Override
	public Bundle uploadReport(User initiator, Bundle client, byte[] document)
	{
		if (client.getState() == BundleState.ACCEPTED)
		{
			throw new BusinessException(
					new NoSuchStateAction(client.getState().toString()));
		}
		Author rights = client.getRights(initiator);

		bundleRepoFile.save(client, document);
		Bundle bestMatchBundle = new Bundle();
		//написать алгоритм сверки
		List<Bundle> bundleList = null;
		try
		{
			bundleList = bundleRepo
					.get(client.getCourse(), client.getBundleType(), client.getNum());

		}
		catch (DataAccessException e)
		{
			client.accept();
			bundleRepo.save(client);
			return bestMatchBundle;
		}

		BuilderMatrix builderMatrix = new BuilderMeta(client, bundleList);
		groupVoters.setM(builderMatrix.buildMatrix());
		Matrix res = groupVoters.verdict();
		res.sortDesc(res.getWidth() - 1);

		bundleList = new LinkedList<>();
		Row rows[] = res.getRows();
		for (int i = 0; i < WINDOW && i < rows.length; i++)
		{
			bundleList.add((Bundle) rows[i].getObj());
		}

		bundleRepoFile.fillTextVector(bundleList);
		if(bundleList.size()==0)
		{
			throw new DataAccessException(new FileNotFoundException("Ошибка при чтении файлов анализа"));
		}

		builderMatrix = new BuilderWords(client, bundleList);
		res           = groupVoters.verdict(builderMatrix.buildMatrix());
		int methodsQuantity = res.getWidth();

		res.sortDesc(methodsQuantity - 1);

		bestMatchBundle = (Bundle) res.getRows()[0].getObj();
		float bestMatchScore = res.getRows()[0].getCortege()[methodsQuantity - 1];
		if (bestMatchScore <= CRITICAL_RES)
		{
			client.accept();
		}
		else
		{
			client.cancel();
		}
		bundleRepo.save(client);
		return bestMatchBundle;

	}

	@Override
	public void decline(Bundle client)
	{
		if(client.getState()!=BundleState.ACCEPTED)
		{
			throw new BusinessException(
					new NoSuchStateAction(client.getState().toString()));
		}
	}

	@Override
	public void delete(Bundle client)
	{

	}
}
