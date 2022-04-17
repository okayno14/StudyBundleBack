package dataAccess.repository;

import dataAccess.entity.Requirement;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class RequirementRepoHiber extends RepoHiberBase implements IRequirementRepo
{
	public RequirementRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	@Override
	public void save(Requirement req)
	{
		Transaction t = getOrBegin();
		if (req.getId() != -1L)
		{
			sessionFactory.getCurrentSession().merge(req);
		}
		else
		{
			sessionFactory.getCurrentSession().save(req);
		}
		t.commit();
	}

	@Override
	public List<Requirement> get()
	{
		Transaction t = getOrBegin();
		HQL="from Requirement";
		q = sessionFactory.getCurrentSession().createQuery(HQL);
		List<Requirement> res = q.getResultList();
		t.commit();
		return res;
	}

	@Override
	public void delete(Requirement req)
	{
		Transaction t = getOrBegin();
		sessionFactory.getCurrentSession().delete(req);
		t.commit();
	}

	@Override
	public long countReferences(Requirement req)
	{
		Transaction t = getOrBegin();
		HQL="select count (c) from Course as c where c.requirement.id = :id";
		q=sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id",req.getId());
		return (long) q.uniqueResult();
	}
}
