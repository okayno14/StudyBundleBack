package parser.JSON;

import com.google.gson.*;
import configuration.MailConf;

import java.lang.reflect.Type;

public class MailConfParser implements JsonDeserializer<MailConf>
{
	@Override
	public MailConf deserialize(JsonElement jsonElement, Type type,
								JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		String mailAddress  = jsonObject.get("mailAddress").getAsString();
		String password = jsonObject.get("password").getAsString();
		String smtpHost = jsonObject.get("smtpHost").getAsString();
		String smtpPort = jsonObject.get("smtpPort").getAsString();

		return new MailConf(mailAddress,password,smtpHost,smtpPort);
	}
}
