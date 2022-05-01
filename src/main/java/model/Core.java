package model;

import business.*;
import business.bundle.BundleService;
import business.bundle.IBundleService;
import dataAccess.repository.WordParser;
import configuration.BusinessConfiguration;
import configuration.ConfMain;
import configuration.DateAccessConf;
import dataAccess.cache.*;
import dataAccess.entity.*;
import dataAccess.repository.*;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.util.*;

public class Core
{
	private SessionFactory        sessionFactory;
	private DateAccessConf        dateAccessConf;
	private BusinessConfiguration businessConfiguration;

	private IBundleService     iBundleService;
	private IBundleTypeService iBundleTypeService;
	private ICourseService     iCourseService;
	private IGroupService      iGroupService;
	private IRoleService       iRoleService;
	private IUserService       iUserService;
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
		iBundleService     = new BundleService(bundleRepoFile, new BundleRepoHiber(sessionFactory),
											   bundleCache);
		iBundleTypeService = new BundleTypeService(new BundleTypeRepoHiber(sessionFactory),
												   bundleTypeCache);
		iCourseService     = new CourseService(new CourseRepoHiber(sessionFactory), courseCache,
											   new RequirementRepoHiber(sessionFactory), reqCache);
		iGroupService      = new GroupService(new GroupRepoHiber(sessionFactory), groupCache);
		iRoleService       = new RoleService(new RoleRepoHiber(sessionFactory), roleCache,
											 confMain.getBusinessConfiguration()
													 .getReservedRoleId());
		iUserService       = new UserService(new UserRepoHiber(sessionFactory), userCache);

		//ТЕСТЫ

		//new CourseRepoHiber(sessionFactory).get(112L);

		//testBundleRepoFile(cacheController, bundleRepoFile);


		//		GroupRepoHiber groupRepoHiber = new GroupRepoHiber(sessionFactory);
		//		List<Group>    res            = groupRepoHiber.get("АВТ-815");
		//		groupRepoHiber.fetchStudents(res.get(0));
		//		cacheController.fetched(res.get(0));

		//		ICourseRepo courseRepo = new CourseRepoHiber(sessionFactory);
		//		User teacher = new User("Малявко", "Александр", "Антонович", "a.malyavko@corp.nstu.ru",
		//						roleCache.get(10L));
		//		teacher.setId(87L);
		//		List<Course> courseList = courseRepo.get(teacher,"Параллельное программирование");

		//		ICourseRepo courseRepo = new CourseRepoHiber(sessionFactory);
		//		User user = new User();
		//		user.setId(81L);
		//		courseRepo.getByStudent(user);

		//testCourse(sessionFactory);
		//IUserRepo userRepo = new UserRepoHiber(sessionFactory);
		//		userRepo.getByCourse(
		//				new User("Васильев", "Василий", "Васильевич", "some.mail@stud.nstu.ru", new Role()),
		//				"Информатика");
		//		userRepo.get("some.mail@stud.nstu.ru");


