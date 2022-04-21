package dataAccess.entity;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Role implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Expose
	private long        id = -1;
	@Expose
	private String      name;
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "role_route",
			   joinColumns = {@JoinColumn(name = "id_role")},
			   inverseJoinColumns = {@JoinColumn(name = "id_route")})
	private List<Route> routeList;

	public Role()
	{
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

	public List<Route> getRouteList()
	{
		return new ArrayList<Route>(routeList);
	}

	public void setRouteList(List<Route> routeList)
	{
		this.routeList = routeList;
	}
}
