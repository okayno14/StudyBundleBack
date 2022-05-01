package dataAccess.repository;

import dataAccess.entity.Bundle;
import dataAccess.entity.Course;
import dataAccess.entity.User;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class BundleRepoHiber extends RepoHiberBase implements IBundleRepo
{
	public BundleRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	@Override
	public void save(Bundle bundle)
	{
		Transaction t = getOrBegin();
			if(bundle.getId()==-1L)
			{
				sessionFactory.getCurrentSession().save(bundle);
				sessionFactory.getCurrentSession().save(bundle.getBundleACLSet().iterator().next());
			}
			else
			{
				sessionFactory.getCurrentSession().merge(bundle);
			}
		t.commit();
	}

	//Ожидается, что поступят только пустые бандлы
	@Override
	public void save(List<Bundle> bundles)
	{
		Transaction t = getOrBegin();
		for(Bundle b:bundles)
		{
			sessionFactory.getCurrentSession().save(b);
		}
		t.commit();
	}

	@Override
	public Bundle get(long id)
	{
		Transaction t =getOrBegin();
		HQL="from Bundle as b from inner join fetch b.bundleACLSet where b.id=:id";
		q=sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id",id);
		Bundle res = (Bundle) q.getSingleResult();
		t.commit();
		if(res!=null)
		{
			return  res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<Bundle> get(String courseName, String groupName, User fio)
	{
		Transaction t = getOrBegin();
		HQL="select b from Bundle as b inner join b.course as c " +
				"inner join fetch b.bundleACLSet as own " +
				"inner join own.user as u " +
				"inner join u.group as g " +
				"where c.name = :course and " +
				"g.name = :group and " +
				"u.lastName = :lastName and " +
				"u.firstName = :firstName and " +
				"u.fatherName = :fatherName";
		q = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course",courseName);
		q.setParameter("group", groupName);
		q.setParameter("lastName", fio.getLastName());
		q.setParameter("firstName", fio.getFirstName());
		q.setParameter("fatherName", fio.getFatherName());
		List<Bundle> res = q.getResultList();
		t.commit();
		if(res.size() != 0)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());

	}

	@Override
	public List<Bundle> get(Course course, User user)
	{
		Transaction t = getOrBegin();
		HQL="select b from Bundle as b inner join b.course as c " +
				"inner join fetch b.bundleACLSet as own " +
				"inner join own.user as u " +
				"where c.id=:course and u.id = :user";
		q=sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course",course.getId());
		q.setParameter("user",user.getId());
		List<Bundle> res = q.getResultList();
		t.commit();
		if(res.size()!=0)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<Bundle> getAll(User user)
	{
		Transaction t = getOrBegin();
		HQL="select b from Bundle as b inner join b.course as c " +
				"inner join fetch b.bundleACLSet as own " +
				"inner join own.user as u " +
				"where u.id = :user";
		q=sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("user",user.getId());
		List<Bundle> res = q.getResultList();
		t.commit();
		if(res.size()!=0)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public void delete(Bundle bundle)
	{
		Transaction t = getOrBegin();
		sessionFactory.getCurrentSession().delete(bundle);
		t.commit();
	}
}
