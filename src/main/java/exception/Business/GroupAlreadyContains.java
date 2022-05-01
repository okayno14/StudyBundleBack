package exception.Business;

import dataAccess.entity.Course;
import dataAccess.entity.Group;

public class GroupAlreadyContains extends RuntimeException
{
	private Group  g;
	private Course c;

	public GroupAlreadyContains(Group g, Course c)
	{
		this.g = g;
		this.c = c;
	}

	@Override
	public String getMessage()
	{
		return "Группа "+g.getName()+" уже подписана на курс " + c.getName();
	}
}
