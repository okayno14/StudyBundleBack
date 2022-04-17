package dataAccess.cache;

import dataAccess.entity.*;

import java.util.Iterator;
import java.util.Set;

public class CacheController implements Runnable
{
	private IBundleCache      bundleCache;
	private IBundleTypeCache  bundleTypeCache;
	private ICourseCache      courseCache;
	private IGroupCache       groupCache;
	private IRoleCache        roleCache;
	private IUserCache        userCache;
	private IRequirementCache requirementCache;

	private Thread t;

	public CacheController()
	{
		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				//20 минут
				//20L * 60L * 1000L
				Thread.sleep(20L * 60L * 1000L);
				clean();
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	//Поведение всех методов одинаково:
	//если вложенной сущности нет в кешах, то добавить (обновить кеш)
	//иначе - вытащить из кеша сущность с этим ID и вложить в добавленную

	void added(User user)
	{
		//вытащить роль из кеша и присвоить новому объекту
		Role role = user.getRole();
		if (roleCache.contains(role.getId()))
		{
			role = roleCache.get(role.getId());
			user.setRole(role);
		}

		//обновить кеш групп
		Group group = user.getGroup();
		if (group != null)
		{
			if (!groupCache.contains(group.getId()))
			{
				groupCache.put(group);
			}
			else
			{
				user.setGroup(groupCache.get(group.getId()));
			}
		}
	}

	//обновить кеш пользователей,
	//но если была загружена группа со студентами
	//может быть lazyException
	public void fetched(Group group)
	{
		Set<User>      users    = group.getStudents();
		Iterator<User> iterator = users.iterator();
		while (iterator.hasNext())
		{
			User user = iterator.next();
			if (!userCache.contains(user.getId()))
			{
				userCache.put(user);
			}
			else
			{
				iterator.remove();
				users.add(userCache.get(user.getId()));
			}
		}
	}

	void added(Bundle bundle)
	{
		//обновить кеш типов работ
		BundleType bt = bundle.getBundleType();
		if (!bundleTypeCache.contains(bt.getId()))
		{
			bundleTypeCache.put(bt);
		}
		else
		{
			bundle.setBundleType(bundleTypeCache.get(bt.getId()));
		}
		//обновить кеш пользователей (добавить владельцев)
		Set<BundleACL>      bundleACLSet      = bundle.getBundleACLSet();
		Iterator<BundleACL> bundleACLIterator = bundleACLSet.iterator();
		BundleACL           bundleACL         = null;
		while (bundleACLIterator.hasNext())
		{
			bundleACL = bundleACLIterator.next();
			User user = bundleACL.getUser();
			if (!userCache.contains(user.getId()))
			{
				userCache.put(user);
			}
			else
			{
				bundleACL.setUser(userCache.get(user.getId()));
			}
		}
		//обновить кеш курсов (добавить курс)
		Course course = bundle.getCourse();
		if (!courseCache.contains(course.getId()))
		{
			courseCache.put(course);
		}
		else
		{
			bundle.setCourse(courseCache.get(course.getId()));
		}
	}

	void added(Course course)
	{
		Set<Requirement>      requirements = course.getRequirementSet();
		Iterator<Requirement> iterator     = requirements.iterator();
		BundleType            bt           = null;
		while (iterator.hasNext())
		{
			//Обновить кеш требований
			Requirement req = iterator.next();
			if (!requirementCache.contains(req.getId()))
			{
				requirementCache.put(req);
			}
			else
			{
				req = requirementCache.get(req.getId());
				course.removeRequirement(req);
				course.addRequirement(req);
			}

			//обновить кеш типов работ
			bt = req.getBundleType();
			if (!bundleTypeCache.contains(bt.getId()))
			{
				bundleTypeCache.put(bt);
			}
			else
			{
				req.setBundleType(bundleTypeCache.get(bt.getId()));
			}
		}
		//обновить кеш пользователей (добавить владельцев)
		Set<CourseACL>      courseACLSet      = course.getCourseACL_Set();
		Iterator<CourseACL> courseACLIterator = courseACLSet.iterator();
		CourseACL           courseACL         = null;
		while (courseACLIterator.hasNext())
		{
			courseACL = courseACLIterator.next();
			User user = courseACL.getUser();
			if (!userCache.contains(user.getId()))
			{
				userCache.put(user);
			}
			else
			{
				courseACL.setUser(userCache.get(user.getId()));
			}
		}
		//обновить кеш групп
		Set<Group>      groups    = course.getGroupes();
		Iterator<Group> iterator1 = groups.iterator();
		Group           group     = null;
		while (iterator1.hasNext())
		{
			group = iterator1.next();
			if (!groupCache.contains(group.getId()))
			{
				groupCache.put(group);
			}
			else
			{
				iterator1.remove();
				groups.add(groupCache.get(group.getId()));
			}
		}
	}

	//Эти методы вызываются, если объект удаляется из системы через сервис
	void deleted(Group group)
	{
		Set<User>      students = group.getStudents();
		Iterator<User> iterator = students.iterator();
		while (iterator.hasNext())
		{
			userCache.delete(iterator.next().getId());
		}
	}

	//Метод очищающий практически все кеши. Удаление происходит только из памяти приложения
	private void clean()
	{
		bundleCache.clean();
		courseCache.clean();
		groupCache.clean();
		userCache.cleanNonAuth();

		//этих не трогаем
		//		bundleTypeCache;
		//		roleCache;
		//		requirementCache;
	}

	public void setBundleCache(IBundleCache bundleCache)
	{
		this.bundleCache = bundleCache;
	}

	public void setBundleTypeCache(IBundleTypeCache bundleTypeCache)
	{
		this.bundleTypeCache = bundleTypeCache;
	}

	public void setCourseCache(ICourseCache courseCache)
	{
		this.courseCache = courseCache;
	}

	public void setGroupCache(IGroupCache groupCache)
	{
		this.groupCache = groupCache;
	}

	public void setRequirementCache(IRequirementCache requirementCache)
	{
		this.requirementCache = requirementCache;
	}

	public void setRoleCache(IRoleCache roleCache)
	{
		this.roleCache = roleCache;
	}

	public void setUserCache(IUserCache userCache)
	{
		this.userCache = userCache;
	}

	//---------------------------------------------------

	public IBundleCache getBundleCache()
	{
		return bundleCache;
	}

	public IBundleTypeCache getBundleTypeCache()
	{
		return bundleTypeCache;
	}

	public ICourseCache getCourseCache()
	{
		return courseCache;
	}

	public IGroupCache getGroupCache()
	{
		return groupCache;
	}

	public IRoleCache getRoleCache()
	{
		return roleCache;
	}

	public IUserCache getUserCache()
	{
		return userCache;
	}
}
