package dataAccess.cache;

import dataAccess.entity.Course;
import dataAccess.entity.Requirement;

import java.util.List;

public interface ICourseCache
{
	boolean contains(long id);
	boolean isEmpty();
	Course get(long id);
	void put(Course course);
	void delete(long id);
	void clean();
}
