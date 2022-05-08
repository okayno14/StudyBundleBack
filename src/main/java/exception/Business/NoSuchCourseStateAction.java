package exception.Business;

import dataAccess.entity.CourseState;

public class NoSuchCourseStateAction extends RuntimeException
{
	private CourseState courseState;
	private String message;

	public NoSuchCourseStateAction(CourseState courseState)
	{
		this.courseState = courseState;
		this.message     = "Операция недопустима для состояния "+ courseState.toString();
	}
}
