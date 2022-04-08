package view.HTTP;

import com.google.gson.Gson;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import controller.*;
import dataAccess.entity.BundleType;
import org.hibernate.exception.ConstraintViolationException;
import spark.Spark;

import javax.persistence.PersistenceException;
import java.util.Iterator;
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

	public ServerFace(HTTP_Conf http_conf, DateAccessConf dateAccessConf, Gson gson)
	{
		this.http_conf = http_conf;
		this.gson      = gson;
		//применяем параметры конфигурации для сервера
		Spark.port(http_conf.getPort());

		//строим контроллер
		Controller controller = new Controller(dateAccessConf);

		bundleController = controller.getBundleController();
		bundleTypeController =controller.getBundleTypeController();
		courseController = controller.getCourseController();
		groupController = controller.getGroupController();
		roleController = controller.getRoleController();
		userController = controller.getUserController();


		testServices();
		//стартуем сервер
		//endpoints();
	}

	private void testServices()
	{
		List<BundleType> res = bundleTypeController.get();
		List<BundleType> res1 = bundleTypeController.get();





		bundleTypeController.setClient(new BundleType("ПРГРГ"));
		bundleTypeController.add();

		bundleTypeController.delete();


	}

	public void endpoints()
	{
		Spark.get("/", (req, resp) ->
		{
			return "Hello";
		});
	}
}
