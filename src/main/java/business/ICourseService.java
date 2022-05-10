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
	void addRequirement(User initiator, Course client, BundleType bt, int q);
	void deleteRequirement(User initiator, Course client, BundleType bt, int q);
	void publish(User initiator, Course client);
	void addGroup(User initiator, Course client, Group group);
	void delGroup(User initiator, Course client, Group group);
	void updateName(User initiator, Course client, String name);
	void delete(User initiator, Course client);
}
