package configuration;

import com.google.gson.*;

import java.lang.reflect.Type;

public class HTTP_ConfJSONParser implements JsonSerializer<HTTP_Conf>, JsonDeserializer<HTTP_Conf>
{
	@Override
	public JsonElement serialize(HTTP_Conf http_conf, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject json = new JsonObject();

		json.addProperty("Port", http_conf.port);
		return json;
	}


	@Override
	public HTTP_Conf deserialize(JsonElement jsonElement, Type type,
								 JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject json = jsonElement.getAsJsonObject();


		return new HTTP_Conf(json.get("Port").getAsInt());
	}
}
