package dataAccess.entity;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "\"group\"")
public class Group implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Expose
	private long      id = -1;
	@Expose
	private String    name;
	@OneToMany(mappedBy = "group",
			   cascade = CascadeType.REMOVE)
	private Set<User> students;

	public Group()
	{
	}

	public Group(String name)
	{
		this.name = name;
		students  = new HashSet<User>();
	}

	public void addStudent(User user)
	{
		if (students.contains(user))
		{
			return;
		}
		students.add(user);
		user.setGroup(this);
	}

	public void removeStudent(User user)
	{
		if (!students.contains(user))
		{
			return;
		}
		students.remove(user);
		user.setGroup(null);
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
		Group group = (Group) o;
		return id == group.id;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Set<User> getStudents()
	{
		//return new HashSet<User>(students);
		return students;
	}

	public void setStudents(Set<User> students)
	{
		this.students = students;
	}
}
