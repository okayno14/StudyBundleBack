package view.HTTP;

import com.google.gson.Gson;
import configuration.ConfMain;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import controller.*;
import dataAccess.entity.BundleType;
import spark.Spark;

import java.util.List;

public class ServerFace
{
	private HTTP_Conf http_conf;
	private Gson      gson;

	private IBundleController     bundleController;
	private IBundleTypeController bundleTypeController;
	private ICourseController     courseController;
	private IGroupController      groupController;
	private IRoleController       roleController;
	private IUserController       userController;

	public ServerFace(HTTP_Conf http_conf, ConfMain confMain, Gson gson)
	{
		this.http_conf = http_conf;
		this.gson      = gson;
		//применяем параметры конфигурации для сервера
		Spark.port(http_conf.getPort());

		//строим контроллер
		Controller controller = new Controller(confMain);

		bundleController = controller.getBundleController();
		bundleTypeController =controller.getBundleTypeController();
		courseController = controller.getCourseController();
		groupController = controller.getGroupController();
		roleController = controller.getRoleController();
		userController = controller.getUserController();


		testBundleTypeService();

		//стартуем сервер
		//endpoints();
	}

	private void testBundleTypeService()
	{
		List<BundleType> res = bundleTypeController.get();
		List<BundleType> res1 = bundleTypeController.get();

		bundleTypeController.setClient(new BundleType("ПРГРГ"));
		bundleTypeController.add();
		bundleTypeController.delete();

		BundleType obj = bundleTypeController.get(3L);
		obj = bundleTypeController.get(156156156L);
	}

	public void endpoints()
	{
		Spark.get("/", (req, resp) ->
		{
			return "Hello";
		});
	}
}
