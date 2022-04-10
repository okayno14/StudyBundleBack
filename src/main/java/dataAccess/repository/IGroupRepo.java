package dataAccess.repository;

import dataAccess.entity.Group;

import java.util.List;

public interface IGroupRepo
{
	void save(Group group);
	Group get(long id);
	List<Group> get(String groupName);
	boolean isStudentsLazy(Group group);
	void fetchStudents(Group group);
	void delete(Group group);
}
