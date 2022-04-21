package parser.JSON.entity;

import com.google.gson.*;
import dataAccess.entity.BundleType;
import dataAccess.entity.Requirement;

import java.lang.reflect.Type;

public class RequirementParser implements JsonSerializer<Requirement>, JsonDeserializer<Requirement>
{
	private Gson gson;

	public RequirementParser(Gson gson)
	{
		this.gson = gson;
	}

	@Override
	public JsonElement serialize(Requirement requirement, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id",requirement.getId());
		jsonObject.addProperty("quantity",requirement.getQuantity());
		jsonObject.add("bundleType",gson.toJsonTree(requirement.getBundleType()));
		return jsonObject;
	}

	@Override
	public Requirement deserialize(JsonElement jsonElement, Type type,
								   JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		Requirement req = new Requirement();
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		req.setId(jsonObject.get("id").getAsLong());
		req.setQuantity(jsonObject.get("quantity").getAsInt());
		req.setBundleType(gson.fromJson(jsonObject.get("bundleType"), BundleType.class));
		return req;
	}
}
