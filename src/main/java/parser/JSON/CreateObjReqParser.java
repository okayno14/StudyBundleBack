package parser.JSON;

import com.google.gson.*;
import view.HTTP.request.CreateObjReq;

import java.lang.reflect.Type;

public class CreateObjReqParser implements JsonSerializer<CreateObjReq>, JsonDeserializer<CreateObjReq>
{
	@Override
	public JsonElement serialize(CreateObjReq createObjReq, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id",createObjReq.getId());
		jsonObject.addProperty("name",createObjReq.getName());
		return jsonObject;
	}

	@Override
	public CreateObjReq deserialize(JsonElement jsonElement, Type type,
									JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		CreateObjReq createObjReq = new CreateObjReq();
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		createObjReq.setId(jsonObject.get("id").getAsLong());
		createObjReq.setName(jsonObject.get("name").getAsString());
		return createObjReq;
	}
}
