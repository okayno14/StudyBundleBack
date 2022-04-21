package controller;

import business.IGroupService;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;
import dataAccess.repository.IGroupRepo;

import java.util.List;
import java.util.Set;

public class GroupController implements IGroupController
{
	private Controller    controller;
	private IGroupService service;

	public GroupController(Controller controller, IGroupService groupService)
	{
		this.controller = controller;
		this.service    = groupService;
	}

	@Override
	public void add(Group client)
	{
		service.add(client);
	}

	@Override
	public Group get(long id)
	{
		return null;
	}

	@Override
	public List<Group> get(String groupName)
	{
		return null;
	}

	@Override
	public Set<Group> get(Course course)
	{
		return null;
	}

	@Override
	public void addUsers(List<User> users)
	{

	}

	@Override
	public Set<User> getUsers(Group client)
	{
		return null;
	}

	@Override
	public void deleteUsers(List<User> users)
	{

	}

	@Override
	public void delete(Group client)
	{
		service.delete(client);
	}

	@Override
	public IGroupRepo getRepo()
	{
		return service.getRepo();
	}
}
