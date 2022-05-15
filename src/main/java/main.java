import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import configuration.ConfMain;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import configuration.LogConf;
import parser.JSON.ConfMainParser;
import parser.JSON.DateAccessConfJSONParser;
import parser.JSON.HTTP_ConfJSONParser;
import parser.JSON.LogConfParser;
import view.HTTP.ServerFace;
import view.LoggerBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

public class main
{
	public static void main(String arg[])
	{
		Properties props = System.getProperties();
		props.setProperty("file.encoding","UTF-8");

		try (BufferedReader fileReader = new BufferedReader(
				new FileReader("resources/Config/conf.json")))
		{
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setPrettyPrinting();
			gsonBuilder.registerTypeAdapter(DateAccessConf.class, new DateAccessConfJSONParser());
			gsonBuilder.registerTypeAdapter(HTTP_Conf.class, new HTTP_ConfJSONParser());
			gsonBuilder.registerTypeAdapter(LogConf.class, new LogConfParser());
			gsonBuilder.registerTypeAdapter(ConfMain.class,new ConfMainParser());
			gsonBuilder.excludeFieldsWithoutExposeAnnotation();

			Gson gson = gsonBuilder.create();

			ConfMain confMain = gson.fromJson(fileReader, ConfMain.class);

			ServerFace serverFace = new ServerFace(confMain, gsonBuilder.create(), gsonBuilder, new LoggerBuilder());
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
