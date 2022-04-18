package view.HTTP;

import com.google.gson.Gson;
import configuration.ConfMain;
import configuration.HTTP_Conf;
import controller.*;
import dataAccess.entity.BundleType;
import dataAccess.entity.User;
import exception.Controller.ControllerException;
import exception.Controller.TokenNotFound;
import spark.Spark;
import view.HTTP.request.LoginReq;

import java.util.List;

import static spark.Spark.*;

public class ServerFace
{
	private Controller controller;

	private HTTP_Conf http_conf;
	private Gson      gson;

	private IBundleController     bundleController;
	private IBundleTypeController bundleTypeController;
	private ICourseController     courseController;
	private IGroupController      groupController;
	private IUserController       userController;
	private IRoleController       roleController;

	public ServerFace(HTTP_Conf http_conf, ConfMain confMain, Gson gson)
	{
		this.http_conf = http_conf;
		this.gson      = gson;
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

		//testBundleTypeService();

		//стартуем сервер
		endpoints();
	}

	private void testBundleTypeService()
	{
		List<BundleType> res  = bundleTypeController.get();
		List<BundleType> res1 = bundleTypeController.get();

		bundleTypeController.setClient(new BundleType("ПРГРГ"));
		bundleTypeController.add();
		bundleTypeController.delete();

		BundleType obj = bundleTypeController.get(3L);
		//obj = bundleTypeController.get(156156156L);
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

				if (req.session().isNew())
				{
					req.session(true);
					client = userController.getGuestUser();
					req.session().attribute("token", client.getToken());
				}
				else
				{
					client = userController.getByToken(req.session().attribute("token"));
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
			String   token    = req.session().attribute("token");
			LoginReq loginReq = gson.fromJson(req.body(), LoginReq.class);
			if (userController.login(token, loginReq.getEmail(), loginReq.getPass()))
			{
				resp.status(200);
				String test = gson.toJson(new Response("Success"));
				return test;
			}
			resp.status(401);
			return gson.toJson(new Response(
					"У пользователя уже есть токен. Или данные не введены корректно"));
		});
	}
}
