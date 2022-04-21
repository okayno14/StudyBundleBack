package view.HTTP.request;

import com.google.gson.annotations.Expose;

public class LoginReq
{
	@Expose
	String email;
	@Expose
	String pass;

	public LoginReq()
	{
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
}
