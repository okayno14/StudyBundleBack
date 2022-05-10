package dataAccess.entity;

import com.google.gson.annotations.Expose;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import exception.Business.BusinessException;
import exception.Business.NoRightException;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.*;


@Entity
@TypeDef(name = "pgsql_enum",
		 typeClass = PostgreSQLEnumType.class)
public class Course
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Expose
	private long           id            = -1;
	@Expose
	private String         name;
	@Enumerated(EnumType.STRING)
	@Type(type = "pgsql_enum")
	@Expose
	private CourseState    state         = CourseState.EMPTY;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "group_course",
			   joinColumns = {@JoinColumn(name = "id_course")},
			   inverseJoinColumns = {@JoinColumn(name = "id_group")})
	@Expose
	private Set<Group>     groupes       = new HashSet<Group>();
	//поменял CascadeType.ALL на MERGE
	@OneToMany(mappedBy = "course",
			   cascade = {CascadeType.MERGE, CascadeType.REMOVE})
	@Expose
	private Set<CourseACL> courseACL_Set = new HashSet<CourseACL>();
	@ManyToMany(fetch = FetchType.EAGER,
				cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "requirement_course",
			   joinColumns = {@JoinColumn(name = "id_course")},
			   inverseJoinColumns = {@JoinColumn(name = "id_requirement")})
	@Expose
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
		this.addACE(author, Author.AUTHOR);
	}

	public User getAuthor()
	{
		for (CourseACL acl : courseACL_Set)
		{
			if (acl.getRights() == Author.AUTHOR)
			{
				return acl.getUser();
			}
		}
		throw new BusinessException(new NoRightException());
	}

	public Author getRights(User user) throws NoRightException
	{
		CourseACL obj = null;
		if (!existsACE(user))
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
		throw new BusinessException(new NoRightException());
	}

	public void addACE(User user, Author rights)
	{
		if (existsACE(user))
		{
			return;
		}
		courseACL_Set.add(new CourseACL(this, user, rights));
	}

	private boolean existsACE(User user)
	{
		CourseACL obj = new CourseACL(this, user, Author.AUTHOR);
		if (courseACL_Set.contains(obj))
		{
			return true;
		}
		return false;
	}

	public CourseACL getACE(User user) throws NoRightException
	{
		for (CourseACL ace : courseACL_Set)
		{
			if (ace.getUser().equals(user))
			{
				return ace;
			}
		}
		throw new BusinessException(new NoRightException());
	}

	public CourseACL getAuthorACE()
	{
		for (CourseACL ace : courseACL_Set)
		{
			if (ace.getRights().equals(Author.AUTHOR))
			{
				return ace;
			}
		}
		throw new BusinessException(new NoRightException());
	}

	public void removeACE(User user)
	{
		CourseACL obj = null;
		if (existsACE(user))
		{
			Iterator<CourseACL> iterator = courseACL_Set.iterator();
			while (iterator.hasNext())
			{
				obj = iterator.next();
				if (obj.getUser().equals(user) && !obj.getRights().equals(Author.AUTHOR))
				{
					iterator.remove();
				}
			}
		}
	}

	public Set<CourseACL> getACL()
	{
		return courseACL_Set;
	}

	public void setACL(Set<CourseACL> courseACL_Set)
	{
		this.courseACL_Set = courseACL_Set;
	}

	public boolean contains(Group g)
	{
		return groupes.contains(g);
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

	public void addRequirement(Requirement requirement)
	{
		if (requirementSet.contains(requirement))
		{
			return;
		}
		requirementSet.add(requirement);
		state = CourseState.IN_PROGRESS;
	}

	public void removeRequirement(Requirement requirement)
	{
		if (!requirementSet.contains(requirement))
		{
			return;
		}
		requirementSet.remove(requirement);
	}

	public boolean isContainRequirement(BundleType bt, int q)
	{
		Requirement req = new Requirement(q, bt);
		return requirementSet.contains(req);
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
		return name.equals(course.name) && Objects.equals(groupes, course.groupes) &&
				Objects.equals(requirementSet, course.requirementSet);
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
		return groupes;
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

	public CourseState getState()
	{
		return state;
	}

	public void publish()
	{
		this.state = CourseState.PUBLISHED;
	}
}
