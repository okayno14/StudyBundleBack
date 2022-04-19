package parser.JSON.entity;

import com.google.gson.*;
import dataAccess.entity.Role;

import java.lang.reflect.Type;

public class RoleParser implements JsonSerializer<Role>, JsonDeserializer<Role>
{
	@Override
	public JsonElement serialize(Role role, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id",role.getId());
		jsonObject.addProperty("name",role.getName());
		return jsonObject;
	}

	@Override
	public Role deserialize(JsonElement jsonElement, Type type,
							JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		Role role = new Role();
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		role.setId(jsonObject.get("id").getAsLong());
		role.setName(jsonObject.get("name").getAsString());
		return role;
	}
}
