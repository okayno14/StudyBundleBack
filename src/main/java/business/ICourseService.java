package business;

import dataAccess.entity.BundleType;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;

import java.util.List;

public interface ICourseService
{
	void add(Course client);
	Course get(long id);
	List<Course> get(User owner, String name);
	List<Course> getByOwner(User owner);
	List<Course> getByStudent(User student);
	void addRequirement(BundleType bt, int q);
	void updateRequirement(BundleType bt, int q);
	void deleteRequirement(BundleType bt, int q);
	void subscribe(Group group);
	void unsubscribe(Group group);
	void updateName(Course client, String name);
	void delete(Course client);
}
