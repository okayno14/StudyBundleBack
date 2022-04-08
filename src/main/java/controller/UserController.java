package controller;

import business.IUserService;

public class UserController implements IUserController
{
	private Controller controller;
	private IUserService userService;

	public UserController(Controller controller, IUserService userService)
	{
		this.controller  = controller;
		this.userService = userService;
	}
}
