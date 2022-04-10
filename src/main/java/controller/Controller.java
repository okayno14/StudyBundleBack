package controller;

import configuration.ConfMain;
import model.Core;


public class Controller
{
	IBundleController     bundleController;
	IBundleTypeController bundleTypeController;
	ICourseController courseController;
	IGroupController groupController;
	IUserController userController;

	public Controller(ConfMain confMain)
	{
		//делает дела для настройки контроллера

		//Инициализация ядра
		Core core = new Core(confMain);

		//После сборки ядра собираем микроконтроллеры
		bundleController = new BundleController(this,core.getiBundleService());
		bundleTypeController = new BundleTypeController(this, core.getiBundleTypeService());
		courseController = new CourseController(this,core.getiCourseService());
		groupController = new GroupController(this,core.getiGroupService());
		userController = new UserController(this,core.getiUserService());

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

	public IUserController getUserController()
	{
		return userController;
	}
}
