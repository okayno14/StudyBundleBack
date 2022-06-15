package view.HTTP;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import configuration.ConfMain;
import configuration.HTTP_Conf;
import controller.*;
import dataAccess.entity.*;
import exception.Business.BusinessException;
import exception.Business.DeletingImportantData;
import exception.Business.NoRightException;
import exception.Business.NoSuchStateAction;
import exception.Controller.ControllerException;
import exception.Controller.TokenNotFound;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.FileNotFoundException;
import exception.DataAccess.ObjectNotFoundException;
import parser.JSON.CreateObjReqParser;
import parser.JSON.LoginReqParser;
import parser.JSON.ResponseParser;
import parser.JSON.entity.*;
import spark.Request;
import spark.Spark;
import view.HTTP.request.CreateObjReq;
import view.HTTP.request.IDReq;
import view.HTTP.request.LoginReq;
import view.HTTP.response.Response;
import view.HTTP.response.data.BundleAnalysisResult;
import view.LoggerBuilder;
import view.mail.MailAgent;

import javax.mail.MessagingException;
import javax.servlet.MultipartConfigElement;
import java.io.*;
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

	private LoggerBuilder logBuilder;
	private MailAgent     mailAgent;

	private Controller            controller;
	private IBundleController     bundleController;
	private IBundleTypeController bundleTypeController;
	private ICourseController     courseController;
	private IGroupController      groupController;
	private IUserController       userController;
	private IRoleController       roleController;

	private GsonBuilder  gsonBuilder;
	private Gson         gson;
	private UserParser   userParser;
	private BundleParser bundleParser;
	private CourseParser courseParser;

	private Translit translit = new Translit();

	public ServerFace(ConfMain confMain, Gson gson, GsonBuilder gsonBuilder,
					  LoggerBuilder logBuilder) throws MessagingException
	{
		this.logBuilder = logBuilder;
		logBuilder.build(confMain.getLogConf().getLog4jConfPath());

		this.mailAgent = new MailAgent(confMain.getMailConf());

		this.http_conf   = confMain.getHttp_conf();
		zipFileSizeLimit = confMain.getDateAccessConf().getZipFileSizeLimit();

		//применяем параметры конфигурации для сервера
		Spark.port(http_conf.getPort());
		Spark.externalStaticFileLocation("resources/Web");

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
		CourseACL_Parser courseACL_parser = new CourseACL_Parser(gson, userParser);
		gsonBuilder.registerTypeAdapter(CourseACL.class, courseACL_parser);
		courseParser = new CourseParser(gson, courseACL_parser);
		gsonBuilder.registerTypeAdapter(Course.class, courseParser);
		BundleACLParser bundleACLParser = new BundleACLParser(gson, userParser);
		gsonBuilder.registerTypeAdapter(BundleACL.class, bundleACLParser);
		bundleParser = new BundleParser(bundleACLParser);
		gsonBuilder.registerTypeAdapter(Bundle.class, bundleParser);


		//стартуем сервер
		endpoints();
	}

	private void preAuthentication(Request req, spark.Response resp)
	{
		//Если сессия новая, то создаём её и унифициируем значение токена
		User    client          = null;
		boolean isNew           = req.session().isNew();
		String  sessID          = req.session().id();
		boolean NoTokenInCookie = !req.session().attributes().contains("token");
		if (isNew || NoTokenInCookie)
		{
			req.session(true);
			client = userController.getGuestUser();
			req.session().attribute("token", client.getToken());
		}
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
			Route firstRoleRoute = client.getRole().getRouteList().get(0);
			if (firstRoleRoute.getMethod().toString().equals(any) &&
					firstRoleRoute.getUrn().equals(any))
			{
				return client;
			}

			Set<String>  reqParams = req.params().keySet();
			String       req_URN   = req.uri();
			StringBuffer reqName   = new StringBuffer();

			Pattern      pattern = Pattern.compile("/[a-zA-z0-9]+");
			Matcher      matcher = pattern.matcher(req_URN);
			List<String> parts   = new LinkedList<>();
			while (matcher.find())
			{
				parts.add(matcher.group());
			}
			Iterator<String> iter = parts.iterator();
			for (int i = 0; i < parts.size() - reqParams.size() && iter.hasNext(); i++)
			{
				reqName.append(iter.next());
			}

			for (Route roleRoute : client.getRole().getRouteList())
			{
				String  roleRoute_urn = roleRoute.getUrn().toLowerCase();
				boolean flag          = true;

				if (roleRoute_urn.equalsIgnoreCase(any))
				{
					flag = flag && true;
				}
				else
				{
					for (String reqParam : reqParams)
					{
						flag = flag && roleRoute_urn.contains(reqParam);
					}
					flag = flag && roleRoute_urn.contains(reqName.toString().toLowerCase());
				}

				if (roleRoute.getMethod().toString().equals(any))
				{
					flag = flag && true;
				}
				else
				{
					flag = flag && roleRoute.getMethod().toString().equals(req.requestMethod());
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

	private JsonObject parseWithFilterAndDefence(Bundle bundle)
	{
		JsonObject jsonObject = gson.toJsonTree(bundle, Bundle.class).getAsJsonObject();
		bundleParser.defend(jsonObject);
		bundleParser.filter(jsonObject, bundle);
		return jsonObject;
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
			resp.header("Access-Control-Expose-Headers", "true");

			if (!req.requestMethod().equals("OPTIONS"))
			{
				preAuthentication(req, resp);
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
				//аутентифицировались как гость
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				LoginReq loginReq = gson.fromJson(req.body(), LoginReq.class);
				if (userController.login(token, tokenExpires, loginReq.getEmail(),
										 loginReq.getPass()))
				{
					resp.status(OK);
					JsonElement data = gson.toJsonTree(userController.getByToken(token));
					return gson.toJson(new Response(data, "Успешно"));
				}
				halt(AUTHENTICATION_ERROR,
					 "Неверные данные УЗ или " + "найденный пользователь аутентифицирован или " +
							 "у найденного пользователя неактивирована УЗ");
				return "";
			});

			get("/me", (req, resp) ->
			{
				User client = authentAuthorize(req, resp);
				resp.status(OK);
				return gson.toJson(new Response(gson.toJsonTree(client), "Успешно"));
			});

			put("/logout", (req, resp) ->
			{
				User   client = authentAuthorize(req, resp);
				String token  = client.getToken();
				userController.logout(token);
				req.session().removeAttribute("token");
				return gson.toJson(new Response("Успешно"));
			});

			put("/confirm/:email", (req, resp) ->
			{
				//preAuthentication(req, resp);
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				String email = req.params("email");
				User   user  = userController.get(email);
				if (user.isEmailState())
				{
					halt(SEMANTIC_ERROR,"Запрошенная УЗ активирована");
				}
				String activateRequest = req.host() + "/user/activate/" + user.getId();
				mailAgent.sendConfirmMail(activateRequest, user.getEmail());
				resp.status(OK);
				return "";
			});

			//Добавить
			delete("/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();
				long   id           = Long.parseLong(req.params("id"));
				User   toDel        = userController.get(id);
				userController.delete(client, toDel);

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

				get("/students/:id", (req, resp) ->
				{
					User   client       = authentAuthorize(req, resp);
					String token        = client.getToken();
					long   tokenExpires = client.getTokenExpires();

					long  groupID = Long.parseLong(req.params("id"));
					Group group   = groupController.get(groupID);

					Set<User> res = groupController.getUsers(group);

					JsonArray jsonArray = new JsonArray();
					for (User user : res)
					{
						JsonObject jsonObject = gson.toJsonTree(user).getAsJsonObject();
						userParser.defendData(jsonObject);
						jsonArray.add(jsonObject);
					}

					resp.status(OK);
					return gson.toJson(new Response(jsonArray));
				});

				put("/addStudents/:id", ((req, resp) ->
				{
					User   client       = authentAuthorize(req, resp);
					String token        = client.getToken();
					long   tokenExpires = client.getTokenExpires();
					Group  group        = groupController.get(Long.parseLong(req.params("id")));
					return setGroupToUser(req, resp, group);
				}));

				delete("/:id", (req, resp) ->
				{
					try
					{
						User  client = authentAuthorize(req, resp);
						long  id     = Long.parseLong(req.params("id"));
						Group toDel  = groupController.get(id);
						groupController.delete(toDel);
						resp.status(OK);
						return gson.toJson(new Response("Успех"));
					}
					catch (DataAccessException e)
					{
						resp.status(OBJECT_NOT_FOUND);
						return gson.toJson(
								new Response("Объект не найден\n " + e.getCause().getMessage()));
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

			get("/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long courseID = Long.parseLong(req.params("id"));

				Course res = courseController.get(courseID);

				JsonObject resJSON = gson.toJsonTree(res).getAsJsonObject();
				courseParser.filterGroupStudents(resJSON);
				courseParser.defend(resJSON);

				resp.status(OK);
				return gson.toJson(new Response(resJSON));
			});

			get("/owner/:ownerID", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long ownerID = Long.parseLong(req.params("ownerID"));

				User owner = client;

				if (ownerID != client.getId())
				{
					owner = userController.get(ownerID);
				}

				List<Course> res     = courseController.getByOwner(owner);
				JsonArray    jsonArr = gson.toJsonTree(res).getAsJsonArray();
				for (JsonElement json : jsonArr)
				{
					JsonObject resJSON = json.getAsJsonObject();
					courseParser.filterGroupStudents(resJSON);
					courseParser.defend(resJSON);
				}

				resp.status(OK);
				return gson.toJson(new Response(jsonArr));
			});

			get("/group/:groupID", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long  groupID = Long.parseLong(req.params("groupID"));
				Group g       = groupController.get(groupID);

				List<Course> res     = courseController.getByGroup(g);
				JsonArray    jsonArr = gson.toJsonTree(res).getAsJsonArray();
				for (JsonElement json : jsonArr)
				{
					JsonObject resJSON = json.getAsJsonObject();
					courseParser.filterGroupStudents(resJSON);
					courseParser.defend(resJSON);
				}

				resp.status(OK);
				return gson.toJson(new Response(jsonArr));
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

				courseController.addGroup(client, c, g);

				return gson.toJson(new Response(gson.toJsonTree(c), "Успех"));
			});

			put("/delGroup/:groupID/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long   groupID  = Long.parseLong(req.params(":groupID"));
				long   courseID = Long.parseLong(req.params(":id"));
				Group  g        = groupController.get(groupID);
				Course c        = courseController.get(courseID);

				courseController.delGroup(client, c, g);

				return "f";
			});

			put("/publish/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long courseID = Long.parseLong(req.params(":id"));

				Course c = courseController.get(courseID);
				courseController.publish(client, c);
				return gson.toJson(new Response(gson.toJsonTree(c), "Успех"));
			});

			delete("/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long               courseID   = Long.parseLong(req.params(":id"));
				Course             c          = courseController.get(courseID);
				LinkedList<Course> courseList = new LinkedList<>();
				courseList.add(c);

				courseController.delete(client, courseList);

				return "f";
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
						courseController.addRequirement(client, course, bt, q);
						resp.status(OK);
						return gson.toJson(new Response(gson.toJsonTree(course), "Успех"));
					}
					catch (BusinessException e)
					{
						resp.status(SEMANTIC_ERROR);
						return gson.toJson(new Response(e.getCause().getMessage()));
					}
				});

				delete("/:courseID/:reqID", (req, resp) ->
				{
					User   client       = authentAuthorize(req, resp);
					String token        = client.getToken();
					long   tokenExpires = client.getTokenExpires();

					long courseID = Long.parseLong(req.params("courseID"));
					long reqID    = Long.parseLong(req.params("reqID"));

					Course      course      = courseController.get(courseID);
					Requirement requirement = courseController.getReq(reqID);

					courseController.deleteRequirement(client, course, requirement);

					resp.status(OK);
					return gson.toJson(new Response("Успех"));
				});
			});
		});

		path("/bundle", () ->
		{
			post("/upload/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long   bundleID = Long.parseLong(req.params("id"));
				Bundle b        = bundleController.get(bundleID);

				req.attribute("org.eclipse.jetty.multipartConfig",
							  new MultipartConfigElement("/temp"));
				byte buf[] = new byte[0];
				try (InputStream is = req.raw().getPart("uploaded_bundle").getInputStream())
				{
					if (is.available() <= zipFileSizeLimit)
					{
						buf = new byte[is.available()];
						is.read(buf);
					}
				}
				catch (IOException e)
				{
					resp.status(USER_DATA_NOT_VALID);
					return "Ошибка при чтении архива";
				}
				try
				{
					Bundle bestMatch = bundleController.uploadReport(client, b, buf);

					BundleAnalysisResult bundleAnalysisResult = new BundleAnalysisResult();
					bundleAnalysisResult.setBundle(parseWithFilterAndDefence(b));
					bundleAnalysisResult.setBestMatch(parseWithFilterAndDefence(bestMatch));
					bundleAnalysisResult.setPercent(b.getSimScore());

					resp.status(OK);
					return gson.toJson(new Response(gson.toJsonTree(bundleAnalysisResult)));
				}
				catch (DataAccessException e)
				{
					if (e.getCause().getClass() == FileNotFoundException.class)
					{
						resp.status(INTERNAL_CRITICAL_SERVER_ERROR);
						return "Ошибка чтения файлов анализа";
					}
					else
					{
						throw e;
					}
				}
			});

			get("/download/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long   bundleID = Long.parseLong(req.params("id"));
				Bundle b        = bundleController.get(bundleID);


				byte buf[] = bundleController.downloadReport(client, b);

				String fileOutName = b.getFolder();
				fileOutName.replace("/", "_");
				fileOutName = fileOutName + ".bow";
				fileOutName = fileOutName.replace("/", "_");
				fileOutName = fileOutName.replace(" ", "_");

				fileOutName = translit.cyr2lat(fileOutName);

				resp.header("Content-Type", "application/zip");
				resp.header("Content-Disposition", "attachment; filename=" + fileOutName);


				try (OutputStream out = resp.raw().getOutputStream())
				{
					out.write(buf);
				}

				return resp.raw();
			});


			get("/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long bundleID = Long.parseLong(req.params("id"));

				Bundle res = bundleController.get(bundleID);

				JsonObject bundleJSON = parseWithFilterAndDefence(res);

				resp.status(OK);
				return gson.toJson(new Response(bundleJSON, "Успех"));
			});

			get("/:courseID/:ownerID", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long courseID = Long.parseLong(req.params("courseID"));
				long ownerID  = Long.parseLong(req.params("ownerID"));

				Course       c     = courseController.get(courseID);
				User         owner = userController.get(ownerID);
				List<Bundle> res   = bundleController.get(c, owner);

				JsonArray jsonArray = new JsonArray();

				for (Bundle bundle : res)
				{
					jsonArray.add(parseWithFilterAndDefence(bundle));
				}

				resp.status(OK);
				return gson.toJson(new Response(jsonArray));
			});


			put("/accept/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long   bundleID = Long.parseLong(req.params("id"));
				Bundle bundle   = bundleController.get(bundleID);

				bundleController.accept(client, bundle);
				JsonObject jsonObject = parseWithFilterAndDefence(bundle);

				resp.status(OK);
				return gson.toJson(new Response(jsonObject, "Успешно"));
			});

			put("/cancel/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long   bundleID = Long.parseLong(req.params("id"));
				Bundle bundle   = bundleController.get(bundleID);

				bundleController.cancel(client, bundle);
				JsonObject jsonObject = parseWithFilterAndDefence(bundle);

				resp.status(OK);
				return gson.toJson(new Response(jsonObject, "Успешно"));
			});

			delete("/:id", (req, resp) ->
			{
				User   client       = authentAuthorize(req, resp);
				String token        = client.getToken();
				long   tokenExpires = client.getTokenExpires();

				long   bundleID = Long.parseLong(req.params("id"));
				Bundle bundle   = bundleController.get(bundleID);

				bundleController.emptify(client, bundle);

				JsonObject jsonObject = parseWithFilterAndDefence(bundle);

				resp.status(OK);
				return gson.toJson(new Response(jsonObject, "Успешно"));
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
			if (e.getCause().getClass() == FileNotFoundException.class ||
					e.getCause().getClass() == IOException.class)
			{
				resp.status(INTERNAL_CRITICAL_SERVER_ERROR);
				resp.body(gson.toJson(new Response(e.getCause().getMessage())));
			}
			//			if (e.getCause().getClass() == ZipDamaged.class ||
			//					e.getCause().getClass() == ZipFileSizeException.class ||
			//					e.getCause().getClass() == FormatNotSupported.class)
			else
			{
				resp.status(USER_DATA_NOT_VALID);
				resp.body(gson.toJson(new Response(e.getMessage())));
			}
		});

		exception(BusinessException.class, (e, req, resp) ->
		{
			if (e.getCause().getClass() == NoRightException.class ||
					e.getCause().getClass() == DeletingImportantData.class)
			{
				resp.status(NO_RIGHT_FOR_OPERATION);
			}
			if (e.getCause().getClass() == NoSuchStateAction.class)
			{
				resp.status(SEMANTIC_ERROR);
			}
			resp.body(gson.toJson(new Response(e.getMessage())));
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
