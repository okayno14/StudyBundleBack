package view.HTTP;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import configuration.ConfMain;
import configuration.HTTP_Conf;
import controller.*;
import dataAccess.entity.*;
import exception.Controller.ControllerException;
import exception.Controller.TokenNotFound;
import exception.DataAccess.DataAccessException;
import parser.JSON.CreateObjReqParser;
import parser.JSON.LoginReqParser;
import parser.JSON.ResponseParser;
import parser.JSON.entity.*;
import spark.Request;
import spark.Spark;
import view.HTTP.request.CreateObjReq;
import view.HTTP.request.LoginReq;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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


		//регистрация парсеров JSON для серверных записей
		gsonBuilder.registerTypeAdapter(Response.class, new ResponseParser(gsonBuilder.create()));
		gsonBuilder.registerTypeAdapter(LoginReq.class, new LoginReqParser());
		gsonBuilder.registerTypeAdapter(CreateObjReqParser.class, new CreateObjReqParser());

		//регистрация парсеров JSON для сущностей
		gsonBuilder.registerTypeAdapter(Role.class, new RoleParser());
		userParser = new UserParser(gson);
		gsonBuilder.registerTypeAdapter(User.class, userParser);
		GroupParser groupParser = new GroupParser(groupController.getRepo(), gson);
		gsonBuilder.registerTypeAdapter(Group.class, groupParser);
		gsonBuilder.registerTypeAdapter(BundleType.class, new BundleTypeParser());
		gsonBuilder.registerTypeAdapter(Requirement.class, new RequirementParser(gson));
		gsonBuilder.registerTypeAdapter(CourseACL.class, new CourseACL_Parser(gson));
		gsonBuilder.registerTypeAdapter(Course.class, new CourseParser(gson));

		//стартуем сервер
		endpoints();
	}

	//Аутентификация и авторизация клиента
	private User authentAuthorize(Request req, spark.Response resp)
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
		//Настрока CORS
		//Если сессии не было или токен удалён, то сгенерировать гостя
		before("/*", (req, resp) ->
		{
			//CORS
			resp.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
			resp.header("Access-Control-Allow-Origin", "*");
			resp.header("Access-Control-Allow-Headers",
						"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
			resp.header("Access-Control-Allow-Credentials", "true");

			//Если сессия новая, то создаём её и уинициируем значение токена
			User client = null;
			if (req.session().isNew() || !req.session().attributes().contains("token"))
			{
				req.session(true);
				client = userController.getGuestUser();
				req.session().attribute("token", client.getToken());
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

		post("/user", ((req, resp) ->
		{
			User client = authentAuthorize(req, resp);
			Type t = new TypeToken<List<User>>()
			{
			}.getType();
			List<User> data = gson.fromJson(req.body(), t);
			userController.add(data);
			JsonArray      jsonArray = new JsonArray();
			Iterator<User> iterator  = data.iterator();
			while (iterator.hasNext())
			{
				JsonObject jsonObject = gson.toJsonTree(iterator.next(), User.class)
											.getAsJsonObject();
				userParser.defendData(jsonObject);
				jsonArray.add(jsonObject);
			}
			resp.status(200);
			return gson.toJson(new Response(jsonArray));
		}));

		path("/user", () ->
		{
			put("/login", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				LoginReq loginReq = gson.fromJson(req.body(), LoginReq.class);
				if (userController
						.login(token, tokenExpires, loginReq.getEmail(), loginReq.getPass()))
				{
					resp.status(200);
					JsonElement data = gson.toJsonTree(userController.getByToken(token));
					return gson.toJson(new Response(data, "Успешно"));
				}
				resp.status(401);
				return gson.toJson(new Response(
						"У пользователя уже есть токен. Или данные не введены корректно"));
			});

			put("/logout", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				userController.logout(token);
				req.session().removeAttribute("token");
				return gson.toJson(new Response("Успешно"));
			});

			path("/group", () ->
			{
				post("/:name", ((req, resp) ->
				{
					User   client = authentAuthorize(req, resp);
					String name   = req.params("name");
					Group  group  = new Group(name);
					try
					{
						groupController.add(group);
						resp.status(200);
						return gson.toJson(new Response(gson.toJsonTree(group), "Успех"));
					}
					catch (DataAccessException e)
					{
						resp.status(422);
						return gson.toJson(new Response(e.getCause().getMessage()));
					}

				}));

				delete("/:id", (req, resp) ->
				{
					try
					{
						User  client = authentAuthorize(req, resp);
						long  id     = Long.parseLong(req.params("id"));
						Group toDel  = new Group();
						toDel.setId(id);
						groupController.delete(toDel);
						resp.status(200);
						return gson.toJson(new Response("Успех"));
					}
					catch (DataAccessException e)
					{
						resp.status(404);
						return gson.toJson(new Response(
								"Объект не найден\n " + e.getCause().getMessage()));
					}
				});
			});
		});

		delete("/user", (req, resp) ->
		{
			User client = authentAuthorize(req, resp);

			return "Empty";
		});

		post("/course", (req, resp) ->
		{
			User client = authentAuthorize(req, resp);
			try
			{
				CreateObjReq createObjReq = gson.fromJson(req.body(), CreateObjReq.class);
				Course c = new Course(createObjReq.getName(),
									  userController.get(createObjReq.getId()));
				courseController.add(c);
				resp.status(200);
				return gson.toJson(new Response(gson.toJsonTree(c), "Успех"));
			}
			catch (DataAccessException e)
			{
				resp.status(404);
				return "По указанному id пользователь не найден";
			}
		});

		//Если ничего не получилось найти, то швыряем стак трэйс в клиентский код
		exception(Exception.class, (e, req, resp) ->
		{
			resp.status(500);
			StringWriter sw = new StringWriter();
			PrintWriter  pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			resp.body(gson.toJson(new Response(sw.toString())));
		});
	}
}
