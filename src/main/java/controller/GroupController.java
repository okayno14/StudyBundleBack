package controller;

import business.IGroupService;
import controller.user.IUserController;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;
import dataAccess.repository.IGroupRepo;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;

import java.util.*;

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
		return service.get(id);
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

	private List<User> filterUserIDList(List<User> users)
	{
		IUserController userController = controller.getUserController();
		Iterator<User>  userIterator   = users.iterator();
		LinkedList<User> toAdd          = new LinkedList<>();
		while (userIterator.hasNext())
		{
			try
			{
				User user = userController.get(userIterator.next().getId());
				toAdd.add(user);
			}
			catch (DataAccessException e)
			{
				if (e.getCause().getClass() == ObjectNotFoundException.class)
				{
					//сделать логирование
				}
			}
		}
		if (toAdd.size() == 0)
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
		return toAdd;
	}

	@Override
	public void addUsers(Group client, List<User> users)
	{
		List<User> userList = filterUserIDList(users);
		service.addUsers(client, userList);
		controller.courseController.GroupChanged(client,userList);
	}

	@Override
	public Set<User> getUsers(Group client)
	{
		return service.getUsers(client);
	}

	@Override
	public void deleteUsers(List<User> users)
	{
		service.deleteUsers(filterUserIDList(users));
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
