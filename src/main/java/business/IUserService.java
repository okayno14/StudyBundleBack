package business;

import dataAccess.entity.Role;
import dataAccess.entity.User;

import java.util.List;

public interface IUserService
{
	void add();
	boolean login(String token, String email, String pass);
	void logout(String token);
	User get(long id);
	User get(String email);
	User getByToken(String token);
	User get(User fio, Role role);
	User getByGroup(User fio, String groupName);
	List<User> getByCourse(User fio, String courseName);
	void updateFIO(User fio);
	void updatePass(String pass);
	void updateMail(String email);
	void forgotPass(String email);
	void resetPass(String passHash, long id);
	void confirm(String email);
	void activate(long id);
	void delete();
	void setClient(User user);
}
