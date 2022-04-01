package dataAccess.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bundle_type")
public class BundleType implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long   id = -1;
	private String name;

	public BundleType()
	{
	}

	public BundleType(String name)
	{
		this.name = name;
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
