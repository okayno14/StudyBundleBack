package controller;

import business.IGroupService;

public class GroupController implements IGroupController
{
	private Controller controller;
	private IGroupService groupService;

	public GroupController(Controller controller, IGroupService groupService)
	{
		this.controller   = controller;
		this.groupService = groupService;
	}
}
