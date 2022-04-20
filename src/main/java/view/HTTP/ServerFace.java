package view.HTTP;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import configuration.ConfMain;
import configuration.HTTP_Conf;
import controller.*;
import dataAccess.entity.BundleType;
import dataAccess.entity.Role;
import dataAccess.entity.Route;
import dataAccess.entity.User;
import exception.Controller.ControllerException;
import exception.Controller.TokenNotFound;
import parser.JSON.entity.RoleParser;
import parser.JSON.entity.UserParser;
import spark.Request;
import spark.Spark;
import view.HTTP.request.LoginReq;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spark.Spark.*;

public class ServerFace
{
	private Controller controller;

	private HTTP_Conf   http_conf;
	private GsonBuilder gsonBuilder;
	private Gson        gson;

	private UserParser userParser;

	private IBundleController     bundleController;
	private IBundleTypeController bundleTypeController;
	private ICourseController     courseController;
	private IGroupController      groupController;
	private IUserController       userController;
	private IRoleController       roleController;

	private final String any = "ANY";

	public ServerFace(HTTP_Conf http_conf, ConfMain confMain, Gson gson, GsonBuilder gsonBuilder)
	{
		this.http_conf   = http_conf;
		this.gson        = gson;
		this.gsonBuilder = gsonBuilder;
		//применяем параметры конфигурации для сервера
		Spark.port(http_conf.getPort());

		//строим контроллер
		controller = new Controller(confMain);

		bundleController     = controller.getBundleController();
		bundleTypeController = controller.getBundleTypeController();
		courseController     = controller.getCourseController();
		groupController      = controller.getGroupController();
		userController       = controller.getUserController();
		roleController       = controller.getRoleController();

		//регистрация парсеров JSON
		gsonBuilder.registerTypeAdapter(Role.class, new RoleParser());
		userParser = new UserParser(gsonBuilder.create());
		gsonBuilder.registerTypeAdapter(User.class, userParser);

		//testBundleTypeService();

		//стартуем сервер
		endpoints();
	}

	private void testBundleTypeService()
	{
		List<BundleType> res  = bundleTypeController.get();
		List<BundleType> res1 = bundleTypeController.get();

		BundleType bt = new BundleType("ПРГРГ");
		bundleTypeController.add(bt);
		bundleTypeController.delete(bt);

		BundleType obj = bundleTypeController.get(3L);
		//obj = bundleTypeController.get(156156156L);
	}

	private User initClient(Request req, spark.Response resp)
	{
		try
		{
			User client = userController.getByToken(req.session().attribute("token"));
			//если пользователь персистентен и не подтвердил почту, то ему нельзя работать в системе
			if (!client.isEmailState() && client.getId() != -1)
			{
				halt(401, "У пользователя нет права на это действие");
			}
			Route route = client.getRole().getRouteList().get(0);
			if (route.getMethod().toString().equals(any) && route.getUrn().equals(any))
			{
				return client;
			}

			Set<String> params  = req.params().keySet();
			String      urn     = req.uri();
			Pattern     pattern = Pattern.compile("/[a-zA-z0-9]+");
			Matcher     matcher = pattern.matcher(urn);

			List<String> parts = new LinkedList<>();
			while (matcher.find())
			{
				parts.add(matcher.group());
			}
			StringBuffer     name = new StringBuffer();
			Iterator<String> iter = parts.iterator();
			for (int i = 0; i < parts.size() - params.size() && iter.hasNext(); i++)
			{
				name.append(iter.next());
			}

			Iterator<Route> routeIterator = client.getRole().getRouteList().iterator();
			while (routeIterator.hasNext())
			{
				route = routeIterator.next();

				boolean flag = true;

				if (!route.getUrn().equals(any))
				{
					iter = params.iterator();
					while (iter.hasNext())
					{
						flag = flag && route.getUrn().contains(iter.next());
					}
					flag = flag && route.getUrn().contains(name.toString());
				}
				if (!route.getMethod().toString().equals(any))
				{
					flag = flag && route.getMethod().toString().equals(req.requestMethod());
				}
				if (flag)
				{
					return client;
				}
			}
			halt(401, "У пользователя нет права на это действие");
		}
		catch (ControllerException e)
		{
			if (e.getCause() instanceof TokenNotFound)
			{
				halt(401, "Закончился срок аренды токена");
			}
		}
		return null;
	}

	public void endpoints()
	{
		before("/*", (req, resp) ->
		{
			//CORS
			resp.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
			resp.header("Access-Control-Allow-Origin", "*");
			resp.header("Access-Control-Allow-Headers",
						"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
			resp.header("Access-Control-Allow-Credentials", "true");

			//Получение токена сессии и проверка прав токена на метод API
			try
			{
				//Поиск по токену пользователя в сессии
				User client;

				if (req.session().isNew() || !req.session().attributes().contains("token"))
				{
					req.session(true);
					client = userController.getGuestUser();
					req.session().attribute("token", client.getToken());
				}
				else
				{

				}
				//Проверка роли пользователя из сессии из запрошенного им метода
			}
			catch (ControllerException e)
			{
				if (e.getCause() instanceof TokenNotFound)
				{
					//добавить обработку
					System.out.println("токена нет");
				}
			}
		});

		//CORS
		options("/*", (request, response) ->
		{
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null)
			{
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null)
			{
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}
			return "OK";
		});

		get("/", (req, resp) ->
		{
			return "Hello";
		});

		put("/user/login", (req, resp) ->
		{
			User     client   = initClient(req, resp);
			String   token    = req.session().attribute("token");
			LoginReq loginReq = gson.fromJson(req.body(), LoginReq.class);
			if (userController.login(token, loginReq.getEmail(), loginReq.getPass()))
			{
				resp.status(200);
				JsonElement data = gson.toJsonTree(userController.getByToken(token));
				return gson.toJson(new Response(data, "Успешно"));
			}
			resp.status(401);
			return gson.toJson(new Response(
					"У пользователя уже есть токен. Или данные не введены корректно"));
		});
		put("/user/logout", (req, resp) ->
		{
			User client = initClient(req, resp);
			userController.logout(client.getToken());
			req.session().removeAttribute("token");
			return gson.toJson(new Response("Успешно"));
		});
	}
}
