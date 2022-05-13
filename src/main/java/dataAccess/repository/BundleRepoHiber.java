package dataAccess.repository;

import dataAccess.entity.*;
import exception.Business.BusinessException;
import exception.Business.DeletingImportantData;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import java.util.*;

public class BundleRepoHiber extends RepoHiberBase implements IBundleRepo
{
	private final String FULL_GRAPH_BUNDLE = "inner join fetch \n" +
			"	b.bundleACLSet as bACL \n" + "inner join fetch \n" + "	b.course as c \n" +
			"inner join fetch \n" + "	c.courseACL_Set as cACL\n";

	public BundleRepoHiber(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
	protected Transaction getOrBegin()
	{
		return super.getOrBegin();
	}

	private void saveAction(Bundle b)
	{
		if (b.getId() == -1L)
		{
			sessionFactory.getCurrentSession().save(b);
			User author = b.getAuthor();
			b.getACL().clear();
			b.addACE(author,Author.AUTHOR);
			sessionFactory.getCurrentSession().save(b.getAuthorACE());
		}
		else
		{
			sessionFactory.getCurrentSession().merge(b);
		}
	}

	@Override
	public void save(Bundle b)
	{
		Transaction t = getOrBegin();
		saveAction(b);
		t.commit();
	}

	//Ожидается, что поступят только пустые бандлы
	@Override
	public void save(List<Bundle> bundles)
	{
		Transaction t = getOrBegin();
		for (Bundle b : bundles)
		{
			saveAction(b);
		}
		t.commit();
	}

	@Override
	public Bundle get(long id)
	{
		Transaction t = getOrBegin();
		try
		{
			HQL = "select \n" +
					"	b \n" +
					"from \n" +
					"	Bundle as b\n"+
					FULL_GRAPH_BUNDLE+
					" where\n" +
					"	b.id=:id";
			q   = sessionFactory.getCurrentSession().createQuery(HQL);
			q.setParameter("id", id);
			Bundle res = (Bundle) q.getSingleResult();
			t.commit();
			return res;
		}
		catch (NoResultException e)
		{
			t.commit();
			throw new DataAccessException(new ObjectNotFoundException());
		}
	}

	@Override
	public List<Bundle> get(String courseName, String groupName, User fio)
	{
		Transaction t = getOrBegin();
		HQL = "select \n" + "	b \n" + "from \n" + "	Bundle as b\n" + FULL_GRAPH_BUNDLE +
				"inner join \n" + "	bACL.user as u \n" + "inner join \n" +
				"	u.group as g \n" + "where \n" + "	c.name = :course and \n" +
				"	g.name = :group and \n" + "	u.lastName = :lastName and \n" +
				"	u.firstName = :firstName and\n" + "	u.fatherName = :fatherName";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course", courseName);
		q.setParameter("group", groupName);
		q.setParameter("lastName", fio.getLastName());
		q.setParameter("firstName", fio.getFirstName());
		q.setParameter("fatherName", fio.getFatherName());
		List<Bundle> res = q.getResultList();
		t.commit();
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
		HQL =
				"select \n" +
				"	b \n" +
				"from \n" +
				"	Bundle as b\n" +
				FULL_GRAPH_BUNDLE +
				"inner join \n" +
				"	bACL.user as u \n" +
				"where \n" +
				"	c.id=:course and \n" +
				"	u.id = :user";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course", course.getId());
		q.setParameter("user", user.getId());
		List<Bundle> res = q.getResultList();

		//Для загрузки ACL курса. Так как при eager будет генерироваться ошибка,
		//а в hql нельзя фетчить коллекции, связью с которыми не владеет вызываемый объект
//		Course c = res.get(0).getCourse();
//		c.getACL();
		t.commit();

//		for (Bundle b : res)
//		{
//			b.setCourse(c);
//		}
		if (res.size() != 0)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	private List<Bundle> getAllACTION(User user)
	{
		HQL =
				"select distinct \n" +
						"	c \n" +
						"from \n" +
						"	Bundle as b\n" +
						"inner join \n" +
						"	b.bundleACLSet as bACL\n" +
						"inner join \n" +
						"	b.course as c\n" + "inner join fetch \n" +
						"	c.courseACL_Set as cACL\n" +
						"where \n" +
						"	bACL.user.id = :user";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("user", user.getId());
		List<Course> courseList = q.getResultList();
		List<Bundle> res        = new LinkedList<>();
		for (Course c : courseList)
		{
			HQL =
					"select \n" +
							"	b \n" +
							"from \n" +
							"	Bundle as b \n" +
							"inner join fetch \n" +
							"	b.bundleACLSet as bACL \n" +
							"inner join \n" +
							"	b.course as c\n" +
							"inner join \n" +
							"	bACL.user as u \n" +
							"inner join \n" +
							"	u.group as g \n" +
							"where \n" +
							"	u.id = :user and \n" +
							"	c.id = :course";
			q = sessionFactory.getCurrentSession().createQuery(HQL);
			q.setParameter("user", user.getId());
			q.setParameter("course", c.getId());
			List<Bundle> bundleList = q.getResultList();
			for (Bundle b : bundleList)
			{
				b.setCourse(c);
				res.add(b);
			}
		}
		return res;
	}

	@Override
	public List<Bundle> getAll(User user)
	{
		//Данный запрос выгоднее по времени и данным выполнить в два этапа:
		//1) получить все курсы
		//2) для каждого из курсов извлечь бандлы
		Transaction t = getOrBegin();
		List<Bundle> res = getAllACTION(user);
		t.commit();
		if (res.size() != 0)
		{
			return res;
		}
		throw new DataAccessException(new ObjectNotFoundException());
	}

	@Override
	public List<Bundle> get(Course course, BundleType bt, int num)
	{
		Transaction t = getOrBegin();
		HQL =
				"        select \n" +
				"            b \n" +
				"        from \n" +
				"            Bundle as b \n" +
				"        inner join fetch \n" +
				"            b.bundleACLSet as bACL \n" +
				"        inner join fetch \n" +
				"            b.course as c \n" +
				"        inner join fetch \n" +
				"            c.courseACL_Set as cACL\n" +
				"        where\n" +
				"            c.id=:course and\n" +
				"            b.bundleType.id =:bt and\n" +
				"            b.num = :num and\n" +
				"            b.state = 'ACCEPTED'";
		q   = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course", course.getId());
		q.setParameter("bt", bt.getId());
		q.setParameter("num", num);
		List<Bundle> res = q.getResultList();
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

	@Override
	public List<Bundle> delete(Course course, List<Group> groupList)
	{
		Transaction t = getOrBegin();
		HQL=
		"        select \n" +
		"            b \n" +
		"        from \n" +
		"            Bundle as b \n" +
		"        inner join fetch \n" +
		"            b.bundleACLSet as bACL \n" +
		"        inner join \n" +
		"            bACL.user as u " +
		"        inner join \n" +
		"            b.course as c \n" +
		"        inner join \n" +
		"            c.courseACL_Set as cACL" +
		"        inner join \n" +
		"            u.group as g \n" +
		"        where \n" +
		"            g in (:group)  and \n" +
		"            c.id = :course";
		q = sessionFactory.getCurrentSession().createQuery(HQL);
		q.setParameter("course", course.getId());
		q.setParameter("group",groupList);
		List<Bundle> res = q.getResultList();

		for(Bundle b:res)
		{
			if(b.getState().equals(BundleState.ACCEPTED))
			{
				t.rollback();
				throw new BusinessException(
						new DeletingImportantData("Запрещено удалять принятые работы"));
			}
			sessionFactory.getCurrentSession().delete(b);
		}
		t.commit();
		return  res;
	}

	@Override
	public List<Bundle> delete(User user)
	{
		Transaction t = getOrBegin();
		List<Bundle> toDel = getAllACTION(user);

		for(Bundle b:toDel)
		{
			if(b.getState().equals(BundleState.ACCEPTED))
			{
				t.rollback();
				throw new BusinessException(
						new DeletingImportantData("Запрещено удалять принятые работы"));
			}
			sessionFactory.getCurrentSession().delete(b);
		}
		t.commit();
		return toDel;
	}
}
