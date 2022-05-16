package parser.JSON.entity;

import com.google.gson.*;
import dataAccess.entity.Bundle;

import java.lang.reflect.Type;

public class BundleParser implements JsonSerializer<Bundle>
{
	private BundleACLParser bundleACLParser;

	public BundleParser(BundleACLParser bundleACLParser)
	{
		this.bundleACLParser = bundleACLParser;
	}

	@Override
	public JsonElement serialize(Bundle bundle, Type type,
								 JsonSerializationContext jsonSerializationContext)
	{
		JsonObject jsonObject = new JsonObject();
		return jsonObject;
	}

	public void defend(JsonObject bundle)
	{
		for(JsonElement bACE:bundle.get("bundleACLSet").getAsJsonArray())
		{
			bundleACLParser.defend(bACE.getAsJsonObject());
		}
	}

	public void filter(JsonObject jsonObject, Bundle source)
	{
		jsonObject.addProperty("courseID",source.getCourse().getId());
	}
}
