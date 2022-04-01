package dataAccess.entity;

import exception.NoRightException;

import javax.persistence.*;
import java.util.*;

@Entity
public class Course
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long            id             = -1;
	private String          name;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "group_course",
			   joinColumns = {@JoinColumn(name = "id_course")},
			   inverseJoinColumns = {@JoinColumn(name = "id_group")})
	private Set<Group>      groupes        = new HashSet<Group>();
	@OneToMany(mappedBy = "course",
			   fetch = FetchType.EAGER,
			   cascade = CascadeType.ALL)
	private List<CourseACL> courseACL_List = new ArrayList<CourseACL>();
	@ManyToMany(fetch = FetchType.EAGER,
				cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinTable(name = "requirement_course",
			   joinColumns = {@JoinColumn(name = "id_course")},
			   inverseJoinColumns = {@JoinColumn(name = "id_requirement")})
	Set<Requirement> requirementSet = new HashSet<Requirement>();

	public Course()
	{
	}

	public Course(String name)
	{
		this.name = name;
	}

	public void addGroup(Group group)
	{
		if (groupes.contains(group))
		{
			return;
		}
		groupes.add(group);
	}

	public void removeGroup(Group group)
	{
		if (!groupes.contains(group))
		{
			return;
		}
		groupes.remove(group);
	}

	private boolean isContainAuthor(User user)
	{
		Iterator<CourseACL> iterator = courseACL_List.iterator();
		while (iterator.hasNext())
		{
			CourseACL courseACL_obj = iterator.next();
			if (courseACL_obj.getUser().equals(user))
			{
				return true;
			}
		}
		return false;
	}

	public void addAuthor(User user, Author rights)
	{
		if (isContainAuthor(user))
		{
			return;
		}
		courseACL_List.add(new CourseACL(this, user, rights));
	}

	//чистит только ссылки в коде. Требуется код для синхронизации с базой
	public void removeAuthor(User user)
	{
		Iterator<CourseACL> iterator = courseACL_List.iterator();
		while (iterator.hasNext())
		{
			CourseACL courseACL_obj = iterator.next();
			if (courseACL_obj.getUser().equals(user))
			{
				iterator.remove();
				courseACL_obj.setCourse(null);
				courseACL_obj.setUser(null);
				break;
			}
		}
	}

	public Author checkRights(User user) throws NoRightException
	{
		Iterator<CourseACL> iterator = courseACL_List.iterator();
		while (iterator.hasNext())
		{
			CourseACL courseACL_obj = iterator.next();
			if (courseACL_obj.getUser().equals(user))
			{
				return courseACL_obj.getRights();
			}
		}
		throw new NoRightException();
	}

	public CourseACL getCourseACL(User user) throws NoRightException
	{
		Iterator<CourseACL> iterator = courseACL_List.iterator();
		while (iterator.hasNext())
		{
			CourseACL courseACL_obj = iterator.next();
			if (courseACL_obj.getUser().equals(user))
			{
				return courseACL_obj;
			}
		}
		throw new NoRightException();
	}

	public void addRequirement(Requirement requirement)
	{
		if(requirementSet.contains(requirement))
		{
			return;
		}
		requirementSet.add(requirement);
	}

	public void removeRequirement(Requirement requirement)
	{
		if(!requirementSet.contains(requirement))
		{
			return;
		}
		requirementSet.remove(requirement);
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

	public Set<Group> getGroupes()
	{
		return new HashSet<Group>(groupes);
	}

	public void setGroupes(Set<Group> groupes)
	{
		this.groupes = groupes;
	}

	public Set<Requirement> getRequirementSet()
	{
		return new HashSet<Requirement>(requirementSet);
	}

	public void setRequirementSet(Set<Requirement> requirementSet)
	{
		this.requirementSet = requirementSet;
	}
}
