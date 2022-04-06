package view.HTTP;

import business.ICourseService;
import business.IGroupService;
import business.IRoleService;
import business.IUserService;
import business.bundle.IBundleService;
import com.google.gson.Gson;
import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import controller.Controller;
import controller.ControllerListener;

public class ServerFace implements ControllerListener
{
	private HTTP_Conf http_conf;


	private IBundleService iBundleService;
	private ICourseService iCourseService;
	private IGroupService  iGroupService;
	private IRoleService   iRoleService;
	private IUserService   iUserService;

	private Gson gson;

	public ServerFace(HTTP_Conf http_conf, DateAccessConf dateAccessConf, Gson gson)
	{
		this.http_conf = http_conf;
		this.gson=gson;
		//применяем параметры конфигурации

		//строим контроллер
		Controller controller = new Controller(dateAccessConf, this);
		iBundleService = controller;
		iCourseService = controller;
		iGroupService  = controller;
		iRoleService   = controller;
		iUserService   = controller;

		//стартуем сервер

	}
}
