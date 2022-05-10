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
		sessionFactory.getCurrentSession().saveOrUpdate(req);
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
		HQL=
				"        select\n" +
				"            count (c)\n" +
				"        from\n" +
				"            Course as c\n" +
				"        inner join\n" +
				"            c.requirementSet as req\n" +
				"        where req.id = :req";
		q=sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("req",req.getId());
		long res = (long) q.uniqueResult();
		t.commit();
		return res;
	}
}
