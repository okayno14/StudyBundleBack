package parser.JSON;

import com.google.gson.*;
import view.HTTP.Response;

import java.lang.reflect.Type;

public class ResponseParser implements JsonSerializer<Response>
{
	private Gson gson;

	public ResponseParser(Gson gson)
	{
		this.gson = gson;
	}

	@Override
	public JsonElement serialize(Response response, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		if (response.getData() != null)
		{
			jsonObject.add("Data", response.getData());
		}
		if (response.getMessage() != null)
		{
			jsonObject.addProperty("Message", response.getMessage());
		}
		return jsonObject;
	}
}
