package controller;

import business.IRoleService;
import dataAccess.entity.Role;

import java.util.List;

public class RoleController implements  IRoleController
{
	private Controller controller;
	private IRoleService roleService;

	public RoleController(Controller controller, IRoleService roleService)
	{
		this.controller  = controller;
		this.roleService = roleService;
	}

	@Override
	public List<Role> get()
	{
		return roleService.get();
	}

	@Override
	public Role getGuest()
	{
		return roleService.getGuest();
	}
}