		//		User user = res.get(0).getStudents().iterator().next();
		//		user.setFirstName("ТЕСТ");
		//		userRepo.save(user);
	}

	private void testBundleRepoFile(CacheController cacheController, IBundleRepoFile bundleRepoFile)
	{
		WordParser wordParser = new WordParser();


		IRoleCache       roleCache       = cacheController.getRoleCache();
		IUserCache       userCache       = cacheController.getUserCache();
		IGroupCache      groupCache      = cacheController.getGroupCache();
		IBundleTypeCache bundleTypeCache = cacheController.getBundleTypeCache();
		ICourseCache     courseCache     = cacheController.getCourseCache();
		IBundleCache     bundleCache     = cacheController.getBundleCache();

		long sequence = 0L;

		User user = new User("Алексеев", "Александр", "Константинович",
							 "a.alekseev.2018@stud.nstu.ru", roleCache.get(11L));
		user.setId(++sequence);
		userCache.put(user);
		user = new User("Иванов", "Иван", "Иванович", "i.ivanov.2018@stud.nstu.ru",
						roleCache.get(11L));
		user.setId(++sequence);
		userCache.put(user);
		Group group = new Group("АВТ-815");
		group.setId(++sequence);
		long avt815 = group.getId();
		group.addStudent(userCache.get(sequence - 1));
		group.addStudent(userCache.get(sequence - 2));
		groupCache.put(group);
		user = new User("Малявко", "Александр", "Антонович", "a.malyavko@corp.nstu.ru",
						roleCache.get(10L));
		long teacher = sequence++;
		user.setId(teacher);
		userCache.put(user);

		Course course = new Course("Параллельное Программирование");
		course.setId(++sequence);
		long pp = sequence;
		course.addGroup(groupCache.get(avt815));
		course.addAuthor(userCache.get(teacher), Author.AUTHOR);
		course.addRequirement(new Requirement(4, bundleTypeCache.get(1L)));
		courseCache.put(course);

		course = courseCache.get(pp);
		Set<Requirement>      requirementSet      = course.getRequirementSet();
		Iterator<Requirement> requirementIterator = requirementSet.iterator();
		Requirement           req                 = null;
		long                  lab                 = sequence + 1;
		while (requirementIterator.hasNext())
		{
			req = requirementIterator.next();
			Iterator<Group> groupIterator = course.getGroupes().iterator();
			group = null;
			while (groupIterator.hasNext())
			{
				group = groupIterator.next();
				Iterator<User> userIterator = group.getStudents().iterator();
				user = null;
				while (userIterator.hasNext())
				{
					user = userIterator.next();
					for (int i = 1; i <= req.getQuantity(); i++)
					{
						Bundle bundle = new Bundle(i, course, req.getBundleType());
						bundle.addAuthor(user, Author.AUTHOR);
						bundle.setId(++sequence);

						bundleCache.put(bundle);
					}
				}
			}
		}


		try (FileInputStream fIN = new FileInputStream("src/test/doc.zip");
			 FileOutputStream fOUT = new FileOutputStream("src/test/docCopy.zip"))
		{
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream(fIN.available());
			int                   c       = 0;
			while ((c = fIN.read()) != -1)
			{
				byteOut.write(c);
			}
			//сохраняю бандл в файловую систему
			bundleRepoFile.save(bundleCache.get(lab), byteOut.toByteArray());
			//достаю бандл из файловой системы в виде архива
			byte[] arr = bundleRepoFile.get(bundleCache.get(lab));
			fOUT.write(arr);
			bundleRepoFile.delete(bundleCache.get(lab));
		}
		catch (java.io.FileNotFoundException fe)
		{
			fe.printStackTrace();
		}
		catch (IOException e)
		{

		}


	}

	private void testCourse(SessionFactory sessionFactory)
	{
		Map<Long, BundleType> bundleTypeCache = new HashMap<Long, BundleType>();
		Map<Long, Role>       roleCache       = new HashMap<Long, Role>();
		Map<Long, User>       userCache       = new HashMap<Long, User>();
		Map<Long, Group>      groupCache      = new HashMap<Long, Group>();
		Map<Long, Course>     courseCache     = new HashMap<Long, Course>();
		Map<Long, Bundle>     bundleCache     = new HashMap<Long, Bundle>();
		sessionFactory.getCurrentSession().beginTransaction();
		for (long i = 9; i <= 11; i++)
		{
			Role buf = sessionFactory.getCurrentSession().get(Role.class, i);
			roleCache.put(buf.getId(), buf);
		}

		String               HQL      = "from BundleType";
		Query                q        = sessionFactory.getCurrentSession().createQuery(HQL);
		List<BundleType>     list     = q.getResultList();
		Iterator<BundleType> iterator = list.iterator();
		while (iterator.hasNext())
		{
			BundleType obj = iterator.next();
			bundleTypeCache.put(obj.getId(), obj);
		}
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		User user = new User("Алексеев", "Александр", "Константинович",
							 "a.alekseev.2018@stud.nstu.ru", roleCache.get(11L));
		sessionFactory.getCurrentSession().persist(user);
		userCache.put(user.getId(), user);
		long me = user.getId();
		user = new User("Иванов", "Иван", "Иванович", "i.ivanov.2018@stud.nstu.ru",
						roleCache.get(11L));
		sessionFactory.getCurrentSession().persist(user);
		userCache.put(user.getId(), user);
		long ivan = user.getId();

		Group group = new Group("АВТ-815");
		group.addStudent(userCache.get(me));
		group.addStudent(userCache.get(ivan));
		sessionFactory.getCurrentSession().persist(group);
		long avt815 = group.getId();
		groupCache.put(avt815, group);
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		user = new User("Малявко", "Александр", "Антонович", "a.malyavko@corp.nstu.ru",
						roleCache.get(10L));
		sessionFactory.getCurrentSession().persist(user);
		userCache.put(user.getId(), user);
		long teacher = user.getId();
		userCache.put(teacher, user);
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		Course course = new Course("Параллельное Программирование");
		course.addGroup(groupCache.get(avt815));
		sessionFactory.getCurrentSession().persist(course);
		courseCache.put(course.getId(), course);
		long pp = course.getId();
		course.addAuthor(userCache.get(teacher), Author.AUTHOR);
		sessionFactory.getCurrentSession().merge(course);
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		course = courseCache.get(pp);
		course.addRequirement(new Requirement(4, bundleTypeCache.get(201L)));

		long lr1 = 0;
		//Добавил пустые бандлы предмета для всех студентов в группе
		for (int i = 1; i <= 4; i++)
		{
			Iterator<User> userIterator = groupCache.get(avt815).getStudents().iterator();
			while (userIterator.hasNext())
			{
				user = userIterator.next();

				Bundle bundle = new Bundle(i, course, bundleTypeCache.get(201L));
				sessionFactory.getCurrentSession().persist(bundle);
				bundleCache.put(bundle.getId(), bundle);
				if (i == 1)
				{
					lr1 = bundle.getId();
				}

				bundle.addAuthor(user, Author.AUTHOR);
				sessionFactory.getCurrentSession().merge(bundle);
			}
		}

		//course.addRequirement(new Requirement(1,bundleTypeCache.get(203L)));

		sessionFactory.getCurrentSession().merge(course);
		sessionFactory.getCurrentSession().getTransaction().commit();

		//работа с бандлами
		sessionFactory.getCurrentSession().beginTransaction();
		WordParser wordParser = new WordParser();
		String     text       = null;
		try
		{
			text = wordParser.parseDoc("resources/doc.docx");
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}

		bundleCache.get(lr1).getReport().setFileNameAndMeta("doc.docx", text);
		bundleCache.get(lr1).accept();
		bundleCache.get(lr1).addAuthor(userCache.get(me), Author.COAUTHOR);
		sessionFactory.getCurrentSession().merge(bundleCache.get(lr1));
		sessionFactory.getCurrentSession().getTransaction().commit();

		//-------------------------------------------------------------

		//удаление группы и студентов
		//удаление связки курса с удаляемой группой
		//удаление бандлов студентов, состоящих в группе
		sessionFactory.getCurrentSession().beginTransaction();
		HQL = "select c from Course as c inner join fetch c.groupes as g where g.id = :id";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id", avt815);
		List<Course>     groupList     = q.getResultList();
		Iterator<Course> groupIterator = groupList.iterator();
		while (groupIterator.hasNext())
		{
			course = groupIterator.next();
			courseCache.put(course.getId(), course);

			Set<Group>      gset      = course.getGroupes();
			Iterator<Group> iterator1 = gset.iterator();
			while (iterator1.hasNext())
			{
				group = iterator1.next();
				groupCache.put(group.getId(), group);
			}
			course.removeGroup(groupCache.get(avt815));
		}


		List<Long> ids = new LinkedList<>();
		ids.add(userCache.get(me).getId());
		ids.add(userCache.get(ivan).getId());

		//выборка всех бандлов, в которых авторами являются студенты из удаляемой группы
		HQL
				= "select b from Bundle as b inner join b.bundleACLSet as acl where acl.id.userID in :id and acl.rights = 'AUTHOR'";
		q       = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameterList("id", ids);
		List<Bundle> res = q.getResultList();

		Iterator<Bundle> bundleIterator = res.iterator();
		while (bundleIterator.hasNext())
		{
			Bundle bundle = bundleIterator.next();
			bundleCache.remove(bundle.getId());
			sessionFactory.getCurrentSession().delete(bundle);
		}

		sessionFactory.getCurrentSession().delete(groupCache.get(avt815));
		groupCache.remove(avt815);
		userCache.remove(me);
		userCache.remove(ivan);

		avt815 = 0L;
		me     = 0L;
		ivan   = 0L;
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().remove(courseCache.get(pp));
		Set<Requirement>      requirements = course.getRequirementSet();
		Iterator<Requirement> iterator1    = requirements.iterator();
		while (iterator1.hasNext())
		{
			sessionFactory.getCurrentSession().delete(iterator1.next());
		}
		courseCache.remove(pp);
		pp = 0L;
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().remove(userCache.get(teacher));
		userCache.remove(teacher);
		teacher = 0;
		sessionFactory.getCurrentSession().getTransaction().commit();
	}

	public IBundleService getiBundleService()
	{
		return iBundleService;
	}

	public IBundleTypeService getiBundleTypeService()
	{
		return iBundleTypeService;
	}

	public ICourseService getiCourseService()
	{
		return iCourseService;
	}

	public IGroupService getiGroupService()
	{
		return iGroupService;
	}

	public IRoleService getiRoleService()
	{
		return iRoleService;
	}

	public IUserService getiUserService()
	{
		return iUserService;
	}

	public UserValidationService getUserValidationService()
	{
		return userValidationService;
	}
}
