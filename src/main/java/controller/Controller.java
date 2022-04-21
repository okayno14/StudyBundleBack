package controller;

import configuration.ConfMain;
import dataAccess.entity.User;
import model.Core;


public class Controller
{
	private User client;

	IBundleController     bundleController;
	IBundleTypeController bundleTypeController;
	ICourseController     courseController;
	IGroupController      groupController;
	IRoleController       roleController;
	IUserController       userController;

	public Controller(ConfMain confMain)
	{
		//делает дела для настройки контроллера

		//Инициализация ядра
		Core core = new Core(confMain);

		//После сборки ядра собираем микроконтроллеры
		bundleController     = new BundleController(this, core.getiBundleService());
		bundleTypeController = new BundleTypeController(this, core.getiBundleTypeService());
		courseController     = new CourseController(this, core.getiCourseService());
		groupController      = new GroupController(this, core.getiGroupService());
		roleController       = new RoleController(this, core.getiRoleService());
		userController       = new UserController(this, core.getiUserService(),
												  core.getUserValidationService());

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

	public User getClient()
	{
		return client;
	}

	public void setClient(User client)
	{
		this.client = client;
	}
}
