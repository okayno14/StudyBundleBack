package controller;

import business.ICourseService;
import dataAccess.entity.*;
import exception.Business.BusinessException;
import exception.Business.NoRightException;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CourseController implements ICourseController
{
	private Controller     controller;
	private ICourseService service;

	public CourseController(Controller controller, ICourseService courseService)
	{
		this.controller = controller;
		this.service    = courseService;
	}

	@Override
	public void add(Course client)
	{
		service.add(client);
	}

	@Override
	public Course get(long id)
	{
		return service.get(id);
	}

	@Override
	public List<Course> get(User owner, String name)
	{
		return null;
	}

	@Override
	public List<Course> getByOwner(User owner)
	{
		return service.getByOwner(owner);
	}

	@Override
	public List<Course> getByStudent(User student)
	{
		return service.getByStudent(student);
	}

	@Override
	public List<Course> getByGroup(Group g)
	{
		return service.getByGroup(g);
	}

	private void genBundlesForReq(List<Bundle> list, Requirement req, Course client, User u)
	{
		for (int i = 1; i <= req.getQuantity(); i++)
		{
			Bundle b = new Bundle(i, client, req.getBundleType());
			b.addACE(u, Author.AUTHOR);
			list.add(b);
		}
	}

	@Override
	public void addRequirement(User initiator, Course client, BundleType bt, int q)
	{
		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator = client.getAuthor();
		}
		service.addRequirement(initiator, client, bt, q);
	}

	@Override
	public Requirement getReq(long id)
	{
		return service.getReq(id);
	}

	@Override
	public void deleteRequirement(User initiator, Course client, Requirement req)
	{
		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator = client.getAuthor();
		}
		service.deleteRequirement(initiator, client, req);
	}

	@Override
	public void publish(User initiator, Course client)
	{
		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator = client.getAuthor();
		}
		service.publish(initiator, client);
	}

	//@Override
	private List<Bundle> genBundlesForUser(Course client, User u)
	{
		LinkedList<Bundle> list = new LinkedList<>();
		for (Requirement req : client.getRequirementSet())
		{
			genBundlesForReq(list, req, client, u);
		}
		return list;
	}

	@Override
	public void addGroup(User initiator, Course client, Group g)
	{
		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator = client.getAuthor();
		}
		service.addGroup(initiator, client, g);
		LinkedList<Bundle> list = new LinkedList<>();
		List<User> userList = new LinkedList<User>(controller.groupController.getUsers(g));
		Set<User> userWithBundles = controller.userController.filter(userList,client);
		for (User u : userList)
		{
			if(!userWithBundles.contains(u))
			{
				list.addAll(genBundlesForUser(client, u));
			}
		}
		controller.bundleController.add(list);
	}

	@Override
	public void GroupChanged(Group g, List<User> userList)
	{
		HashSet<Course> curGroupCourses = new HashSet<>();
		List<Bundle> list = new LinkedList<>();
		try
		{
			curGroupCourses = new HashSet<>(getByGroup(g));
		}
		catch (DataAccessException e)
		{
		}
		for (User u : userList)
		{
			HashSet<Course> oldGroupCourses = new HashSet<>();
			try
			{
				List<Bundle> bundleList = controller.bundleController.getAll(u);
				for (Bundle b : bundleList)
				{
					oldGroupCourses.add(b.getCourse());
					if (b.getState() != BundleState.EMPTY)
					{
						controller.bundleController.groupChanged(b);
						list.add(b);
					}
				}
			}
			catch (DataAccessException e)
			{
				if (!(e.getCause().getClass() == ObjectNotFoundException.class))
				{
					throw e;
				}
			}
			//выполняем вычитание множеств
			curGroupCourses.removeAll(oldGroupCourses);
			if (curGroupCourses.size() == 0)
			{
				return;
			}
			for (Course c : curGroupCourses)
			{
				for (Requirement req : c.getRequirementSet())
				{
					genBundlesForReq(list, req, c, u);
				}
			}
			controller.bundleController.add(list);
		}
	}

	@Override
	public void delGroup(User initiator, Course client, Group group)
	{
		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator = client.getAuthor();
		}
		List<Group> groupList = new LinkedList<>();
		groupList.add(group);
		List<Course> courseList = new LinkedList<>();
		courseList.add(client);
		controller.bundleController.groupMovedFromCourse(initiator,courseList,groupList);
		service.delGroup(initiator, client, group);
	}

	@Override
	public void updateName(User initiator, Course client, String name)
	{
		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator = client.getAuthor();
		}
	}

	@Override
	public void delete(User initiator, List<Course> clientList)
	{
		//это делать может только автор
		HashSet<User> authors = new HashSet<>();
		HashSet<Group> groups = new HashSet<>();
		for(Course c:clientList)
		{
			authors.add(c.getAuthor());
			groups.addAll(c.getGroupes());
		}

		//составлен перечень пользователей откуда можно взять замену для инициатора
		//поэтому можно заранее бросить исключение
		if(!(initiator.getRole().getId()==controller.roleController.getAdmin().getId()))
		{
			//Если инициатор не является администратором, то тогда нужно проверить условие:
			//является ли инициатор автором каждого из полученных курсов
			if(authors.size()!=1 || authors.iterator().next().getId()!=initiator.getId())
			{
				throw new BusinessException(new NoRightException());
			}
		}

		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator =authors.iterator().next();
		}

		List<Group> groupList = new LinkedList(groups);
		controller.bundleController.groupMovedFromCourse(initiator,clientList,groupList);

		for(Course c: clientList)
		{
			service.delete(initiator, clientList);
		}
	}
}
