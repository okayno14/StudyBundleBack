package business;

import dataAccess.entity.*;

import java.util.List;

public interface ICourseService
{
	void add(Course client);
	Course get(long id);
	List<Course> get(User owner, String name);
	List<Course> getByOwner(User owner);
	List<Course> getByStudent(User student);
	List<Course> getByGroup(Group g);
	Requirement addRequirement(Course client, BundleType bt, int q);
	void updateRequirement(Course client, BundleType bt, int q);
	void deleteRequirement(Course client, BundleType bt, int q);
	void addGroup(Course client, Group group);
	void delGroup(Course client, Group group);
	void updateName(Course client, String name);
	void delete(Course client);
}
