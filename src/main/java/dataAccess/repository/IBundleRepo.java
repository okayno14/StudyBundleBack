package dataAccess.repository;

import dataAccess.entity.Bundle;
import dataAccess.entity.Course;
import dataAccess.entity.User;

import java.util.List;
import java.util.Set;

public interface IBundleRepo
{
	void save(Bundle bundle);
	void save(List<Bundle> bundles);
	Bundle get(long id);
	List<Bundle> get(String courseName, String groupName, User fio);
	List<Bundle> get(Course course, User user);
	List<Bundle> getAll(User user);
	void delete(Bundle bundle);
}
