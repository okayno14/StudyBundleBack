package dataAccess.entity;

import com.google.gson.annotations.Expose;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "course_acl")
@TypeDef(name="pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class CourseACL implements Serializable
{
	@EmbeddedId
	private CourseACLID id;
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("courseID")
	private  Course      course;
	@ManyToOne
	@MapsId("userID")
	@Expose
	private User         user;
	@Enumerated(EnumType.STRING)
	@Column(name = "rights")
	@Type(type="pgsql_enum")
	@Expose
	private Author       rights;

	public CourseACL()
	{
	}

	public CourseACL(Course course, User user, Author rights)
	{
		this.course = course;
		this.user   = user;
		this.id=new CourseACLID(course.getId(), user.getId());
		this.rights=rights;
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
		CourseACL courseACL = (CourseACL) o;
		return course.equals(courseACL.course) && user.equals(courseACL.user);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(course, user);
	}

	public CourseACLID getId()
	{
		return id;
	}

	public void setId(CourseACLID id)
	{
		this.id = id;
	}

	public Course getCourse()
	{
		return course;
	}

	public void setCourse(Course course)
	{
		this.course = course;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public Author getRights()
	{
		return rights;
	}

	public void setRights(Author rights)
	{
		this.rights = rights;
	}
}
