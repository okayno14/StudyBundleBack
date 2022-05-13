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
	public List<Requirement> deleteNotLinked(List<Requirement> reqList)
	{
		Transaction t = getOrBegin();
		HQL=
			"        select\n" +
			"            req"+
			"        from \n" +
			"            Requirement as req\n" +
			"        where\n" +
			"            size(req.courseSet)=1 and\n" +
			"            req in (:requirement)";
		q=sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("requirement",reqList);
		List<Requirement>  res = q.getResultList();
		if(res.size()!=0)
		{
			HQL="delete from Requirement as req where req in (:requirement)";
			q=sessionFactory.getCurrentSession().createQuery(HQL);
			q.setParameter("requirement",res);
			q.executeUpdate();
		}
		t.commit();
		return res;
	}
}
