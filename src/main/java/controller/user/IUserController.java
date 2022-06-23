package controller.user;

import business.IUserService;
import dataAccess.entity.User;

import java.util.List;

public interface IUserController extends IUserService
{
	void add(List<User> userList);
	boolean contains(String token);
	User getByToken(String token);
	void logoutAll();
	void delete(List<User> userList);
	User getGuestUser();
}
