package view.HTTP;

import business.*;
import business.bundle.IBundleService;
import com.google.gson.Gson;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import controller.Controller;
import controller.ControllerListener;

public class ServerFace implements ControllerListener
{
	private HTTP_Conf http_conf;
	private Gson gson;

	private IBundleService iBundleService;
	private IBundleTypeService iBundleTypeService;
	private ICourseService iCourseService;
	private IGroupService  iGroupService;
	private IRoleService   iRoleService;
	private IUserService   iUserService;

	public ServerFace(HTTP_Conf http_conf, DateAccessConf dateAccessConf, Gson gson)
	{
		this.http_conf = http_conf;
		this.gson=gson;
		//применяем параметры конфигурации для сервера

		//строим контроллер
		Controller controller = new Controller(dateAccessConf, this);
		iBundleService = controller;
		iBundleTypeService = controller;
		iCourseService = controller;
		iGroupService  = controller;
		iRoleService   = controller;
		iUserService   = controller;

		//стартуем сервер
		endpoints();
	}

	public static void endpoints()
	{
		System.out.println("Стартовал сервер");
	}
}
