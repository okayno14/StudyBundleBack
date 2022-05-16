package parser.JSON.entity;

import com.google.gson.*;
import dataAccess.entity.*;

import java.lang.reflect.Type;

public class BundleACLParser implements JsonSerializer<BundleACL>, JsonDeserializer<BundleACL>
{
	private Gson       gson;
	private UserParser userParser;

	public BundleACLParser(Gson gson, UserParser userParser)
	{
		this.gson       = gson;
		this.userParser = userParser;
	}

	@Override
	public JsonElement serialize(BundleACL bundleACL, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		return jsonObject;
	}

	@Override
	public BundleACL deserialize(JsonElement jsonElement, Type type,
								 JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		BundleACL  bundleACL  = new BundleACL();
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		bundleACL.setUser(gson.fromJson(jsonObject.get("user"), User.class));
		bundleACL.setRights(Author.valueOf(jsonObject.get("rights").getAsString()));
		return bundleACL;
	}

	public void defend(JsonObject bACE)
	{
		userParser.defendData(bACE.get("user").getAsJsonObject());
	}

}
