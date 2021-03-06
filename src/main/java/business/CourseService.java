package business;

import dataAccess.cache.ICourseCache;
import dataAccess.cache.IRequirementCache;
import dataAccess.entity.*;
import dataAccess.repository.ICourseRepo;
import dataAccess.repository.IRequirementRepo;
import exception.Business.*;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CourseService implements ICourseService
{
	private ICourseRepo       repo;
	private ICourseCache      cache;
	private IRequirementRepo  reqRepo;
	private IRequirementCache reqCache;

	public CourseService(ICourseRepo repo, ICourseCache cache, IRequirementRepo reqRepo,
						 IRequirementCache reqCache)
	{
		this.repo     = repo;
		this.cache    = cache;
		this.reqRepo  = reqRepo;
		this.reqCache = reqCache;

		List<Requirement> list = reqRepo.get();
		for (Requirement req : list)
		{
			reqCache.put(req);
		}
	}

	@Override
	public void add(Course client)
	{
		repo.save(client);
		cache.put(client);
	}

	@Override
	public Course get(long id)
	{
		Course res;
		res = cache.get(id);
		if (res == null)
		{
			res = repo.get(id);
			cache.put(res);
		}
		return res;
	}

	@Override
	public List<Course> get(User owner, String name)
	{
		return null;
	}

	@Override
	public List<Course> getByOwner(User owner)
	{
		List<Course> res =  repo.getByOwner(owner);
		for(Course c:res)
		{
			cache.put(c);
		}
		return res;
	}

	@Override
	public List<Course> getByStudent(User student)
	{
		if(student.getGroup()==null)
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
		return getByGroup(student.getGroup());
	}

	@Override
	public List<Course> getByGroup(Group g)
	{
		List<Course> res = repo.getByGroup(g);
		for (Course c : res)
		{
			cache.put(c);
		}
		return res;
	}

	@Override
	public void addRequirement(User initiator, Course client, BundleType bt, int q)
	{
		if (client.getState() == CourseState.PUBLISHED)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		isInitiatorInACL(initiator, client);

		for (Requirement req : client.getRequirementSet())
		{
			if (req.getBundleType().equals(bt))
			{
				throw new BusinessException(new RequirementExistsException(bt.getName()));
			}
		}

		Requirement toSave = new Requirement(q, bt);
		if (!reqCache.contains(toSave))
		{
			reqRepo.save(toSave);
			reqCache.put(toSave);
		}
		else
		{
			Iterator<Requirement> iterator = reqCache.get().iterator();
			Requirement           obj      = new Requirement();
			while (iterator.hasNext() && !toSave.equals(obj))
			{
				obj = iterator.next();
			}
			toSave = obj;
		}
		client.addRequirement(toSave);
		repo.save(client);
	}

	@Override
	public Requirement getReq(long id)
	{
		Requirement res = reqCache.get(id);
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public void deleteRequirement(User initiator, Course client, Requirement toDel)
	{
		if (client.getState() == CourseState.PUBLISHED)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		isInitiatorInACL(initiator, client);

		if (!client.getRequirementSet().contains(toDel))
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
		LinkedList<Requirement> toDelList = new LinkedList<>();
		toDelList.add(toDel);
		List<Requirement> res = reqRepo.deleteNotLinked(toDelList);
		client.removeRequirement(toDel);
		if (res.size() != 0)
		{
			reqCache.delete(toDel.getId());
		}
		else
		{
			repo.save(client);
		}
	}

	@Override
	public void publish(User initiator, Course client)
	{
		if (client.getState() == CourseState.IN_PROGRESS)
		{
			isInitiatorInACL(initiator, client);

			client.publish();
			repo.save(client);
		}
	}

	@Override
	public void addGroup(User initiator, Course client, Group group)
	{
		if (client.getState() != CourseState.PUBLISHED)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		isInitiatorInACL(initiator, client);
		if (client.contains(group))
		{
			throw new BusinessException(new GroupAlreadyContains(group, client));
		}
		client.addGroup(group);
		repo.save(client);
	}

	@Override
	public void delGroup(User initiator, Course client, Group group)
	{
		if (client.getState() != CourseState.PUBLISHED)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		isInitiatorInACL(initiator, client);
		client.removeGroup(group);
		repo.save(client);
	}

	@Override
	public void updateName(User initiator, Course client, String name)
	{
		if (client.getState() == CourseState.PUBLISHED)
		{
			throw new BusinessException(new NoSuchStateAction(client.getState().toString()));
		}
		isInitiatorInACL(initiator, client);
	}

	@Override
	public void delete(User initiator, List<Course> clientList)
	{
		HashSet<Requirement> requirementSet = new HashSet<>();
		for (Course c : clientList)
		{
			isInitiatorAUTHOR(initiator, c);
			requirementSet.addAll(c.getRequirementSet());
		}

		LinkedList<Requirement> reqToDel = new LinkedList<>(requirementSet);

		List<Requirement> requirementList = reqRepo.deleteNotLinked(reqToDel);

		for (Requirement req : requirementList)
		{
			reqCache.delete(req.getId());
		}

		repo.delete(clientList);
		for (Course c : clientList)
		{
			cache.delete(c.getId());
		}
	}

	private void isInitiatorInACL(User initiator, Course client) throws BusinessException
	{
		Author rights = client.getRights(initiator);
	}

	private void isInitiatorAUTHOR(User initiator, Course client) throws BusinessException
	{
		User author = client.getAuthor();
		if (!initiator.equals(author))
		{
			throw new BusinessException(new NoRightException());
		}
	}
}
