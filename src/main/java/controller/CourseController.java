package controller;

import business.ICourseService;
import dataAccess.entity.BundleType;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;

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
		return null;
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
	public void addRequirement(BundleType bt, int q)
	{

	}

	@Override
	public void updateRequirement(BundleType bt, int q)
	{

	}

	@Override
	public void deleteRequirement(BundleType bt, int q)
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
