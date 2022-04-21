package business;

import dataAccess.cache.ICourseCache;
import dataAccess.cache.IRequirementCache;
import dataAccess.entity.BundleType;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;
import dataAccess.repository.ICourseRepo;
import dataAccess.repository.IRequirementRepo;

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
		return null;
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
	public void addRequirement(BundleType bt, int q)
	{

	}

	@Override
	public void updateRequirement(BundleType bt, int q)
	{

	}

	@Override
	public void deleteRequirement(BundleType bt, int q)
	{

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
