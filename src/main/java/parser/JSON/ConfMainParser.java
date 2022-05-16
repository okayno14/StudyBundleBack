package parser.JSON;

import com.google.gson.*;
import configuration.ConfMain;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import configuration.LogConf;

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
		JsonObject json = jsonElement.getAsJsonObject();

		HTTP_Conf http_conf = deserializationContext
				.deserialize(json.get("HTTP_Conf"), HTTP_Conf.class);
		DateAccessConf dateAccessConf = deserializationContext
				.deserialize(json.get("DataAccessConf"), DateAccessConf.class);
		LogConf logConf = deserializationContext.deserialize(json.get("LogConf"), LogConf.class);

		String resourcesPath = json.get("resourcesPath").getAsString();


		ConfMain confMain = new ConfMain(http_conf, dateAccessConf, logConf, resourcesPath);
		confMain.makeSubConfigs();

		return confMain;
	}
}
