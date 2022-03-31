package dataAccess.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Role implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int         id;
	private String      name;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_route",
			   joinColumns = {@JoinColumn(name = "id_role")},
			   inverseJoinColumns = {@JoinColumn(name = "id_route")})
	private List<Route> routeList;

	public Role()
	{
	}
}
