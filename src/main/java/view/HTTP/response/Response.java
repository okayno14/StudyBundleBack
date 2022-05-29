package view.HTTP.response;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

public class Response
{
	@Expose
	private JsonElement data=null;
	@Expose
	private String message=null;

	public Response(JsonElement data, String message)
	{
		this.data    = data;
		this.message = message;
	}

	public Response(JsonElement data)
	{
		this.data = data;
	}

	public Response(String message)
	{
		this.message = message;
	}

	public Response()
	{
	}

	public JsonElement getData()
	{
		return data;
	}

	public void setData(JsonElement data)
	{
		this.data = data;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}
