import dataAccess.entity.*;
import exception.NoRightException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
		Map<Long, Role> roleMap = new HashMap<Long, Role>();
		sessionFactory.getCurrentSession().beginTransaction();
			for (long i = 9; i <= 11; i++)
			{
				Role buf = sessionFactory.getCurrentSession().get(Role.class, i);
				roleMap.put(buf.getId(), buf);
			}
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
			Course course = new Course("Теория Формальных Языков и Компиляторов (ТФЯиК)");
			User user = new User("Малявко", "Александр", "Антонович", "a.malyavko@corp.nstu.ru",
								roleMap.get(10L));
			sessionFactory.getCurrentSession().save(user);
			course.addAuthor(user, Author.AUTHOR);
			sessionFactory.getCurrentSession().save(course);
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
			course.removeAuthor(user);
			//sessionFactory.getCurrentSession().delete(course);
			//sessionFactory.getCurrentSession().delete(user);
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
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		testCourse(sessionFactory);
	}
}
