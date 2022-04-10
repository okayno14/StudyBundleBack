package business;

import dataAccess.cache.ICourseCache;
import dataAccess.entity.Course;
import dataAccess.repository.ICourseRepo;

public class CourseService implements ICourseService
{
	private ICourseRepo repo;
	private ICourseCache cache;
	private Course client;

	public CourseService(ICourseRepo repo, ICourseCache cache)
	{
		this.repo  = repo;
		this.cache = cache;
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
