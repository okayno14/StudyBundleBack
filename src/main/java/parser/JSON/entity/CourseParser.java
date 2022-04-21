package parser.JSON.entity;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dataAccess.entity.Course;
import dataAccess.entity.CourseACL;
import dataAccess.entity.Group;
import dataAccess.entity.Requirement;

import java.lang.reflect.Type;
import java.util.Set;

public class CourseParser implements JsonSerializer<Course>, JsonDeserializer<Course>
{
	private Gson gson;

	public CourseParser(Gson gson)
	{
		this.gson = gson;
	}

	@Override
	public JsonElement serialize(Course course, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", course.getId());
		jsonObject.addProperty("name", course.getName());
		if (course.getGroupes().size()!=0)
		{
			jsonObject.add("groupes", gson.toJsonTree(course.getGroupes()));
		}
		if(course.getCourseACL_Set().size()!=0)
		{
			jsonObject.add("courseACL_Set", gson.toJsonTree(course.getCourseACL_Set()));
		}
		if(course.getRequirementSet().size()!=0)
		{
			jsonObject.add("requirementSet", gson.toJsonTree(course.getRequirementSet()));
		}
		return jsonObject;
	}

	@Override
	public Course deserialize(JsonElement jsonElement, Type type,
							  JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		Course     c          = new Course();
		c.setId(jsonObject.get("id").getAsLong());
		c.setName(jsonObject.get("name").getAsString());
		Type type1 = new TypeToken<Set<Group>>()
		{
		}.getType();
		c.setGroupes(gson.fromJson(jsonObject.get("groupes"), type1));
		Type type2 = new TypeToken<Set<CourseACL>>()
		{
		}.getType();
		c.setCourseACL_Set(gson.fromJson(jsonObject.get("courseACL_Set"), type2));
		Type type3 = new TypeToken<Set<Requirement>>()
		{
		}.getType();
		c.setRequirementSet(gson.fromJson(jsonObject.get("requirementSet"), type3));
		return c;
	}
}
