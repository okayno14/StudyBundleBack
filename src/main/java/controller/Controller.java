package controller;

import configuration.ConfMain;
import controller.user.IUserController;
import controller.user.UserController;
import model.Core;


public class Controller
{
	IBundleController     bundleController;
	IBundleTypeController bundleTypeController;
	ICourseController     courseController;
	IGroupController      groupController;
	IRoleController roleController;
	IUserController userController;

	public Controller(ConfMain confMain)
	{
		//делает дела для настройки контроллера

		//Инициализация ядра
		Core core = new Core(confMain);

		//После сборки ядра собираем микроконтроллеры
		bundleController     = new BundleController(this, core.getBundleService());
		bundleTypeController = new BundleTypeController(this, core.getBundleTypeService());
		courseController     = new CourseController(this, core.getCourseService());
		groupController      = new GroupController(this, core.getGroupService());
		roleController       = new RoleController(this, core.getRoleService());
		userController       = new UserController(this, core.getUserService(),
												  core.getUserValidationService(),
												  confMain.getBusinessConfiguration());

		//ТЕСТЫ
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
