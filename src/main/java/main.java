import dataAccess.entity.HTTP_Method;
import dataAccess.entity.Role;
import dataAccess.entity.Route;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class main
{
	public static void testEntity(SessionFactory sessionFactory)
	{
		sessionFactory.getCurrentSession().beginTransaction();
			Role role1= sessionFactory.getCurrentSession().get(Role.class,9);
			Role role2= sessionFactory.getCurrentSession().get(Role.class,10);
			Role role3= sessionFactory.getCurrentSession().get(Role.class,11);
		sessionFactory.getCurrentSession().getTransaction().commit();
	}
	public static void main(String arg[])
	{
		System.out.println(1+1);
		HTTP_Method http_method = HTTP_Method.POST;
		System.out.println(http_method);

		File file = new File("resources/Config/hibernate.cfg.xml");
		Configuration configuration = new Configuration().configure(file);
		configuration.addAnnotatedClass(Route.class);
		configuration.addAnnotatedClass(Role.class);
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		testEntity(sessionFactory);
	}
}
