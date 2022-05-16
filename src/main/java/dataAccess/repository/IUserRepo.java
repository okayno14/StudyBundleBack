package dataAccess.repository;

import dataAccess.entity.Course;
import dataAccess.entity.User;

import java.util.List;
import java.util.Set;

public interface IUserRepo
{
	void save(User user);
	User get(long id);
	User get(String email);
	User get(String email, String pass);
	User get(User fio);
	User getByGroup(User fio, String groupName);
	List<User> getByCourse(User fio, String courseName);
	Set<User> filter(List<User> userList, Course c);
	void delete(User user);
}
