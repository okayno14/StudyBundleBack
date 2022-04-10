package dataAccess.repository;

import dataAccess.entity.Course;
import dataAccess.entity.User;

import java.util.List;

public interface ICourseRepo
{
	void save(Course course);
	Course get(long id);
	List<Course> get(User owner, String name);
	List<Course> getByOwner(User owner);
	List<Course> getByStudent(User student);
	void delete(Course course);
}
