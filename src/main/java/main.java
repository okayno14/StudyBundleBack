import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import configuration.ConfMain;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import dataAccess.entity.Role;
import dataAccess.entity.User;
import org.apache.log4j.xml.DOMConfigurator;
import parser.JSON.entity.RoleParser;
import parser.JSON.entity.UserParser;
import view.HTTP.Response;
import view.HTTP.ServerFace;

import parser.JSON.*;
import view.HTTP.request.LoginReq;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

public class main
{
	public static void main(String arg[])
	{
		Properties props = System.getProperties();
		props.setProperty("org.jboss.logging.provider", "slf4j");
		props.setProperty("org.apache.poi.util.POILogger","org.apache.poi.util.SLF4JLogger");
		props.setProperty("file.encoding","UTF-8");

		DOMConfigurator.configure("resources/Config/log4j.xml");

		try (BufferedReader fileReader = new BufferedReader(
				new FileReader("resources/Config/conf.json")))
		{
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setPrettyPrinting();
			gsonBuilder.registerTypeAdapter(DateAccessConf.class, new DateAccessConfJSONParser());
			gsonBuilder.registerTypeAdapter(HTTP_Conf.class, new HTTP_ConfJSONParser());
			gsonBuilder.excludeFieldsWithoutExposeAnnotation();

			ConfMain confMain = new ConfMain(gsonBuilder.create());
			gsonBuilder.registerTypeAdapter(ConfMain.class, confMain);
			confMain = gsonBuilder.create().fromJson(fileReader, confMain.getClass());



			ServerFace serverFace = new ServerFace(confMain, gsonBuilder.create(), gsonBuilder);
		}
		catch (Exception e)
		{
			System.out.println("ВСЁ ПЛОХО");
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}
