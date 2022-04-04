package dataAccess.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BundleACLID implements Serializable
{
	private long bundleID = -1;
	private long userID = -1;

	public BundleACLID(){}

	public BundleACLID(long bundleID, long userID)
	{
		this.bundleID = bundleID;
		this.userID   = userID;
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
		BundleACLID that = (BundleACLID) o;
		return bundleID == that.bundleID && userID == that.userID;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(bundleID, userID);
	}

	public long getBundleID()
	{
		return bundleID;
	}

	public void setBundleID(long bundleID)
	{
		this.bundleID = bundleID;
	}

	public long getUserID()
	{
		return userID;
	}

	public void setUserID(long userID)
	{
		this.userID = userID;
	}
}
