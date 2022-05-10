package parser.JSON.entity;

import com.google.gson.*;
import dataAccess.entity.Author;
import dataAccess.entity.Course;
import dataAccess.entity.CourseACL;
import dataAccess.entity.User;

import java.lang.reflect.Type;

public class CourseACL_Parser implements JsonSerializer<CourseACL>, JsonDeserializer<CourseACL>
{
	private Gson gson;
	private UserParser userParser;

	public CourseACL_Parser(Gson gson, UserParser userParser)
	{
		this.gson = gson;
		this.userParser=userParser;
	}

	@Override
	public JsonElement serialize(CourseACL courseACL, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		return jsonObject;
	}

	@Override
	public CourseACL deserialize(JsonElement jsonElement, Type type,
								 JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		CourseACL courseACL = new CourseACL();
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		courseACL.setUser(gson.fromJson(jsonObject.get("user"), User.class));
		courseACL.setRights(Author.valueOf(jsonObject.get("rights").getAsString()));
		return courseACL;
	}

	public void defend(JsonObject cACE)
	{
		userParser.defendData(cACE.get("user").getAsJsonObject());
	}
}
