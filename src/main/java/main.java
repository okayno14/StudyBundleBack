import dataAccess.entity.*;
import exception.NoRightException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.Query;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class main
{
	public static void testUserGroup(SessionFactory sessionFactory)
	{
		sessionFactory.getCurrentSession().beginTransaction();
		Role admin   = sessionFactory.getCurrentSession().get(Role.class, 9);
		Role teacher = sessionFactory.getCurrentSession().get(Role.class, 10);
		Role student = sessionFactory.getCurrentSession().get(Role.class, 11);
		sessionFactory.getCurrentSession().getTransaction().commit();


		sessionFactory.getCurrentSession().beginTransaction();
		User user = new User("Алексеев", "Александр", "Константинович",
							 "a.alekseev.2018@stud.nstu.ru", student);
		sessionFactory.getCurrentSession().save(user);
		long id = user.getId();
		user = null;
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
		User  user1 = sessionFactory.getCurrentSession().get(User.class, id);
		Group group = new Group("АВТ-816");
		sessionFactory.getCurrentSession().save(group);
		group.addStudent(user1);
		long id2 = group.getId();
		sessionFactory.getCurrentSession().getTransaction().commit();
	}

	public static void testCourse(SessionFactory sessionFactory)
	{
		Map<Long, BundleType> bundleTypeCache = new HashMap<Long, BundleType>();
		Map<Long, Role> roleCache = new HashMap<Long, Role>();
		Map<Long, User> userCache = new HashMap<Long, User>();
		Map<Long,Group> groupCache = new HashMap<Long,Group>();
		Map<Long,Course> courseCache = new HashMap<Long,Course>();
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
			course.addRequirement(new Requirement(1,bundleTypeCache.get(203L)));
			sessionFactory.getCurrentSession().merge(course);
		sessionFactory.getCurrentSession().getTransaction().commit();

		//-------------------------------------------------------------

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

				Set<Group> gset=course.getGroupes();
				Iterator<Group> iterator1 = gset.iterator();
				while(iterator1.hasNext())
				{
					group=iterator1.next();
					groupCache.put(group.getId(),group);
				}
				course.removeGroup(groupCache.get(avt815));
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

	public static void main(String arg[])
	{
		File          file          = new File("resources/Config/hibernate.cfg.xml");
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
		SessionFactory sessionFactory = configuration.buildSessionFactory();

		testCourse(sessionFactory);
	}
}
