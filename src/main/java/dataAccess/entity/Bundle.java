package dataAccess.entity;

import business.bundle.Similarity;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import configuration.BusinessConfiguration;
import exception.Business.BusinessException;
import exception.Business.NoRightException;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity
@TypeDef(name = "pgsql_enum",
		 typeClass = PostgreSQLEnumType.class)
public class Bundle implements Serializable, Similarity
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long           id           = -1;
	private String         folder       = null;
	private int            num          = 0;
	@Enumerated(EnumType.STRING)
	@Type(type = "pgsql_enum")
	private BundleState    state        = BundleState.EMPTY;
	@ManyToOne
	@JoinColumn(name = "id_course",
				referencedColumnName = "id")
	private Course         course;
	@ManyToOne
	@JoinColumn(name = "id_bundle_type",
				referencedColumnName = "id")
	private BundleType     bundleType;
	@OneToOne(cascade = CascadeType.ALL,
			  orphanRemoval = true)
	@JoinColumn(name = "id_report",
				referencedColumnName = "id")
	private Report         report;
	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE},
			   mappedBy = "bundle")
	private Set<BundleACL> bundleACLSet = new HashSet<BundleACL>();


	public Bundle()
	{
	}

	//Считается, что пользователь состоит в учебной группе и содержит ссылку на неё
	//Конструктор создаёт пустой Bundle, который в течение других бизнес-процессов будет дополнятся
	//метаданными
	public Bundle(int num, Course course, BundleType bundleType)
	{
		this.num        = num;
		this.course     = course;
		this.bundleType = bundleType;
		this.report     = new Report();
	}

	private boolean isContainAuthor(User user)
	{
		BundleACL obj = new BundleACL(this, user, null);
		if (bundleACLSet.contains(obj))
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
		bundleACLSet.add(new BundleACL(this, user, rights));

		if(rights==Author.AUTHOR)
		{
			folder = user.getGroup().getName() + "/" + user.getLastName() + " " +
					user.getFirstName().charAt(0) + user.getFatherName().charAt(0) + "/" +
					course.getName() + "/" + bundleType.getName() + " " + num;
		}
	}

	public void removeAuthor(User user)
	{
		BundleACL obj = new BundleACL(this, user, null);
		if (isContainAuthor(user))
		{
			bundleACLSet.remove(obj);
		}
	}

	public Author checkRights(User user) throws NoRightException
	{
		BundleACL obj = null;
		if (!isContainAuthor(user))
		{
			throw new BusinessException(new NoRightException());
		}
		Iterator<BundleACL> iterator = bundleACLSet.iterator();
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

	public BundleACL getBundleACL(User user) throws NoRightException
	{
		Iterator<BundleACL> iterator = bundleACLSet.iterator();
		while (iterator.hasNext())
		{
			BundleACL bundleACL_obj = iterator.next();
			if (bundleACL_obj.getUser().equals(user))
			{
				return bundleACL_obj;
			}
		}
		throw new BusinessException(new NoRightException());
	}

	public Set<BundleACL> getBundleACLSet()
	{
		return bundleACLSet;
	}

	public void setBundleACLSet(Set<BundleACL> bundleACLSet)
	{
		this.bundleACLSet = bundleACLSet;
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
		Bundle bundle = (Bundle) o;
		return id == bundle.id;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public boolean isSetCompatible()
	{
		return report.isSetCompatible();
	}

	@Override
	public Set<Object> getSet()
	{
		return report.getSet();
	}

	public BundleState getState()
	{
		return state;
	}

	public void cancel()
	{
		this.state = BundleState.CANCELED;
	}

	public void accept()
	{
		this.state = BundleState.ACCEPTED;
	}

	public long getId()
	{
		return id;
	}

	public String getFolder()
	{
		return folder;
	}

	public int getNum()
	{
		return num;
	}

	public Course getCourse()
	{
		return course;
	}

	public BundleType getBundleType()
	{
		return bundleType;
	}

	public Report getReport()
	{
		return report;
	}

	public void setReport(Report report)
	{
		this.report = report;
	}

	public void setBundleType(BundleType bundleType)
	{
		this.bundleType = bundleType;
	}

	public void setCourse(Course course)
	{
		this.course = course;
	}

	public void setId(long id)
	{
		this.id = id;
	}
}
