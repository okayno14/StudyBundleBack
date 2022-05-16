package business;

import dataAccess.cache.IGroupCache;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;
import dataAccess.repository.IGroupRepo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GroupService implements IGroupService
{
	private IGroupRepo  repo;
	private IGroupCache cache;

	public GroupService(IGroupRepo repo, IGroupCache cache)
	{
		this.repo  = repo;
		this.cache = cache;
	}

	@Override
	public void add(Group client)
	{
		repo.save(client);
		cache.put(client);
	}

	@Override
	public Group get(long id)
	{
		Group res = cache.get(id);
		if (res != null)
		{
			return res;
		}
		res = repo.get(id);
		cache.put(res);
		return res;
	}

	@Override
	public List<Group> get(String groupName)
	{
		return null;
	}

	@Override
	public Set<Group> get(Course course)
	{
		return null;
	}

	private void setGroupToUsers(List<User> users, Group newGroup)
	{
		Iterator<User> userIterator = users.iterator();
		while (userIterator.hasNext())
		{
			User  user     = userIterator.next();
			Group curGroup = user.getGroup();
			if (curGroup != null && repo.isStudentsFetched(curGroup))
			{
				curGroup.removeStudent(user);
			}
			user.setGroup(newGroup);
			if (newGroup != null && repo.isStudentsFetched(newGroup))
			{
				newGroup.addStudent(user);
			}
		}
		repo.save(users);
	}

	@Override
	public void addUsers(Group client, List<User> users)
	{
		setGroupToUsers(users, client);
	}

	@Override
	public Set<User> getUsers(Group client)
	{
		if (!repo.isStudentsFetched(client))
		{
			repo.fetchStudents(client);
			cache.putWithUsers(client);
		}
		return client.getStudents();
	}

	@Override
	public void deleteUsers(List<User> users)
	{
		setGroupToUsers(users, null);
	}

	@Override
	public void delete(Group client)
	{
		//отвязать пользователей
		LinkedList<User> users = new LinkedList<>(getUsers(client));
		setGroupToUsers(users,null);
		//удалить группу
		long id = client.getId();
		if (id != -1L)
		{
			repo.delete(client);
			cache.delete(id);
		}
	}

	@Override
	public IGroupRepo getRepo()
	{
		return repo;
	}
}
