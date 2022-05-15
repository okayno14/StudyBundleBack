package parser.JSON;

import com.google.gson.*;
import configuration.ConfMain;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;

import java.lang.reflect.Type;

public class ConfMainParser implements JsonSerializer<ConfMain>, JsonDeserializer<ConfMain>
{
	@Override
	public JsonElement serialize(ConfMain confMain, Type type,
								 JsonSerializationContext serializationContext)
	{
		JsonObject json = new JsonObject();
		json.add("HTTP_Conf", serializationContext.serialize(confMain.getHttp_conf()));
		json.add("DataAccessConf", serializationContext.serialize(confMain.getDateAccessConf()));

		return json;
	}

	@Override
	public ConfMain deserialize(JsonElement jsonElement, Type type,
								JsonDeserializationContext deserializationContext)
			throws JsonParseException
	{
		JsonObject json     = jsonElement.getAsJsonObject();

		HTTP_Conf http_conf = deserializationContext
				.deserialize(json.getAsJsonObject("HTTP_Conf"), HTTP_Conf.class);
		DateAccessConf dateAccessConf = deserializationContext
				.deserialize(json.getAsJsonObject("DataAccessConf"), DateAccessConf.class);
		String resourcesPath = json.get("resourcesPath").getAsString();

		ConfMain confMain = new ConfMain(http_conf, dateAccessConf, resourcesPath);
		confMain.makeSubConfigs();

		return confMain;
	}
}
