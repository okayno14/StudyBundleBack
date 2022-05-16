package dataAccess.entity;

import com.google.gson.annotations.Expose;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table (name = "bundle_acl")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class BundleACL implements Serializable
{
	@EmbeddedId
	private BundleACLID id;
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("bundleID")
	private Bundle bundle;
	@ManyToOne
	@MapsId("userID")
	@Expose
	private User user;
	@Enumerated(EnumType.STRING)
	@Column(name = "rights")
	@Type(type = "pgsql_enum")
	@Expose
	private Author rights;

	public BundleACL(){}

	public BundleACL(Bundle bundle, User user, Author rights)
	{
		this.bundle = bundle;
		this.user   = user;
		this.rights = rights;
		this.id = new BundleACLID(bundle.getId(),user.getId());
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
		BundleACL bundleACL = (BundleACL) o;
		return bundle.equals(bundleACL.bundle) && user.equals(bundleACL.user);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(bundle, user);
	}

	public BundleACLID getId()
	{
		return id;
	}

	public void setId(BundleACLID id)
	{
		this.id = id;
	}

	public Bundle getBundle()
	{
		return bundle;
	}

	public void setBundle(Bundle bundle)
	{
		this.bundle = bundle;
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
