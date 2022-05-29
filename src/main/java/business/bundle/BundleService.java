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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class BundleService implements IBundleService
{
	private IBundleRepoFile bundleRepoFile;
	private IBundleRepo     bundleRepo;
	private IBundleCache    bundleCache;

	private GroupVoters groupVoters = new GroupVoters(null);

	private final int   WINDOW;
	private final float WORD_ANALYSIS_CRITICAL_VAL;

	private final static Logger logger = LoggerFactory.getLogger(BundleService.class);

	public BundleService(IBundleRepoFile bundleRepoFile, IBundleRepo bundleRepo,
						 IBundleCache bundleCache, int WINDOW, float WORD_ANALYSIS_CRITICAL_VAL)
	{
		this.bundleRepoFile = bundleRepoFile;
		this.bundleRepo     = bundleRepo;
		this.bundleCache    = bundleCache;

		this.WINDOW                     = WINDOW;
		this.WORD_ANALYSIS_CRITICAL_VAL = WORD_ANALYSIS_CRITICAL_VAL;
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
		List<Bundle> res = bundleRepo.get(course,author);
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
		Bundle dir = new Bundle(client.getNum(), client.getCourse(), client.getBundleType());
		dir.addACE(client.getAuthor(), Author.AUTHOR);
		bundleRepoFile.moveGroupChanged(client, dir.getFolder());
	}

	@Override
	public void groupMovedFromCourse(User initiator, List<Course> courseList, List<Group> groupList)
	{
		for (Course c : courseList)
		{
			isInitiatorINCourseACL(initiator, c);
		}
		List<Bundle> bundleList = bundleRepo.delete(courseList, groupList);

		for (Bundle b : bundleList)
		{
			bundleCache.delete(b.getId());
		}
	}

	@Override
	public byte[] downloadReport(User initiator, Bundle client)
	{
		if (client.getState() == BundleState.EMPTY)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		isInitiatorInBundleACL_OR_InCourseACL(initiator, client);

		return bundleRepoFile.get(client);
	}

	@Override
	public Bundle uploadReport(User initiator, Bundle client, byte[] document)
	{
		if (client.getState() == BundleState.ACCEPTED)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		isInitiatorInBundleACL_OR_InCourseACL(initiator, client);
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
			logger.trace("Бандл id = {} принят,  работа была загружена первой", client.getId());
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
		if (bundleList.size() == 0)
		{
			throw new DataAccessException(
					new FileNotFoundException("Ошибка при чтении файлов анализа"));
		}

		builderMatrix = new BuilderWords(client, bundleList);
		res           = groupVoters.verdict(builderMatrix.buildMatrix());
		int methodsQuantity = res.getWidth();

		res.sortDesc(methodsQuantity - 1);

		bestMatchBundle = (Bundle) res.getRows()[0].getObj();
		float bestMatchScore = res.getRows()[0].getCortege()[methodsQuantity - 1];
		bestMatchBundle.setOriginality(bestMatchScore);
		if (bestMatchScore <= WORD_ANALYSIS_CRITICAL_VAL)
		{
			client.accept();
			logger.trace("Бандл id = {} принят,  результат={}, максимальное сходство с id={}",
						 client.getId(), bestMatchScore, bestMatchBundle.getId());

		}
		else
		{
			client.cancel();
			logger.trace("Бандл id = {} отклонён,  результат={}, максимальное сходство с id={}",
						 client.getId(), bestMatchScore, bestMatchBundle.getId());
		}
		bundleRepo.save(client);
		return bestMatchBundle;
	}

	@Override
	public void accept(User initiator, Bundle client)
	{
		if (client.getState() != BundleState.CANCELED)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		//может только автор курса
		isInitiatorINCourseACL(initiator, client.getCourse());
		client.accept();
		bundleRepo.save(client);
	}

	@Override
	public void cancel(User initiator, Bundle client)
	{
		if (client.getState() != BundleState.ACCEPTED)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		//может только автор курса
		isInitiatorINCourseACL(initiator, client.getCourse());
		client.cancel();
		bundleRepo.save(client);
	}

	@Override
	public void emptify(User initiator, Bundle client)
	{
		//удаление - удаление файла с диска
		//действие допустимо как в состоянии ACCEPTED, так и CANCELED
		if (client.getState().equals(BundleState.EMPTY))
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		//имеет право только автор курса
		isInitiatorINCourseACL(initiator, client.getCourse());

		bundleRepoFile.delete(client);
		client.setState(BundleState.EMPTY);
		bundleRepo.save(client);
	}

	@Override
	public void delete(User initiator, User target)
	{
		if (initiator.getId() != target.getId())
		{
			throw new BusinessException(new NoRightException());
		}
		List<Bundle> bundleList = bundleRepo.delete(target);
		for (Bundle b : bundleList)
		{
			bundleCache.delete(b.getId());
		}
	}

	private void isInitiatorInACL(User initiator, Bundle client) throws BusinessException
	{
		Author rights = client.getRights(initiator);
	}

	private void isInitiatorAUTHOR(User initiator, Bundle client) throws BusinessException
	{
		User courseAuthor = client.getAuthor();
		if (!initiator.equals(courseAuthor))
		{
			throw new BusinessException(new NoRightException());
		}
	}

	private void isInitiatorINCourseACL(User initiator, Course client) throws BusinessException
	{
		client.getRights(initiator);
	}

	private void isInitiatorInBundleACL_OR_InCourseACL(User initiator, Bundle client)
			throws BusinessException
	{
		try
		{
			isInitiatorInACL(initiator, client);
		}
		catch (BusinessException e)
		{
			isInitiatorINCourseACL(initiator, client.getCourse());
		}
	}
}
