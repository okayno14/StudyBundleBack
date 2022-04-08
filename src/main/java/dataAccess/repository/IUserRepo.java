package dataAccess.repository;

import dataAccess.entity.User;

import java.util.List;

public interface IUserRepo
{
	void save(User user);
	User get(long id);
	User get(String email);
	User get(String email, String pass);
	User get(User fio);
	User getByGroup(User fio, String groupName);
	List<User> getByCourse(User fio, String courseName);
	void delete(User user);
}
