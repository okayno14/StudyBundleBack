package view.HTTP.request;

import com.google.gson.annotations.Expose;

public class CreateObjReq
{
	@Expose
	private long id;
	@Expose
	private String name;

	public CreateObjReq(long id, String name)
	{
		this.id   = id;
		this.name = name;
	}

	public CreateObjReq(long id)
	{
		this.id = id;
	}

	public CreateObjReq(String name)
	{
		this.name = name;
	}

	public CreateObjReq()
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
}
