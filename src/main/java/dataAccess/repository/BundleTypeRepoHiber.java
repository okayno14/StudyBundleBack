package dataAccess.repository;

import dataAccess.entity.BundleType;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.NotUniqueException;
import exception.DataAccess.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;
import java.util.List;

public class BundleTypeRepoHiber extends RepoHiberBase implements IBundleTypeRepo
{
	public BundleTypeRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	@Override
	public List<BundleType> get()
	{
		Transaction t = getOrBegin();
		HQL = "from BundleType";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		List<BundleType> list = q.getResultList();
		t.commit();
		return list;
	}

	@Override
	public BundleType get(long id)
	{
		Transaction t   = getOrBegin();
		BundleType  res = sessionFactory.getCurrentSession().get(BundleType.class, id);
		t.commit();
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}


	@Override
	public void save(BundleType bundleType) throws NotUniqueException
	{
		Transaction t = getOrBegin();
		try
		{
			if (bundleType.getId() != -1L)
			{
				sessionFactory.getCurrentSession().merge(bundleType);
			}
			else
			{
				sessionFactory.getCurrentSession().save(bundleType);
			}
			t.commit();
		}
		catch (PersistenceException ee)
		{
			t.rollback();
			if (ee.getCause() instanceof ConstraintViolationException)
			{
				throw new DataAccessException(
						new NotUniqueException("Данный тип работы присутствует в базе",
											   ee.getCause()));
			}
		}
	}

	private long countReferences(BundleType bundleType)
	{
		HQL = "select count (b.bundleType) from Bundle as b where b.bundleType.id = :id";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id", bundleType.getId());
		long res = (long) q.uniqueResult();
		HQL = "select count (r.bundleType) from Requirement as r where r.bundleType.id=:id";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id", bundleType.getId());
		res += (long) q.uniqueResult();
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
