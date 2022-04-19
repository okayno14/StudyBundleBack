import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import configuration.ConfMain;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import dataAccess.entity.Role;
import dataAccess.entity.User;
import parser.JSON.entity.RoleParser;
import parser.JSON.entity.UserParser;
import view.HTTP.Response;
import view.HTTP.ServerFace;

import parser.JSON.*;
import view.HTTP.request.LoginReq;

import java.io.BufferedReader;
import java.io.FileReader;

public class main
{
	public static void main(String arg[])
	{
		try (BufferedReader fileReader = new BufferedReader(
				new FileReader("resources/Config/conf.json")))
		{
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setPrettyPrinting();
			gsonBuilder.registerTypeAdapter(DateAccessConf.class, new DateAccessConfJSONParser());
			gsonBuilder.registerTypeAdapter(HTTP_Conf.class, new HTTP_ConfJSONParser());


			ConfMain confMain = new ConfMain(gsonBuilder.create());
			gsonBuilder.registerTypeAdapter(ConfMain.class, confMain);
			confMain = gsonBuilder.create().fromJson(fileReader, confMain.getClass());
			//тут блок регистрации парсеров сущностей бизнес-слоя
			gsonBuilder
					.registerTypeAdapter(Response.class, new ResponseParser(gsonBuilder.create()));
			gsonBuilder.registerTypeAdapter(LoginReq.class, new LoginReqParser());
			gsonBuilder.registerTypeAdapter(Role.class, new RoleParser());
			gsonBuilder.registerTypeAdapter(User.class, new UserParser(gsonBuilder.create()));
			ServerFace serverFace = new ServerFace(confMain.getHttp_conf(), confMain,
												   gsonBuilder.create(), gsonBuilder);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//System.out.println(e.getCause());
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}
