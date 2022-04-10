package controller;

import business.IUserService;
import dataAccess.entity.User;

public class UserController implements IUserController
{
	private Controller controller;
	private IUserService userService;

	public UserController(Controller controller, IUserService userService)
	{
		this.controller  = controller;
		this.userService = userService;
	}

	//пустышка
	@Override
	public User get(long id)
	{
		return null;
	}
}
