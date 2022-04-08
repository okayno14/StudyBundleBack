package configuration;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ConfMain implements JsonSerializer<ConfMain>, JsonDeserializer<ConfMain>
{
	private HTTP_Conf      http_conf      = null;
	private DateAccessConf dateAccessConf = null;
	private BusinessConfiguration businessConfiguration = new BusinessConfiguration();
	private Gson           gson;

	public ConfMain(Gson gson)
	{
		this.gson = gson;
	}

	public ConfMain(HTTP_Conf http_conf, DateAccessConf dateAccessConf)
	{
		this.http_conf      = http_conf;
		this.dateAccessConf = dateAccessConf;
	}

	@Override
	public JsonElement serialize(ConfMain confMain, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject json = new JsonObject();
		json.addProperty("HTTP_Conf", gson.toJson(http_conf));
		json.addProperty("DataAccessConf", gson.toJson(dateAccessConf));

		return json;
	}

	@Override
	public ConfMain deserialize(JsonElement jsonElement, Type type,
								JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject json = jsonElement.getAsJsonObject();
		http_conf = gson.fromJson(json.getAsJsonObject("HTTP_Conf"), HTTP_Conf.class);
		dateAccessConf = gson.fromJson(json.getAsJsonObject("DataAccessConf"),DateAccessConf.class);

		return this;
	}

	public HTTP_Conf getHttp_conf()
	{
		return http_conf;
	}

	public DateAccessConf getDateAccessConf()
	{
		return dateAccessConf;
	}

	public BusinessConfiguration getBusinessConfiguration()
	{
		return businessConfiguration;
	}
}
