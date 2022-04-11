package dataAccess.entity;

import exception.Business.BusinessException;
import exception.Business.NoRightException;

import javax.persistence.*;
import java.util.*;

@Entity
public class Course
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long           id            = -1;
	private String         name;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "group_course",
			   joinColumns = {@JoinColumn(name = "id_course")},
			   inverseJoinColumns = {@JoinColumn(name = "id_group")})
	private Set<Group>     groupes       = new HashSet<Group>();
	//поменял CascadeType.ALL на MERGE
	@OneToMany(fetch = FetchType.EAGER,
			   mappedBy = "course",
			   cascade = {CascadeType.MERGE, CascadeType.REMOVE})
	private Set<CourseACL> courseACL_Set = new HashSet<CourseACL>();
	@ManyToMany(fetch = FetchType.EAGER,
				cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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

	public Course(String name, User author)
	{
		this.name = name;
		this.addAuthor(author, Author.AUTHOR);
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
		CourseACL obj = new CourseACL(this, user, Author.AUTHOR);
		if (courseACL_Set.contains(obj))
		{
			return true;
		}
		return false;
	}

	public void addAuthor(User user, Author rights)
	{
		if (isContainAuthor(user))
		{
			return;
		}
		courseACL_Set.add(new CourseACL(this, user, rights));
	}

	public void removeAuthor(User user)
	{
		CourseACL obj = new CourseACL(this, user, Author.AUTHOR);
		if (isContainAuthor(user))
		{
			courseACL_Set.remove(obj);
		}
	}

	public Author checkRights(User user) throws NoRightException
	{
		CourseACL obj = new CourseACL(this, user, Author.AUTHOR);
		if (!isContainAuthor(user))
		{
			throw new BusinessException(new NoRightException());
		}
		Iterator<CourseACL> iterator = courseACL_Set.iterator();
		while (iterator.hasNext())
		{
			obj = iterator.next();
			if (obj.getUser().equals(user))
			{
				return obj.getRights();
			}
		}
		throw new  BusinessException(new NoRightException());
	}

	public CourseACL getCourseACL(User user) throws NoRightException
	{
		Iterator<CourseACL> iterator = courseACL_Set.iterator();
		while (iterator.hasNext())
		{
			CourseACL courseACL_obj = iterator.next();
			if (courseACL_obj.getUser().equals(user))
			{
				return courseACL_obj;
			}
		}
		throw new BusinessException(new NoRightException());
	}

	public void addRequirement(Requirement requirement)
	{
		if (requirementSet.contains(requirement))
		{
			return;
		}
		requirementSet.add(requirement);
	}

	public void removeRequirement(Requirement requirement)
	{
		if (!requirementSet.contains(requirement))
		{
			return;
		}
		requirementSet.remove(requirement);
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
		Course course = (Course) o;
		return name.equals(course.name) && Objects.equals(groupes, course.groupes) && Objects
				.equals(requirementSet, course.requirementSet);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, groupes, requirementSet);
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
		return requirementSet;
	}

	public void setRequirementSet(Set<Requirement> requirementSet)
	{
		this.requirementSet = requirementSet;
	}

	public Set<CourseACL> getCourseACL_Set()
	{
		return courseACL_Set;
	}
}
