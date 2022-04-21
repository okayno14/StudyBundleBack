package dataAccess.entity;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "bundle_type")
public class BundleType implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Expose
	private long   id = -1;
	@Expose
	private String name;

	public BundleType()
	{
	}

	public BundleType(String name)
	{
		this.name = name;
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
		BundleType that = (BundleType) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name);
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
}
