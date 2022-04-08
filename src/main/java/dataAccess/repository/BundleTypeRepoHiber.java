package dataAccess.repository;

import dataAccess.entity.BundleType;
import exception.DataAccess.NotUniqueException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import javax.persistence.PersistenceException;
import java.util.List;

public class BundleTypeRepoHiber implements IBundleTypeRepo
{
	private SessionFactory sessionFactory;
	private String         HQL;
	private Query          q;

	public BundleTypeRepoHiber(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	private Transaction getOrBegin()
	{
		Transaction t = sessionFactory.getCurrentSession().getTransaction();
		if(!t.isActive())
		{
			t.begin();
		}
		return t;
	}

	@Override
	public List<BundleType> get()
	{
		Transaction t = getOrBegin();
		HQL = "from BundleType";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		List<BundleType>     list     = q.getResultList();
		t.commit();
		return list;
	}

	@Override
	public void save(BundleType bundleType) throws NotUniqueException
	{
		Transaction t = getOrBegin();
		try
		{
			if(bundleType.getId() != -1)
			{
				sessionFactory.getCurrentSession().merge(bundleType);
			}
			sessionFactory.getCurrentSession().save(bundleType);
			t.commit();
		}
		catch (PersistenceException ee)
		{
			if(ee.getCause() instanceof ConstraintViolationException)
			{
				t.rollback();
				NotUniqueException toThrow = new NotUniqueException();
				toThrow.initCause(ee.getCause());
				throw toThrow;
			}
		}
	}

	private long countReferences(BundleType bundleType)
	{
		HQL="select count (b.bundleType) from Bundle as b where b.bundleType.id = :id";
		q = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id",bundleType.getId());
		long res = (long) q.uniqueResult();
		HQL="select count (r.bundleType) from Requirement as r where r.bundleType.id=:id";
		q = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id",bundleType.getId());
		res+=(long) q.uniqueResult();
		return res;
	}

	@Override
	public void delete(BundleType bundleType)
	{
		Transaction t = getOrBegin();
			countReferences(bundleType);
			sessionFactory.getCurrentSession().delete(bundleType);
		t.commit();
	}
}
