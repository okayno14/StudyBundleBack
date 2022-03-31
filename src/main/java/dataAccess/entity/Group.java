package dataAccess.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="\"group\"")
public class Group implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int       id = -1;
	private String    name;
	@OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)

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

	public int getId()
	{
		return id;
	}

	public void setId(int id)
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
		return new HashSet<User>(students);
	}

	public void setStudents(Set<User> students)
	{
		this.students = students;
	}
}
