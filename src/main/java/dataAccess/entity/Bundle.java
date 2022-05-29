package dataAccess.entity;

import business.bundle.Similarity;
import com.google.gson.annotations.Expose;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import exception.Business.BusinessException;
import exception.Business.NoRightException;
import org.hibernate.annotations.Fetch;
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
	@Expose
	private long           id           = -1;
	@Expose
	private String         folder       = null;
	@Expose
	private int            num          = 0;
	@Enumerated(EnumType.STRING)
	@Type(type = "pgsql_enum")
	@Expose
	private BundleState    state        = BundleState.EMPTY;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_course",
				referencedColumnName = "id")
	private Course         course;
	@ManyToOne
	@JoinColumn(name = "id_bundle_type",
				referencedColumnName = "id")
	@Expose
	private BundleType     bundleType;
	@OneToOne(cascade = CascadeType.ALL,
			  orphanRemoval = true)
	@JoinColumn(name = "id_report",
				referencedColumnName = "id")
	@Expose
	private Report         report;
	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE},
			   mappedBy = "bundle")
	@Expose
	private Set<BundleACL> bundleACLSet = new HashSet<BundleACL>();
	@Transient
	private float originality = 1.0f;

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

	public Bundle(String folder, int num, BundleState state, BundleType bundleType,
				  Report report, Set<BundleACL> bundleACLSet)
	{
		this.folder       = folder;
		this.num          = num;
		this.state        = state;
		this.bundleType   = bundleType;
		this.report       = report;
		this.bundleACLSet = bundleACLSet;
	}

	public User getAuthor()
	{
		for (BundleACL acl : bundleACLSet)
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
		BundleACL obj = null;
		if (!existsACE(user))
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

	public void addACE(User user, Author rights)
	{
		if (existsACE(user))
		{
			return;
		}
		bundleACLSet.add(new BundleACL(this, user, rights));

		if (rights == Author.AUTHOR)
		{
			folder = user.getGroup().getName() + "/" + user.getLastName() + " " +
					user.getFirstName().charAt(0) + user.getFatherName().charAt(0) + "/" +
					course.getName() + "/" + bundleType.getName() + " " + num;
		}
	}

	public boolean existsACE(User user)
	{
		BundleACL obj = new BundleACL(this, user, null);
		if (bundleACLSet.contains(obj))
		{
			return true;
		}
		return false;
	}

	public BundleACL getACE(User user) throws NoRightException
	{
		for (BundleACL ace : bundleACLSet)
		{
			if (ace.getUser().equals(user))
			{
				return ace;
			}
		}
		throw new BusinessException(new NoRightException());
	}

	public BundleACL getAuthorACE()
	{
		for (BundleACL acl : bundleACLSet)
		{
			if (acl.getRights() == Author.AUTHOR)
			{
				return acl;
			}
		}
		throw new BusinessException(new NoRightException());
	}

	public void removeACE(User user)
	{
		BundleACL obj = new BundleACL(this, user, null);
		if (existsACE(user))
		{
			Iterator<BundleACL> iterator = bundleACLSet.iterator();
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

	public Set<BundleACL> getACL()
	{
		return bundleACLSet;
	}

	public void setACL(Set<BundleACL> bundleACLSet)
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

	public void setState(BundleState state)
	{
		this.state = state;
	}

	public void cancel()
	{
		this.state = BundleState.CANCELED;
	}

	public void accept()
	{
		this.state = BundleState.ACCEPTED;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public void setFolder(String folder)
	{
		this.folder = folder;
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

	public float getOriginality()
	{
		return originality;
	}

	public void setOriginality(float originality)
	{
		this.originality = originality;
	}
}
