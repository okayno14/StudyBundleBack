package business;

import dataAccess.entity.Role;
import dataAccess.entity.User;

import java.util.List;

public interface IUserService
{
	void add(User client);
	boolean login(String token, String email, String pass);
	void logout(String token);
	User get(long id);
	User get(String email);
	User getByToken(String token);
	User get(User fio, Role role);
	List<User> getByCourse(User fio, String courseName);
	void updateFIO(User client, User fio);
	void updatePass(User client, String pass);
	void updateMail(User client, String email);
	void forgotPass(User client, String email);
	void resetPass(String passHash, long id);
	void confirm(String email);
	void activate(long id);
	void delete(User client);
}
