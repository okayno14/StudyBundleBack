package controller;

import business.IUserService;
import configuration.DateAccessConf;
import model.Core;


public class Controller
{
	IBundleController     bundleController;
	IBundleTypeController bundleTypeController;
	ICourseController courseController;
	IGroupController groupController;
	IRoleController roleController;
	IUserController userController;

	public Controller(DateAccessConf dateAccessConf)
	{
		//делает дела для настройки контроллера

		//Инициализация ядра
		Core core = new Core(dateAccessConf);

		//После сборки ядра собираем микроконтроллеры
		bundleController = new BundleController(this,core.getiBundleService());
		bundleTypeController = new BundleTypeController(this, core.getiBundleTypeService());
		courseController = new CourseController(this,core.getiCourseService());
		groupController = new GroupController(this,core.getiGroupService());
		roleController = new RoleController(this,core.getiRoleService());
		userController = new UserController(this,core.getiUserService());
	}

	public IBundleTypeController getBundleTypeController()
	{
		return bundleTypeController;
	}

	public IBundleController getBundleController()
	{
		return bundleController;
	}

	public ICourseController getCourseController()
	{
		return courseController;
	}

	public IGroupController getGroupController()
	{
		return groupController;
	}

	public IRoleController getRoleController()
	{
		return roleController;
	}

	public IUserController getUserController()
	{
		return userController;
	}
}
