package controller;

import business.ICourseService;

public class CourseController implements ICourseController
{
	private Controller controller;
	private ICourseService courseService;

	public CourseController(Controller controller, ICourseService courseService)
	{
		this.controller    = controller;
		this.courseService = courseService;
	}
}
