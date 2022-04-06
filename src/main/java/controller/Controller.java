package controller;

import business.ICourseService;
import business.IGroupService;
import business.IRoleService;
import business.IUserService;
import business.bundle.IBundleService;
import configuration.DateAccessConf;
import model.Core;

public class Controller implements IBundleService, ICourseService, IGroupService, IRoleService, IUserService
{
	private Core               core;
	private ControllerListener listener;

	private IBundleService iBundleService;
	private ICourseService iCourseService;
	private IGroupService  iGroupService;
	private IRoleService   iRoleService;
	private IUserService   iUserService;

	public Controller(DateAccessConf dateAccessConf, ControllerListener listener)
	{
		//делает дела для настройки контроллера
		this.listener = listener;

		//Инициализация ядра
		core = new Core(dateAccessConf);

		//После сборки ядра можем получить реализации сервисов
		iBundleService=core.getiBundleService();
		iCourseService=core.getiCourseService();
		iGroupService=core.getiGroupService();
		iRoleService=core.getiRoleService();
		iUserService=core.getiUserService();
	}
}
