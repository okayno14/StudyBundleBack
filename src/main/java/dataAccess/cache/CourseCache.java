package dataAccess.cache;

import dataAccess.entity.Course;

import java.util.HashMap;
import java.util.Map;

public class CourseCache implements ICourseCache
{
	private Map<Long, Course> cache = new HashMap<>();
	private CacheController cacheController;

	public CourseCache(CacheController cacheController)
	{
		this.cacheController = cacheController;
	}

	@Override
	public boolean contains(long id)
	{
		return cache.containsKey(id);
	}

	@Override
	public boolean isEmpty()
	{
		return cache.isEmpty();
	}

	@Override
	public Course get(long id)
	{
		return cache.get(id);
	}

	@Override
	public void put(Course course)
	{
		cache.put(course.getId(),course);
		cacheController.added(course);
	}

	@Override
	public void delete(long id)
	{
		cache.remove(id);
	}

	@Override
	public void clean()
	{
		cache.clear();
	}
}
