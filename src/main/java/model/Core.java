package model;

import business.*;
import business.bundle.BundleService;
import business.bundle.IBundleService;
import configuration.BusinessConfiguration;
import configuration.ConfMain;
import configuration.DateAccessConf;
import dataAccess.cache.*;
import dataAccess.entity.*;
import dataAccess.repository.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.*;

public class Core
{
	private SessionFactory        sessionFactory;
	private DateAccessConf        dateAccessConf;
	private BusinessConfiguration businessConfiguration;

	private IBundleService        bundleService;
	private IBundleTypeService    bundleTypeService;
	private ICourseService        courseService;
	private IGroupService         groupService;
	private IRoleService          roleService;
	private IUserService          userService;
	private UserValidationService userValidationService = new UserValidationService();

	private void initHiber(String path)
	{
		File          file          = new File(path);
		Configuration configuration = new Configuration().configure(file);
		configuration.addAnnotatedClass(Route.class);
		configuration.addAnnotatedClass(Role.class);
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(Group.class);
		configuration.addAnnotatedClass(Course.class);
		configuration.addAnnotatedClass(CourseACL.class);
		configuration.addAnnotatedClass(CourseACLID.class);
		configuration.addAnnotatedClass(BundleType.class);
		configuration.addAnnotatedClass(Requirement.class);
		configuration.addAnnotatedClass(Bundle.class);
		configuration.addAnnotatedClass(BundleACL.class);
		configuration.addAnnotatedClass(BundleACLID.class);
		configuration.addAnnotatedClass(Report.class);
		sessionFactory = configuration.buildSessionFactory();
	}

	public Core(ConfMain confMain)
	{
		this.dateAccessConf = confMain.getDateAccessConf();
		initHiber(dateAccessConf.getHibernateConf());

		//сборка кэшей
		CacheController cacheController = new CacheController();

		IBundleCache      bundleCache;
		IBundleTypeCache  bundleTypeCache;
		ICourseCache      courseCache;
		IGroupCache       groupCache;
		IRequirementCache reqCache;
		IRoleCache        roleCache;
		IUserCache        userCache;

		bundleCache     = new BundleCache(cacheController);
		bundleTypeCache = new BundleTypeCache();
		courseCache     = new CourseCache(cacheController);
		groupCache      = new GroupCache(cacheController);
		reqCache        = new RequirementCache();
		roleCache       = new RoleCache();
		userCache       = new UserCache(cacheController);

		cacheController.setBundleCache(bundleCache);
		cacheController.setBundleTypeCache(bundleTypeCache);
		cacheController.setCourseCache(courseCache);
		cacheController.setGroupCache(groupCache);
		cacheController.setRequirementCache(reqCache);
		cacheController.setRoleCache(roleCache);
		cacheController.setUserCache(userCache);

		//сборка сервисов
		IBundleRepoFile bundleRepoFile = new BundleRepoFile(dateAccessConf.getStoragePath(),
															dateAccessConf.getSupportedFormats(),
															dateAccessConf.getZipFileSizeLimit());
		bundleService     = new BundleService(bundleRepoFile, new BundleRepoHiber(sessionFactory),
											   bundleCache);
		bundleTypeService = new BundleTypeService(new BundleTypeRepoHiber(sessionFactory),
												  bundleTypeCache);
		courseService     = new CourseService(new CourseRepoHiber(sessionFactory), courseCache,
											  new RequirementRepoHiber(sessionFactory), reqCache);
		groupService      = new GroupService(new GroupRepoHiber(sessionFactory), groupCache);
		roleService       = new RoleService(new RoleRepoHiber(sessionFactory), roleCache,
											confMain.getBusinessConfiguration()
													 .getReservedRoleId());
		userService       = new UserService(new UserRepoHiber(sessionFactory), userCache);
	}

	public IBundleService getBundleService()
	{
		return bundleService;
	}

	public IBundleTypeService getBundleTypeService()
	{
		return bundleTypeService;
	}

	public ICourseService getCourseService()
	{
		return courseService;
	}

	public IGroupService getGroupService()
	{
		return groupService;
	}

	public IRoleService getRoleService()
	{
		return roleService;
	}

	public IUserService getUserService()
	{
		return userService;
	}

	public UserValidationService getUserValidationService()
	{
		return userValidationService;
	}
}
