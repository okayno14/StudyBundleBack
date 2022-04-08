package controller;

import business.IRoleService;

public class RoleController implements IRoleController
{
	private Controller controller;
	private IRoleService roleService;

	public RoleController(Controller controller, IRoleService roleService)
	{
		this.controller  = controller;
		this.roleService = roleService;
	}
}
