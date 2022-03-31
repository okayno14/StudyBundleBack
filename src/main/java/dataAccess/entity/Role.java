package dataAccess.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Role implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int         id = -1;
	private String      name;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_route", joinColumns = {@JoinColumn(name = "id_role")},
			   inverseJoinColumns = {@JoinColumn(name = "id_route")})
	private List<Route> routeList;

	public Role()
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
