package dataAccess.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Route implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int         id = -1;
	@Enumerated(EnumType.STRING)
	private HTTP_Method method;
	private String      urn;

	public Route()
	{
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public HTTP_Method getMethod()
	{
		return method;
	}

	public void setMethod(HTTP_Method method)
	{
		this.method = method;
	}

	public String getUrn()
	{
		return urn;
	}

	public void setUrn(String urn)
	{
		this.urn = urn;
	}
}
