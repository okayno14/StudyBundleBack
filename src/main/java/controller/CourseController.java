package controller;

import business.ICourseService;
import dataAccess.entity.*;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
		return null;
	}

	@Override
	public List<Course> getByStudent(User student)
	{
		return null;
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
			b.addAuthor(u, Author.AUTHOR);
			list.add(b);
		}
	}

	@Override
	public Requirement addRequirement(Course client, BundleType bt, int q)
	{
		Requirement        req  = service.addRequirement(client, bt, q);
		LinkedList<Bundle> list = new LinkedList<>();
		for (Group g : client.getGroupes())
		{
			for (User u : controller.groupController.getUsers(g))
			{
				genBundlesForReq(list, req, client, u);
			}
		}
		controller.bundleController.add(list);
		return req;
	}

	@Override
	public void updateRequirement(Course client, BundleType bt, int q)
	{

	}

	@Override
	public void deleteRequirement(Course client, BundleType bt, int q)
	{

	}

	@Override
	public List<Bundle> genBundlesForUser(Course client, User u)
	{
		LinkedList<Bundle> list = new LinkedList<>();
		for (Requirement req : client.getRequirementSet())
		{
			genBundlesForReq(list, req, client, u);
		}
		return list;
	}

	@Override
	public void addGroup(Course client, Group g)
	{
		service.addGroup(client, g);
		LinkedList<Bundle> list = new LinkedList<>();
		for (User u : controller.groupController.getUsers(g))
		{
			list.addAll(genBundlesForUser(client, u));
		}
		controller.bundleController.add(list);
	}

	@Override
	public void GroupChanged(Group g, List<User> userList)
	{
		HashSet<Course> curGroupCourses = new HashSet<>();
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
				List<Bundle> bundleList = controller.bundleController
						.getAll(u);
				for (Bundle b : bundleList)
				{
					oldGroupCourses.add(b.getCourse());
					if (b.getState() != BundleState.EMPTY)
					{
						controller.bundleController.groupChanged(b);
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
			List<Bundle> list = new LinkedList<>();
			for (Course c : curGroupCourses)
			{
				for (Requirement req : c.getRequirementSet())
				{
					genBundlesForReq(list,req,c,u);
				}
			}
		}
	}

	@Override
	public void delGroup(Course client, Group group)
	{

	}

	@Override
	public void updateName(Course client, String name)
	{

	}

	@Override
	public void delete(Course client)
	{

	}
}
