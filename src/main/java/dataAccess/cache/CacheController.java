package dataAccess.cache;

import dataAccess.entity.*;

import java.util.Iterator;
import java.util.Set;

public class CacheController
{
	private IBundleCache     bundleCache;
	private IBundleTypeCache bundleTypeCache;
	private ICourseCache     courseCache;
	private IGroupCache      groupCache;
	private IRoleCache       roleCache;
	private IUserCache       userCache;

	public CacheController()
	{
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
		if (!groupCache.contains(group.getId()))
		{
			groupCache.put(group);
		}
		else
		{
			user.setGroup(groupCache.get(group.getId()));
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

	//обновить кеш типов работ
	//обновить кеш пользователей (добавить владельцев)
	//обновить кеш курсов (добавить курс)
	void added(Bundle bundle)
	{

	}

	void added(Course course)
	{
		//обновить кеш типов работ
		Set<Requirement>      requirements = course.getRequirementSet();
		Iterator<Requirement> iterator     = requirements.iterator();
		BundleType            bt           = null;
		while (iterator.hasNext())
		{
			Requirement req =iterator.next();
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
		//Set<CourseACL> courseACLSet = course.getCourseACL_Set();

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

	public void setRoleCache(IRoleCache roleCache)
	{
		this.roleCache = roleCache;
	}

	public void setUserCache(IUserCache userCache)
	{
		this.userCache = userCache;
	}
}
