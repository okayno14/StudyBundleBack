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
		if(roleCache.contains(role.getId()))
		{
			role=roleCache.get(role.getId());
			user.setRole(role);
		}

		//обновить кеш групп
		Group group = user.getGroup();
		if(!groupCache.contains(group.getId()))
		{
			groupCache.put(group);
		}
	}
	//обновить кеш пользователей,
	//но если была загружена группа со студентами
	//может быть lazyException
	public void added(Group group)
	{
		Set<User> users = group.getStudents();
		Iterator<User> iterator = users.iterator();
		while(iterator.hasNext())
		{
			User user=iterator.next();
			if(!userCache.contains(user.getId()))
			{
				userCache.put(user);
			}
		}
	}
	//обновить кеш пользователей (добавить владельцев)
	//обновить кеш курсов (добавить курс)
	//обновить кеш типов работ
	void added(Bundle bundle){}
	//обновить кеш типов работ
	//обновить кеш групп
	void added(Course course){}
	private void clean(){}

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
