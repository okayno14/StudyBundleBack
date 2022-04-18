package parser.JSON;


import com.google.gson.*;
import view.HTTP.request.LoginReq;

import java.lang.reflect.Type;

public class LoginReqParser implements JsonDeserializer<LoginReq>
{
	@Override
	public LoginReq deserialize(JsonElement jsonElement, Type type,
								JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		LoginReq   loginReq   = new LoginReq();
		loginReq.setEmail(jsonObject.get("email").getAsString());
		loginReq.setPass(jsonObject.get("pass").getAsString());
		return loginReq;
	}
}
