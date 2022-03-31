import dataAccess.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class main
{
	public static void testEntity(SessionFactory sessionFactory)
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
			int id=user.getId();
			user=null;
		sessionFactory.getCurrentSession().getTransaction().commit();

		sessionFactory.getCurrentSession().beginTransaction();
			User user1=sessionFactory.getCurrentSession().get(User.class,id);
			Group group = new Group("АВТ-816");
			sessionFactory.getCurrentSession().save(group);
			group.addStudent(user1);
			int id2=group.getId();
		sessionFactory.getCurrentSession().getTransaction().commit();


	}

	public static void main(String arg[])
	{
		System.out.println(1 + 1);
		HTTP_Method http_method = HTTP_Method.POST;
		System.out.println(http_method);

		File          file          = new File("resources/Config/hibernate.cfg.xml");
		Configuration configuration = new Configuration().configure(file);
		configuration.addAnnotatedClass(Route.class);
		configuration.addAnnotatedClass(Role.class);
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(Group.class);
		SessionFactory sessionFactory = configuration.buildSessionFactory();


		sessionFactory.getCurrentSession().beginTransaction();
		User user1 = sessionFactory.getCurrentSession().get(User.class,120);
		sessionFactory.getCurrentSession().getTransaction().commit();

		testEntity(sessionFactory);
	}
}
