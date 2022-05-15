package parser.JSON;

import com.google.gson.*;
import configuration.LogConf;

import java.lang.reflect.Type;

public class LogConfParser implements JsonSerializer<LogConf>, JsonDeserializer<LogConf>
{
	@Override
	public JsonElement serialize(LogConf logConf, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		return new JsonObject();
	}

	@Override
	public LogConf deserialize(JsonElement jsonElement, Type type,
							   JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		String log4jConfPath = jsonObject.get("log4jConfPath").getAsString();
		return new LogConf(log4jConfPath);
	}
}
