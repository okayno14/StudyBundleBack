package dataAccess.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CourseACLID implements Serializable
{
	private long courseID = -1;
	private long userID = -1;

	public CourseACLID()
	{
	}

	public CourseACLID(long courseID, long userID)
	{
		this.courseID = courseID;
		this.userID   = userID;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		CourseACLID courseACLID = (CourseACLID) o;
		return courseID == courseACLID.courseID && userID == courseACLID.userID;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(courseID, userID);
	}

	public long getCourseID()
	{
		return courseID;
	}

	public void setCourseID(long courseID)
	{
		this.courseID = courseID;
	}

	public long getUserID()
	{
		return userID;
	}

	public void setUserID(long userID)
	{
		this.userID = userID;
	}
}
