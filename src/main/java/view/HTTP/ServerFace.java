package view.HTTP;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import configuration.ConfMain;
import configuration.HTTP_Conf;
import controller.*;
import dataAccess.entity.*;
import exception.Business.BusinessException;
import exception.Business.NoSuchCourseStateAction;
import exception.Controller.ControllerException;
import exception.Controller.TokenNotFound;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;
import exception.DataAccess.ZipDamaged;
import exception.DataAccess.ZipFileSizeException;
import parser.JSON.CreateObjReqParser;
import parser.JSON.LoginReqParser;
import parser.JSON.ResponseParser;
import parser.JSON.entity.*;
import spark.Request;
import spark.Spark;
import view.HTTP.request.CreateObjReq;
import view.HTTP.request.IDReq;
import view.HTTP.request.LoginReq;

import javax.servlet.MultipartConfigElement;
import java.io.InputStream;
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
	private              HTTP_Conf http_conf;
	private final        int       zipFileSizeLimit;
	private final        String    any                            = "ANY";
	private static final int       OK                             = 200;
	private static final int       NO_RIGHT_FOR_OPERATION         = 403;
	private static final int       AUTHENTICATION_ERROR           = 401;
	private static final int       USER_DATA_NOT_VALID            = 400;
	private static final int       INTERNAL_CRITICAL_SERVER_ERROR = 500;
	private static final int       SEMANTIC_ERROR                 = 422;
	private static final int       OBJECT_NOT_FOUND               = 404;

	private Controller            controller;
	private IBundleController     bundleController;
	private IBundleTypeController bundleTypeController;
	private ICourseController     courseController;
	private IGroupController      groupController;
	private IUserController       userController;
	private IRoleController       roleController;

	private GsonBuilder gsonBuilder;
	private Gson        gson;
	private UserParser  userParser;

	public ServerFace(ConfMain confMain, Gson gson, GsonBuilder gsonBuilder)
	{
		this.http_conf   = confMain.getHttp_conf();
		zipFileSizeLimit = confMain.getDateAccessConf().getZipFileSizeLimit();

		//применяем параметры конфигурации для сервера
		Spark.port(http_conf.getPort());

		//строим контроллер
		controller           = new Controller(confMain);
		bundleController     = controller.getBundleController();
		bundleTypeController = controller.getBundleTypeController();
		courseController     = controller.getCourseController();
		groupController      = controller.getGroupController();
		userController       = controller.getUserController();
		roleController       = controller.getRoleController();

		//регистрация парсеров JSON для серверных записей
		this.gson        = gson;
		this.gsonBuilder = gsonBuilder;
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
				halt(NO_RIGHT_FOR_OPERATION, "У пользователя нет права на это действие");
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
			halt(NO_RIGHT_FOR_OPERATION, "У пользователя нет права на это действие");
		}
		catch (ControllerException e)
		{
			if (e.getCause() instanceof TokenNotFound)
			{
				halt(AUTHENTICATION_ERROR, "Закончился срок аренды токена");
			}
		}
		return null;
	}

	private String setGroupToUser(Request req, spark.Response resp, Group group)
	{
		IDReq idReq = gson.fromJson(req.body(), IDReq.class);
		if (group != null)
		{
			groupController.addUsers(group, idReq.getArr());
		}
		else
		{
			groupController.deleteUsers(idReq.getArr());
		}
		resp.status(OK);
		return gson.toJson(new Response("Успех"));
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

		path("/user", () ->
		{
			post("/", ((req, resp) ->
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
				resp.status(OK);
				return gson.toJson(new Response(jsonArray));
			}));

			put("/login", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				LoginReq loginReq = gson.fromJson(req.body(), LoginReq.class);
				if (userController
						.login(token, tokenExpires, loginReq.getEmail(), loginReq.getPass()))
				{
					resp.status(OK);
					JsonElement data = gson.toJsonTree(userController.getByToken(token));
					return gson.toJson(new Response(data, "Успешно"));
				}
				resp.status(AUTHENTICATION_ERROR);
				return gson.toJson(new Response(
						"У пользователя уже есть токен. Или данные не введены корректно"));
			});

			put("/logout", (req, resp) ->
			{
				User   client = authentAuthorize(req, resp);
				String token  = client.getToken();
				userController.logout(token);
				req.session().removeAttribute("token");
				return gson.toJson(new Response("Успешно"));
			});

			//Добавить
			delete("/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();
				long   id           = Long.parseLong(req.params("id"));
				User   toDel        = userController.get(id);
				userController.delete(toDel);

				//можем не найти по id toDel
				return "Empty";
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
						resp.status(OK);
						return gson.toJson(new Response(gson.toJsonTree(group), "Успех"));
					}
					catch (DataAccessException e)
					{
						resp.status(SEMANTIC_ERROR);
						return gson.toJson(new Response(e.getCause().getMessage()));
					}

				}));

				put("/addStudents/:id", ((req, resp) ->
				{
					User   client       = authentAuthorize(req, resp);
					String token        = client.getToken();
					long   tokenExpires = client.getTokenExpires();
					Group  group        = groupController.get(Long.parseLong(req.params("id")));
					return setGroupToUser(req, resp, group);
				}));

				put("/delStudents", ((req, resp) ->
				{
					User   client       = authentAuthorize(req, resp);
					String token        = client.getToken();
					long   tokenExpires = client.getTokenExpires();
					Group  group        = null;
					return setGroupToUser(req, resp, group);
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
						resp.status(OK);
						return gson.toJson(new Response("Успех"));
					}
					catch (DataAccessException e)
					{
						resp.status(OBJECT_NOT_FOUND);
						return gson.toJson(new Response(
								"Объект не найден\n " + e.getCause().getMessage()));
					}
				});
			});
		});

		path("/course", () ->
		{
			post("/", (req, resp) ->
			{
				User client = authentAuthorize(req, resp);

				CreateObjReq createObjReq = gson.fromJson(req.body(), CreateObjReq.class);
				Course c = new Course(createObjReq.getName(),
									  userController.get(createObjReq.getId()));
				courseController.add(c);
				resp.status(OK);
				return gson.toJson(new Response(gson.toJsonTree(c), "Успех"));
			});

			put("/addGroup/:groupID/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long   groupID  = Long.parseLong(req.params(":groupID"));
				long   courseID = Long.parseLong(req.params(":id"));
				Group  g        = groupController.get(groupID);
				Course c        = courseController.get(courseID);

				courseController.addGroup(c, g);

				return gson.toJson(new Response(gson.toJsonTree(c), "Успех"));
			});

			put("/publish/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();
				long   courseID     = Long.parseLong(req.params(":id"));

				Course c = courseController.get(courseID);
				courseController.publish(c);
				return gson.toJson(new Response(gson.toJsonTree(c), "Успех"));
			});

			path("/requirement", () ->
			{
				post("/:courseID/:bundleTypeID/:q", (req, resp) ->
				{
					User   client       = authentAuthorize(req, resp);
					String token        = client.getToken();
					long   tokenExpires = client.getTokenExpires();

					long courseID     = Long.parseLong(req.params("courseID"));
					long bundleTypeID = Long.parseLong(req.params("bundleTypeID"));
					int  q            = Integer.parseInt(req.params("q"));

					BundleType bt     = bundleTypeController.get(bundleTypeID);
					Course     course = courseController.get(courseID);

					try
					{
						courseController.addRequirement(course, bt, q);
						resp.status(OK);
						return gson.toJson(new Response(gson.toJsonTree(course), "Успех"));
					}
					catch (BusinessException e)
					{
						resp.status(SEMANTIC_ERROR);
						return gson.toJson(new Response(e.getCause().getMessage()));
					}
				});

				put("/:courseID/:bundleTypeID/:q", (req, resp) ->
				{

					return "F";
				});

				delete("/:courseID/:bundleTypeID/:q", (req, resp) ->
				{

					return "F";
				});
			});


		});

		path("/bundle", () ->
		{
			post("/upload/:id", (req, resp) ->
			{
				//				User   client       = authentAuthorize(req, resp);
				//				String token        = client.getToken();
				//				long   tokenExpires = client.getTokenExpires();

				long   bundleID = Long.parseLong(req.params("id"));
				Bundle b        = bundleController.get(bundleID);

				req.attribute("org.eclipse.jetty.multipartConfig",
							  new MultipartConfigElement("/temp"));
				try (InputStream is = req.raw().getPart("uploaded_bundle").getInputStream())
				{
					if (is.available() <= zipFileSizeLimit)
					{
						int  c     = is.available();
						byte buf[] = new byte[is.available()];
						is.read(buf);
						Bundle bestMatch = bundleController.uploadReport(b, buf);
						//запилиить вывод с инфой по похожести и отправить пользователю изменённый бандл

						String      message = "";
						JsonElement data    = null;
						if (b.getState() == BundleState.ACCEPTED)
						{
							message = "Отчёт успешно прошёл проверку";
							data    = gson.toJsonTree(b);
						}
						else if (b.getState() == BundleState.CANCELED)
						{
							message = "Отчёт недостаточно оригинален.\n";
							Bundle arr[] = new Bundle[2];
							arr[0] = b;
							arr[1] = bestMatch;
									 data = gson.toJsonTree(arr);
						}
						return new Response(data, message);
					}
				}
				return "fail";

			});
		});


		exception(NumberFormatException.class, (e, req, resp) ->
		{
			resp.status(USER_DATA_NOT_VALID);
			resp.body(gson.toJson(new Response("Неправильный формат ID")));
		});

		exception(DataAccessException.class, (e, req, resp) ->
		{
			if (e.getCause().getClass() == ObjectNotFoundException.class)
			{
				resp.status(OBJECT_NOT_FOUND);
				resp.body(gson.toJson(new Response("Объект с запрошенным id не найден")));
			}
			if (e.getCause().getClass() == ZipDamaged.class ||
					e.getCause().getClass() == ZipFileSizeException.class)
			{
				resp.status(USER_DATA_NOT_VALID);
				resp.body(gson.toJson(new Response(e.getMessage())));
			}
		});

		exception(BusinessException.class, (e, req, resp) ->
		{
			if (e.getCause().getClass() == NoSuchCourseStateAction.class)
			{
				resp.status(SEMANTIC_ERROR);
				resp.body(gson.toJson(new Response(e.getMessage())));
			}
		});

		//Если ничего не получилось найти, то швыряем стак трэйс в клиентский код
		exception(Exception.class, (e, req, resp) ->
		{
			resp.status(INTERNAL_CRITICAL_SERVER_ERROR);
			StringWriter sw = new StringWriter();
			PrintWriter  pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			resp.body(gson.toJson(new Response(sw.toString())));
		});
	}
}
