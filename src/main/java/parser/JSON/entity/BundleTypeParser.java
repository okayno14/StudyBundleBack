package parser.JSON.entity;

import com.google.gson.*;
import dataAccess.entity.BundleType;

import java.lang.reflect.Type;

public class BundleTypeParser implements JsonSerializer<BundleType>, JsonDeserializer<BundleType>
{
	@Override
	public JsonElement serialize(BundleType bundleType, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id",bundleType.getId());
		jsonObject.addProperty("name",bundleType.getName());
		return jsonObject;
	}

	@Override
	public BundleType deserialize(JsonElement jsonElement, Type type,
								  JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		BundleType bt = new BundleType();
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		bt.setId(jsonObject.get("id").getAsLong());
		bt.setName(jsonObject.get("name").getAsString());
		return bt;
	}
}
