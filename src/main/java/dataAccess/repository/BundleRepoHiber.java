package dataAccess.repository;

import dataAccess.entity.Bundle;
import dataAccess.entity.Course;
import dataAccess.entity.User;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class BundleRepoHiber extends RepoHiberBase implements IBundleRepo
{
	private String fullGraph = "select b from Bundle as b " +
			"inner join fetch b.bundleACLSet as bACL " + "inner join fetch b.course as c " +
			"left join c.courseACL_Set as cACL ";

	public BundleRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	private void saveNewBundle(Bundle b)
	{
		sessionFactory.getCurrentSession().save(b);
		sessionFactory.getCurrentSession().save(b.getAuthorACE());
	}

	@Override
	public void save(Bundle b)
	{
		Transaction t = getOrBegin();
		if (b.getId() == -1L)
		{
			saveNewBundle(b);
		}
		else
		{
			sessionFactory.getCurrentSession().merge(b);
		}
		t.commit();
	}

	//Ожидается, что поступят только пустые бандлы
	@Override
	public void save(List<Bundle> bundles)
	{
		Transaction t = getOrBegin();
		for (Bundle b : bundles)
		{
			saveNewBundle(b);
		}
		t.commit();
	}

	@Override
	public Bundle get(long id)
	{
		Transaction t = getOrBegin();
		HQL = fullGraph + "where b.id=:id";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id", id);
		Bundle res = (Bundle) q.getSingleResult();
		//Для загрузки ACL курса. Так как при eager будет генерироваться ошибка,
		//а в hql нельзя фетчить коллекции, связью с которыми не владеет вызываемый объект
		res.getCourse().getCourseACL_Set().size();
		t.commit();
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<Bundle> get(String courseName, String groupName, User fio)
	{
		Transaction t = getOrBegin();
		HQL = fullGraph +
				"inner join bACL.user as u " +
				"inner join u.group as g " +
				"where c.name = :course and " +
				"g.name = :group and " +
				"u.lastName = :lastName and " + "u.firstName = :firstName and " +
				"u.fatherName = :fatherName";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course", courseName);
		q.setParameter("group", groupName);
		q.setParameter("lastName", fio.getLastName());
		q.setParameter("firstName", fio.getFirstName());
		q.setParameter("fatherName", fio.getFatherName());
		List<Bundle> res = q.getResultList();

		//Для загрузки ACL курса. Так как при eager будет генерироваться ошибка,
		//а в hql нельзя фетчить коллекции, связью с которыми не владеет вызываемый объект
		Course c = res.get(0).getCourse();
		c.getCourseACL_Set();
		t.commit();

		for(Bundle b:res)
		{
			b.setCourse(c);
		}

		if (res.size() != 0)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<Bundle> get(Course course, User user)
	{
		Transaction t = getOrBegin();
		HQL = fullGraph + "inner join bACL.user as u " + "where c.id=:course and u.id = :user";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course", course.getId());
		q.setParameter("user", user.getId());
		List<Bundle> res = q.getResultList();

		//Для загрузки ACL курса. Так как при eager будет генерироваться ошибка,
		//а в hql нельзя фетчить коллекции, связью с которыми не владеет вызываемый объект
		Course c = res.get(0).getCourse();
		c.getCourseACL_Set();
		t.commit();

		for(Bundle b:res)
		{
			b.setCourse(c);
		}
		if (res.size() != 0)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<Bundle> getAll(User user)
	{
		Transaction t = getOrBegin();
		HQL = "select distinct c from Bundle as b " +
				"inner join b.course as c " +
				"inner join fetch c.courseACL_Set as cACL " +
				"inner join b.bundleACLSet as bACL " +
				"where bACL.user.id = :user";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("user", user.getId());
		List<Course> courseList = q.getResultList();
		List<Bundle> res        = new LinkedList<>();
		for (Course c : courseList)
		{
			HQL = "select b from Bundle as b " +
					"inner join fetch b.bundleACLSet as bACL " +
					"inner join b.course as c " +
					"where bACL.user.id = :user and c.id= :course";
			q   = sessionFactory.getCurrentSession().createQuery(HQL);
			q.setParameter("user", user.getId());
			q.setParameter("course", c.getId());
			List<Bundle> bundleList = q.getResultList();
			for (Bundle b : bundleList)
			{
				b.setCourse(c);
				res.add(b);
			}
		}
		t.commit();
		if (res.size() != 0)
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
