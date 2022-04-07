package model;

import business.*;
import business.bundle.IBundleService;
import business.bundle.WordParser;
import configuration.DateAccessConf;
import dataAccess.entity.*;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.*;

public class Core
{
	private SessionFactory sessionFactory;
	private DateAccessConf dateAccessConf;

	private IBundleService iBundleService;
	private IBundleTypeService iBundleTypeService;
	private ICourseService iCourseService;
	private IGroupService  iGroupService;
	private IRoleService   iRoleService;
	private IUserService   iUserService;

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

	public Core(DateAccessConf dateAccessConf)
	{
		this.dateAccessConf = dateAccessConf;
		initHiber(dateAccessConf.getHibernateConf());

		//сборка сервисов

		//testCourse(sessionFactory);
	}

	private void testCourse(SessionFactory sessionFactory)
	{
		Map<Long, BundleType> bundleTypeCache = new HashMap<Long, BundleType>();
		Map<Long, Role>       roleCache       = new HashMap<Long, Role>();
		Map<Long, User>       userCache       = new HashMap<Long, User>();
		Map<Long,Group>       groupCache      = new HashMap<Long,Group>();
		Map<Long,Course>      courseCache     = new HashMap<Long,Course>();
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
		User user = new User("Алексеев","Александр","Константинович",
							 "a.alekseev.2018@stud.nstu.ru", roleCache.get(11L));
		sessionFactory.getCurrentSession().persist(user);
		userCache.put(user.getId(),user);
		long me = user.getId();
		user = new User("Иванов","Иван","Иванович",
						"i.ivanov.2018@stud.nstu.ru", roleCache.get(11L));
		sessionFactory.getCurrentSession().persist(user);
		userCache.put(user.getId(),user);
		long ivan = user.getId();

		Group group = new Group("АВТ-815");
		group.addStudent(userCache.get(me));
		group.addStudent(userCache.get(ivan));
		sessionFactory.getCurrentSession().persist(group);
		long avt815=group.getId();
		groupCache.put(avt815,group);
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		user = new User("Малявко", "Александр", "Антонович", "a.malyavko@corp.nstu.ru",
						roleCache.get(10L));
		sessionFactory.getCurrentSession().persist(user);
		userCache.put(user.getId(),user);
		long teacher=user.getId();
		userCache.put(teacher,user);
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		Course course = new Course("Параллельное Программирование");
		course.addGroup(groupCache.get(avt815));
		sessionFactory.getCurrentSession().persist(course);
		courseCache.put(course.getId(),course);
		long pp=course.getId();
		course.addAuthor(userCache.get(teacher),Author.AUTHOR);
		sessionFactory.getCurrentSession().merge(course);
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		course = courseCache.get(pp);
		course.addRequirement(new Requirement(4,bundleTypeCache.get(201L)));

		long lr1=0;
		//Добавил пустые бандлы предмета для всех студентов в группе
		for(int i=1;i<=4;i++)
		{
			Iterator<User> userIterator = groupCache.get(avt815).getStudents().iterator();
			while(userIterator.hasNext())
			{
				user = userIterator.next();

				Bundle bundle = new Bundle(i,course,bundleTypeCache.get(201L));
				sessionFactory.getCurrentSession().persist(bundle);
				bundleCache.put(bundle.getId(),bundle);
				if(i==1)
				{
					lr1=bundle.getId();
				}

				bundle.addAuthor(user,Author.AUTHOR);
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

		bundleCache.get(lr1).getReport().setFileName("doc.docx",text);
		bundleCache.get(lr1).accept();
		bundleCache.get(lr1).addAuthor(userCache.get(me),Author.COAUTHOR);
		sessionFactory.getCurrentSession().merge(bundleCache.get(lr1));
		sessionFactory.getCurrentSession().getTransaction().commit();

		//-------------------------------------------------------------

		//удаление группы и студентов
		//удаление связки курса с удаляемой группой
		//удаление бандлов студентов, состоящих в группе
		sessionFactory.getCurrentSession().beginTransaction();
		HQL="select c from Course as c inner join fetch c.groupes as g where g.id = :id";
		q=sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id", avt815);
		List<Course> groupList= q.getResultList();
		Iterator<Course> groupIterator = groupList.iterator();
		while (groupIterator.hasNext())
		{
			course = groupIterator.next();
			courseCache.put(course.getId(),course);

			Set<Group>      gset      =course.getGroupes();
			Iterator<Group> iterator1 = gset.iterator();
			while(iterator1.hasNext())
			{
				group=iterator1.next();
				groupCache.put(group.getId(),group);
			}
			course.removeGroup(groupCache.get(avt815));
		}


		List<Long> ids = new LinkedList<>();
		ids.add(userCache.get(me).getId());
		ids.add(userCache.get(ivan).getId());

		//выборка всех бандлов, в которых авторами являются студенты из удаляемой группы
		HQL="select b from Bundle as b inner join b.bundleACLSet as acl where acl.id.userID in :id and acl.rights = 'AUTHOR'";
		q=sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameterList("id",ids);
		List<Bundle> res = q.getResultList();

		Iterator<Bundle> bundleIterator=res.iterator();
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

		avt815=0L;
		me=0L;
		ivan=0L;
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().remove(courseCache.get(pp));
		Set<Requirement> requirements = course.getRequirementSet();
		Iterator<Requirement> iterator1 = requirements.iterator();
		while (iterator1.hasNext())
		{
			sessionFactory.getCurrentSession().delete(iterator1.next());
		}
		courseCache.remove(pp);
		pp=0L;
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().remove(userCache.get(teacher));
		userCache.remove(teacher);
		teacher=0;
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
}
