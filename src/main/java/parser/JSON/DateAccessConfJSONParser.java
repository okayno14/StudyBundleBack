package parser.JSON;

import com.google.gson.*;
import configuration.DateAccessConf;

import java.lang.reflect.Type;

public class DateAccessConfJSONParser implements JsonSerializer<DateAccessConf>, JsonDeserializer<DateAccessConf>
{
	@Override
	public JsonElement serialize(DateAccessConf dateAccess, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject json = new JsonObject();
		json.addProperty("HibernateConf",dateAccess.getHibernateConf());
		json.addProperty("StoragePath",dateAccess.getStoragePath());
		return json;
	}


	@Override
	public DateAccessConf deserialize(JsonElement jsonElement, Type type,
							  JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject object = jsonElement.getAsJsonObject();
		String hibernateConf = object.get("HibernateConf").getAsString();
		String storagePath = object.get("StoragePath").getAsString();

		return new DateAccessConf(hibernateConf,storagePath);
	}
}
