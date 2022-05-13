package dataAccess.repository;

import dataAccess.entity.Author;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.NotUniqueException;
import exception.DataAccess.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.util.List;

public class CourseRepoHiber extends RepoHiberBase implements ICourseRepo
{
	private String fullGraph
			= "select c from Course as c inner join fetch c.courseACL_Set as cACL ";

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
	public void  save(Course course)
	{
		try
		{
			Transaction t       = getOrBegin();
			Session     session = sessionFactory.getCurrentSession();
			if (course.getId() == -1L)
			{
				session.save(course);
				User author = course.getAuthor();
				course.getACL().clear();
				course.addACE(author, Author.AUTHOR);
				session.save(course.getAuthorACE());
			}
			else
			{
				session.update(course);
			}
			t.commit();
		}
		catch (PersistenceException e)
		{
			if (e.getCause() instanceof ConstraintViolationException)
			{
				throw new DataAccessException(
						new NotUniqueException("Курс присутствует в системе", e.getCause()));
			}
		}
	}

	@Override
	public Course get(long id)
	{
		Transaction t = getOrBegin();
		try
		{
			Session session = sessionFactory.getCurrentSession();
			HQL = fullGraph + "where c.id=:id";
			q   = session.createQuery(HQL);
			q.setParameter("id", id);
			Course res = (Course) q.getSingleResult();
			t.commit();
			return res;
		}
		catch (NoResultException e)
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
	}

	@Override
	public List<Course> get(User owner, String name)
	{
		Transaction t       = getOrBegin();
		Session     session = sessionFactory.getCurrentSession();
		HQL =
				fullGraph +
				"where cACL.id.userID=:id and c.name=:name";
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
		HQL = fullGraph + "where cACL.id.userID=:id";
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
		HQL =
				fullGraph +
				"inner join c.groupes as g " +
				"inner join g.students as u " +
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
	public List<Course> getByGroup(Group g)
	{
		Transaction t = getOrBegin();
		HQL = fullGraph + "inner join c.groupes as g where g.id = :id";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id", g.getId());
		List<Course> res = q.getResultList();
		t.commit();
		if (res.size() == 0)
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
		return res;
	}

	@Override
	public void delete(List<Course> courseList)
	{
		Transaction t       = getOrBegin();
		HQL = "delete from Course as c where c in (:course)";
		q = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course", courseList);
		q.executeUpdate();

		//Session     session = sessionFactory.getCurrentSession();
		//session.delete(course);
		t.commit();
	}
}
