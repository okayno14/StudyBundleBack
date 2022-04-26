package business;

import dataAccess.cache.ICourseCache;
import dataAccess.cache.IRequirementCache;
import dataAccess.entity.*;
import dataAccess.repository.ICourseRepo;
import dataAccess.repository.IRequirementRepo;
import exception.Business.BusinessException;
import exception.Business.RequirementExistsException;

import java.util.Iterator;
import java.util.List;

public class CourseService implements ICourseService
{
	private ICourseRepo repo;
	private ICourseCache cache;
	private IRequirementRepo reqRepo;
	private IRequirementCache reqCache;

	public CourseService(ICourseRepo repo, ICourseCache cache, IRequirementRepo reqRepo,
						 IRequirementCache reqCache)
	{
		this.repo     = repo;
		this.cache    = cache;
		this.reqRepo  = reqRepo;
		this.reqCache = reqCache;

		List<Requirement> list = reqRepo.get();
		for(Requirement req:list)
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
		res=cache.get(id);
		if(res==null)
		{
			res=repo.get(id);
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
		return null;
	}

	@Override
	public List<Course> getByStudent(User student)
	{
		return null;
	}

	@Override
	public void addRequirement(Course client, BundleType bt, int q)
	{
		//Проверить есть ли требование с этим bt в курсе. Если есть, то исключение
		//Проверить есть ли такое требование в системе. Если есть, то добавим к курсу этот объект
		//Если нет, то создадим новое

		for(Requirement req: client.getRequirementSet())
		{
			if(req.getBundleType().equals(bt))
			{
				throw new BusinessException(new RequirementExistsException(bt.getName()));
			}
		}

		Requirement req = new Requirement(q,bt);
		if(reqCache.contains(req))
		{
			for(Requirement i: reqCache.get())
			{
				if(i.equals(req))
				{
					req=i;
				}
			}
		}
		else
		{
			reqCache.put(req);
		}
		client.addRequirement(req);
		reqRepo.save(req);
		repo.save(client);
	}

	@Override
	public void updateRequirement(Course client, BundleType bt, int q)
	{
		//найти старое требование
		//посмотреть в базе количество ссылок на него
		//если 1, то сохранить изменения старого требования в базе
		//если больше, то отвязать от старого, создать новый объект и сохранить его в базу
	}

	@Override
	public void deleteRequirement(Course client, BundleType bt, int q)
	{
		//посмотреть в базе количество ссылок на него
		//если 1, то удалить старый объект из базы
		//если больше, то отвязать от старого
	}

	@Override
	public void subscribe(Group group)
	{

	}

	@Override
	public void unsubscribe(Group group)
	{

	}

	@Override
	public void updateName(Course client, String name)
	{

	}

	@Override
	public void delete(Course client)
	{

	}
}
