package dataAccess.repository;

import dataAccess.entity.Group;
import dataAccess.entity.User;

import java.util.List;

public interface IGroupRepo
{
	void save(Group group);
	Group get(long id);
	List<Group> get(String groupName);
	void save(List<User> users);
	boolean isStudentsFetched(Group group);
	void fetchStudents(Group group);
	void delete(Group group);
}
