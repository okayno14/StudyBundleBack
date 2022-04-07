package controller;

import business.*;
import business.bundle.IBundleService;
import configuration.DateAccessConf;
import model.Core;

public class Controller implements IBundleService, ICourseService, IGroupService, IRoleService, IUserService, IBundleTypeService
{
	private IBundleService iBundleService;
	private IBundleTypeService iBundleTypeService;
	private ICourseService iCourseService;
	private IGroupService  iGroupService;
	private IRoleService   iRoleService;
	private IUserService   iUserService;

	private ControllerListener listener;


	public Controller(DateAccessConf dateAccessConf, ControllerListener listener)
	{
		//делает дела для настройки контроллера
		this.listener = listener;

		//Инициализация ядра
		Core core = new Core(dateAccessConf);

		//После сборки ядра можем получить реализации сервисов
		iBundleService = core.getiBundleService();
		iBundleTypeService = core.getiBundleTypeService();
		iCourseService = core.getiCourseService();
		iGroupService  = core.getiGroupService();
		iRoleService   = core.getiRoleService();
		iUserService   = core.getiUserService();
	}
}
