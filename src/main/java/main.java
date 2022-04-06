import com.google.gson.GsonBuilder;
import configuration.*;
import view.HTTP.ServerFace;

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


			ServerFace serverFace = new ServerFace(confMain.getHttp_conf(),
												   confMain.getDateAccessConf(),
												   gsonBuilder.create());
			System.out.println("Стартуем");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
			System.exit(1);
		}
	}
}
