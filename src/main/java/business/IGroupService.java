package business;

import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;
import dataAccess.repository.IGroupRepo;

import java.util.List;
import java.util.Set;

public interface IGroupService
{
	//Никто кроме админа не имеет права вызывать методы данного интерфейса
	//поэтому здесь нет смысла указывать инициатора
	void add(Group client);
	Group get(long id);
	List<Group> get(String groupName);
	Set<Group> get(Course course);
	void addUsers(Group client, List<User> users);
	Set<User> getUsers(Group client);
	void deleteUsers(List<User> users);
	void delete(Group client);

	IGroupRepo getRepo();
}
