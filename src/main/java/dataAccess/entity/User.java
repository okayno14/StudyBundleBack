package dataAccess.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "\"user\"")
public class User implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long    id         = -1;
	@Transient
	private String  token;
	@ManyToOne
	@JoinColumn(name = "id_role", referencedColumnName = "id")
	private Role    role;
	@Column(name = "email_state")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean emailState = false;
	private String  email;
	private String  pass;
	private String  lastName;
	private String  firstName;
	private String  fatherName;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_group", referencedColumnName = "id")
	private Group   group;

	public User()
	{
	}

	private String genPass()
	{
		StringBuffer stringBuffer = new StringBuffer(20);
		//33-127
		for (int i = 0; i < 20; i++)
		{
			int code = (int) (Math.random() * (127 - 33) + 33);
			stringBuffer.append((char) code);
		}
		return stringBuffer.toString();
	}

	public User(String lastName, String firstName, String fatherName, String email, Role role)
	{
		this.lastName   = lastName;
		this.firstName  = firstName;
		this.fatherName = fatherName;
		this.email      = email;
		this.role       = role;
		pass            = genPass();
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
		User user = (User) o;
		return id == user.id;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	public long getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public boolean isEmailState()
	{
		return emailState;
	}

	public void setEmailState(boolean emailState)
	{
		this.emailState = emailState;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPass()
	{
		return pass;
	}

	public void setPass(String pass)
	{
		this.pass = pass;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getFatherName()
	{
		return fatherName;
	}

	public void setFatherName(String fatherName)
	{
		this.fatherName = fatherName;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public Group getGroup()
	{
		return group;
	}

	public void setGroup(Group group)
	{
		this.group = group;
	}
}
