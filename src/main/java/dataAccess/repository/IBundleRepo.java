package dataAccess.repository;

import dataAccess.entity.*;

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
	List<Bundle> get(Course course, BundleType bt, int num, List<Bundle> cachedBundles);
	void delete(Bundle bundle);
	//возвращает список удалённых бандлов
	List<Bundle> delete(List<Course> courseList, List<Group> groupList);
	List<Bundle> delete(User user);
}
