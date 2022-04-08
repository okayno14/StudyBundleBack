package dataAccess.repository;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class RepoHiberBase
{
	protected SessionFactory sessionFactory;
	protected String         HQL=null;
	protected Query          q=null;

	public RepoHiberBase(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	protected Transaction getOrBegin()
	{
		Transaction t = sessionFactory.getCurrentSession().getTransaction();
		if (!t.isActive())
		{
			t.begin();
		}
		return t;
	}
}
