package parser.JSON.entity;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dataAccess.entity.Group;
import dataAccess.entity.User;
import dataAccess.repository.IGroupRepo;

import java.lang.reflect.Type;
import java.util.Set;

public class GroupParser implements JsonSerializer<Group>, JsonDeserializer<Group>
{
	private IGroupRepo groupRepo;
	private Gson       gson;

	public GroupParser(IGroupRepo groupRepo, Gson gson)
	{
		this.groupRepo = groupRepo;
		this.gson      = gson;
	}

	@Override
	public JsonElement serialize(Group group, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", group.getId());
		jsonObject.addProperty("name", group.getId());
		if (groupRepo.isStudentsFetched(group))
		{
			if(group.getStudents().size()!=0)
			{
				jsonObject.add("students", gson.toJsonTree(group.getStudents()));
			}
		}
		return jsonObject;
	}

	@Override
	public Group deserialize(JsonElement jsonElement, Type type,
							 JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		Group      group      = new Group();
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		group.setId(jsonObject.get("id").getAsLong());
		group.setName(jsonObject.get("name").getAsString());
		if (jsonObject.has("students"))
		{
			Type t = new TypeToken<Set<User>>(){}.getType();
			Set<User> students =gson.fromJson("students",t);
			group.setStudents(students);
		}
		return group;
	}
}
