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
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Main
{
	static File getConfMainJSON(File root, int curDepth, int maxDepth, String fileName)
	{
		if (root.isDirectory())
		{
			curDepth++;
		}
		if (curDepth > maxDepth)
		{
			return null;
		}
		File childs[] = root.listFiles();
		if (childs != null)
		{
			for (File node : childs)
			{
				File res = getConfMainJSON(node, curDepth, maxDepth, fileName);
				if (res != null)
				{
					return res;
				}
			}
		}
		if (!root.isDirectory() && fileName.equals(root.getName()))
		{
			return root;
		}
		return null;
	}

	public static void main(String arg[])
	{
		try
		{
			Properties props = System.getProperties();
			props.setProperty("file.encoding", "UTF-8");

			String confName = "conf.json";
			File confMainJSON = null;
			if (arg.length == 0)
			{
				int maxDepth = 3;
				File mainFolder = new File(
						Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
				confMainJSON = getConfMainJSON(mainFolder, -1, maxDepth, confName);

				System.out.println("No confPath in arg. Trying get confPath in jar location");
				System.out.print("Root of search is: ");
				System.out.println(mainFolder.getAbsolutePath());

				if (confMainJSON == null)
				{
					mainFolder   = new File(props.getProperty("user.dir"));
					confMainJSON = getConfMainJSON(mainFolder, -1, maxDepth,
												   confName);

					System.out.println("No confPath in jar location. Trying get confPath in user.d");
					System.out.print("Root of search is: ");
					System.out.println(mainFolder.getAbsolutePath());
				}

			}
			else
			{
				confMainJSON = new File(arg[0]);
			}
			System.out.print("Finded confPath is: ");
			System.out.println(confMainJSON.getAbsolutePath());
			try (BufferedReader fileReader = new BufferedReader(new FileReader(confMainJSON)))
			{

				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.setPrettyPrinting();
				gsonBuilder
						.registerTypeAdapter(DateAccessConf.class, new DateAccessConfJSONParser());
				gsonBuilder.registerTypeAdapter(HTTP_Conf.class, new HTTP_ConfJSONParser());
				gsonBuilder.registerTypeAdapter(LogConf.class, new LogConfParser());
				gsonBuilder.registerTypeAdapter(ConfMain.class, new ConfMainParser());
				gsonBuilder.excludeFieldsWithoutExposeAnnotation();

				Gson gson = gsonBuilder.create();

				ConfMain confMain = gson.fromJson(fileReader, ConfMain.class);

				ServerFace serverFace = new ServerFace(confMain, gsonBuilder.create(), gsonBuilder,
													   new LoggerBuilder());
			}
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
