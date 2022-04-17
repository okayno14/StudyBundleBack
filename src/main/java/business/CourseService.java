package business;

import dataAccess.cache.ICourseCache;
import dataAccess.cache.IRequirementCache;
import dataAccess.entity.Course;
import dataAccess.repository.ICourseRepo;
import dataAccess.repository.IRequirementRepo;

public class CourseService implements ICourseService
{
	private ICourseRepo repo;
	private ICourseCache cache;
	private IRequirementRepo reqRepo;
	private IRequirementCache reqCache;
	private Course client;

	public CourseService(ICourseRepo repo, ICourseCache cache, IRequirementRepo reqRepo,
						 IRequirementCache reqCache)
	{
		this.repo     = repo;
		this.cache    = cache;
		this.reqRepo  = reqRepo;
		this.reqCache = reqCache;
	}

	public Course getClient()
	{
		return client;
	}

	public void setClient(Course client)
	{
		this.client = client;
	}
}
