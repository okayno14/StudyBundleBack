package controller;

import business.IUserService;
import dataAccess.entity.User;

import java.util.List;

public interface IUserController extends IUserService
{
	void add(List<User> userList);
	void delete(List<User> userList);
	User getGuestUser();
}
