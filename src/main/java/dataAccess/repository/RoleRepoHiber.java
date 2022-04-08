package dataAccess.repository;

import dataAccess.entity.Role;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class RoleRepoHiber extends RepoHiberBase implements IRoleRepo
{

	public RoleRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	@Override
	public List<Role> get()
	{
		Transaction t = getOrBegin();
		HQL = "from Role";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		List<Role> res = q.getResultList();
		t.commit();
		return res;
	}
}
