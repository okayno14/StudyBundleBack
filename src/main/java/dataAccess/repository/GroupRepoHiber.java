package dataAccess.repository;

import dataAccess.entity.Group;
import dataAccess.entity.User;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.NotUniqueException;
import exception.DataAccess.ObjectNotFoundException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class GroupRepoHiber extends RepoHiberBase implements IGroupRepo
{
	public GroupRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	@Override
	public void save(Group group)
	{
		Transaction t = getOrBegin();
		try
		{
			if (group.getId() != -1L)
			{
				sessionFactory.getCurrentSession().merge(group);
			}
			else
			{
				sessionFactory.getCurrentSession().save(group);
			}
			t.commit();
		}
		catch (PersistenceException e)
		{
			t.rollback();
			if (e.getCause() instanceof ConstraintViolationException)
			{
				throw new DataAccessException(
						new NotUniqueException("Группа с таким именем присутствует в системе",
											   e.getCause()));
			}
		}

	}

	@Override
	public Group get(long id)
	{
		Transaction t = getOrBegin();
		Group res = sessionFactory.getCurrentSession().get(Group.class, id);
		t.commit();
		if(res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<Group> get(String groupName)
	{
		Transaction t = getOrBegin();
		HQL = "from Group as g where g.name like :template";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("template", groupName);
		List<Group> res = q.getResultList();
		t.commit();
		if (res.size() != 0)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}



	@Override
	public void save(List<User> users)
	{
		Transaction t = getOrBegin();
		Session session = sessionFactory.getCurrentSession();
		for(User u:users)
		{
			session.update(u);
		}
		t.commit();
	}


	@Override
	public boolean isStudentsFetched(Group group)
	{
		return Hibernate.isInitialized(group.getStudents());
	}

	@Override
	public void fetchStudents(Group group)
	{
		Transaction t = getOrBegin();
		HQL = "from User as u where u.group.id = :id";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("id", group.getId());
		List<User> res = (List<User>) q.getResultList();
		t.commit();
		group.setStudents(new HashSet<>(res));
	}

	@Override
	public void delete(Group group)
	{
		try
		{
			Transaction t = getOrBegin();
			sessionFactory.getCurrentSession().delete(group);
			t.commit();
		}
		catch (OptimisticLockException e)
		{
			throw new DataAccessException(new ObjectNotFoundException());
		}
	}
}
