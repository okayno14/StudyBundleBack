package dataAccess.repository;

import dataAccess.entity.User;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.NotUniqueException;
import exception.DataAccess.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;
import java.util.List;

public class UserRepoHiber extends RepoHiberBase implements IUserRepo
{
	private IRoleRepo roleRepo;

	public UserRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
		roleRepo = new RoleRepoHiber(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	@Override
	public void save(User user)
	{
		Transaction t = getOrBegin();
		try
		{
			if (user.getId() != -1L)
			{
				sessionFactory.getCurrentSession().merge(user);
			}
			else
			{
				sessionFactory.getCurrentSession().save(user);
			}
			t.commit();
		}
		catch (PersistenceException ee)
		{
			t.rollback();
			if (ee.getCause() instanceof ConstraintViolationException)
			{
				throw new DataAccessException(
						new NotUniqueException("Почта " + user.getEmail() + " уже есть в базе",
											   ee.getCause()));
			}
		}
	}

	@Override
	public User get(long id)
	{
		Transaction t   = getOrBegin();
		User        res = sessionFactory.getCurrentSession().get(User.class, id);
		t.commit();
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public User get(String email)
	{
		Transaction t = getOrBegin();
		HQL = "from User as u where u.email=:email";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("email", email);
		User res = (User) q.uniqueResult();
		t.commit();
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public User get(String email, String pass)
	{
		Transaction t = getOrBegin();
		HQL = "from User as u where u.email=:email and u.pass=:pass";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("email", email);
		q.setParameter("pass", pass);
		User res = (User) q.uniqueResult();
		t.commit();
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public User get(User fio)
	{
		Transaction t = getOrBegin();
		HQL = "from User as u where " +
				"u.lastName=:lastName and u.firstName=:firstName and u.fatherName=:fatherName " +
				"and u.role.id=:roleID";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("lastName", fio.getLastName());
		q.setParameter("firstName", fio.getFirstName());
		q.setParameter("fatherName", fio.getFatherName());
		q.setParameter("roleID", fio.getRole().getId());
		User res = (User) q.uniqueResult();
		t.commit();
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public User getByGroup(User fio, String groupName)
	{
		Transaction t = getOrBegin();
		HQL = "from User as u where " +
				"u.lastName=:lastName and u.firstName=:firstName and u.fatherName=:fatherName " +
				"and u.group.name = :groupName";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("lastName", fio.getLastName());
		q.setParameter("firstName", fio.getFirstName());
		q.setParameter("fatherName", fio.getFatherName());
		q.setParameter("groupName", groupName);
		User res = (User) q.uniqueResult();
		t.commit();
		if (res != null)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<User> getByCourse(User fio, String courseName)
	{
		Transaction t = getOrBegin();
		HQL = "select u from User as u " + "inner join CourseACL as ca on ca.id.userID = u.id " +
				"inner join Course as c on c.id = ca.id.courseID " +
				"where u.lastName=:lastName and u.firstName=:firstName and u.fatherName=:fatherName and " +
				"c.name=:courseName";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("lastName", fio.getLastName());
		q.setParameter("firstName", fio.getFirstName());
		q.setParameter("fatherName", fio.getFatherName());
		q.setParameter("courseName", courseName);
		List<User> res = q.getResultList();
		t.commit();
		if (res.size() != 0)
		{
			return res;
		}

		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public void delete(User user)
	{
		Transaction t = getOrBegin();
		sessionFactory.getCurrentSession().delete(user);
		t.commit();
	}

	public IRoleRepo getRoleRepo()
	{
		return roleRepo;
	}
}
