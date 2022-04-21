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

	public CourseACL_Parser(Gson gson)
	{
		this.gson = gson;
	}

	@Override
	public JsonElement serialize(CourseACL courseACL, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		Course c = courseACL.getCourse();
		courseACL.setCourse(null);
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("user", gson.toJsonTree(courseACL.getUser()));
		jsonObject.addProperty("rights", courseACL.getRights().toString());
		courseACL.setCourse(c);
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
}
