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
	private CourseACL_Parser courseACL_parser;

	public CourseParser(Gson gson, CourseACL_Parser courseACL_parser)
	{
		this.gson             = gson;
		this.courseACL_parser = courseACL_parser;
	}

	@Override
	public JsonElement serialize(Course course, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
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

	public void filterGroupStudents(JsonObject course)
	{
		JsonArray arr = course.getAsJsonArray("groupes");
		for(JsonElement jsonElement: arr)
		{
			JsonObject gOBJ = jsonElement.getAsJsonObject();
			if(gOBJ.has("students"))
			{
				gOBJ.remove("students");
			}
		}
	}

	public void defend(JsonObject course)
	{
		for(JsonElement cACE:course.get("courseACL_Set").getAsJsonArray())
		{
			courseACL_parser.defend(cACE.getAsJsonObject());
		}
	}
}
