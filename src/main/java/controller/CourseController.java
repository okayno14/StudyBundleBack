package controller;

import business.ICourseService;
import dataAccess.entity.*;

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
	public Requirement addRequirement(Course client, BundleType bt, int q)
	{
		Requirement req= service.addRequirement(client, bt, q);
		LinkedList<Bundle> list = new LinkedList<>();
		for(Group g:client.getGroupes())
		{
			controller.groupController.getUsers(g);
			for(User u: g.getStudents())
			{
				for(int i=0;i<q;i++)
				{
					Bundle b = new Bundle(i,client,req.getBundleType());
					b.addAuthor(u,Author.AUTHOR);
					list.add(b);
				}
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
	public void subscribe(Group group)
	{

	}

	@Override
	public void unsubscribe(Group group)
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
