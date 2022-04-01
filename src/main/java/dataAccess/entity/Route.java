package dataAccess.entity;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@TypeDef(name="pgsql_enum",typeClass = PostgreSQLEnumType.class)
public class Route implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long        id = -1;
	@Enumerated(EnumType.STRING)
	@Type(type = "pgsql_enum")
	private HTTP_Method method;
	private String      urn;

	public Route()
	{
	}

	public Route(HTTP_Method http_method, String urn)
	{
		this.method=http_method;
		this.urn=urn;
	}

	public long getId()
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
