package parser.JSON.entity;

import com.google.gson.*;
import dataAccess.entity.Role;
import dataAccess.entity.User;

import java.lang.reflect.Type;

public class UserParser implements JsonSerializer<User>, JsonDeserializer<User>
{
	private Gson gson;

	public UserParser(Gson gson)
	{
		this.gson = gson;
	}

	@Override
	public JsonElement serialize(User user, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getId());
		jsonObject.addProperty("token", user.getToken());
		jsonObject.add("role", gson.toJsonTree(user.getRole()));
		jsonObject.addProperty("email", user.getEmail());
		jsonObject.addProperty("pass", user.getPass());
		jsonObject.addProperty("lastName", user.getLastName());
		jsonObject.addProperty("firstName", user.getFirstName());
		jsonObject.addProperty("fatherName", user.getFatherName());
		return jsonObject;
	}

	@Override
	public User deserialize(JsonElement jsonElement, Type type,
							JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		User       user       = new User();
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		user.setId(jsonObject.get("id").getAsLong());

		if (jsonObject.has("token"))
		{
			user.setToken(jsonObject.get("token").getAsString());
		}
		user.setRole(gson.fromJson(jsonObject.get("role"), Role.class));
		user.setEmail(jsonObject.get("email").toString());
		if (jsonObject.has("pass"))
		{
			user.setPass(jsonObject.get("pass").toString());
		}
		user.setLastName(jsonObject.get("lastName").getAsString());
		user.setFirstName(jsonObject.get("firstName").getAsString());
		user.setFatherName(jsonObject.get("fatherName").getAsString());
		return user;
	}

	public void defendData(JsonObject jsonObject)
	{
		jsonObject.remove("token");
		jsonObject.remove("pass");
	}
}
