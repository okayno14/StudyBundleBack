package dataAccess.entity;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Requirement implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Expose
	private long       id = -1;
	@Expose
	private int        quantity;
	@ManyToOne
	@JoinColumn(name = "id_bundle_type",
				referencedColumnName = "id")
	@Expose
	private BundleType bundleType;

	public Requirement()
	{
	}

	public Requirement(int quantity, BundleType bundleType)
	{
		this.quantity   = quantity;
		this.bundleType = bundleType;
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
		Requirement that = (Requirement) o;
		return quantity == that.quantity && bundleType.equals(that.bundleType);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(quantity, bundleType);
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	public BundleType getBundleType()
	{
		return bundleType;
	}

	public void setBundleType(BundleType bundleType)
	{
		this.bundleType = bundleType;
	}
}
