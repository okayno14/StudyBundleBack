package dataAccess.repository;

import dataAccess.entity.Course;
import dataAccess.entity.User;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class CourseRepoHiber extends RepoHiberBase implements ICourseRepo
{
	public CourseRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	@Override
	public void save(Course course)
	{
		Transaction t       = getOrBegin();
		Session     session = sessionFactory.getCurrentSession();
		if (course.getId() != -1)
		{
			session.merge(course);
		}
		else
		{
			session.save(course);
		}
		t.commit();
	}

	@Override
	public Course get(long id)
	{
		Transaction t       = getOrBegin();
		Session     session = sessionFactory.getCurrentSession();
		Course      res     = session.get(Course.class, id);
		t.commit();
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<Course> get(User owner, String name)
	{
		Transaction t       = getOrBegin();
		Session     session = sessionFactory.getCurrentSession();
		HQL = "select c from Course as c inner join c.courseACL_Set as o " +
				"where o.id.userID=:id and c.name=:name";
		q   = session.createQuery(HQL);
		q.setParameter("id", owner.getId());
		q.setParameter("name", name);
		List<Course> res = q.getResultList();
		t.commit();
		if (res.size() == 0)
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
		return res;
	}

	@Override
	public List<Course> getByOwner(User owner)
	{
		Transaction t       = getOrBegin();
		Session     session = sessionFactory.getCurrentSession();
		HQL = "select c from Course as c inner join c.courseACL_Set as o " +
				"where o.id.userID=:id";
		q   = session.createQuery(HQL);
		q.setParameter("id", owner.getId());
		List<Course> res = q.getResultList();
		t.commit();
		if (res.size() == 0)
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
		return res;
	}

	@Override
	public List<Course> getByStudent(User student)
	{
		Transaction t       = getOrBegin();
		Session     session = sessionFactory.getCurrentSession();
		HQL = "select c from Course as c inner join c.groupes as g inner join g.students as u " +
				"where u.id=:id";
		q   = session.createQuery(HQL);
		q.setParameter("id", student.getId());
		List<Course> res = q.getResultList();
		t.commit();
		if (res.size() == 0)
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
		return res;
	}

	@Override
	public void delete(Course course)
	{
		Transaction t       = getOrBegin();
		Session     session = sessionFactory.getCurrentSession();
		session.delete(course);
		t.commit();
	}
}