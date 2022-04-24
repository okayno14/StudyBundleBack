package business;

import dataAccess.cache.IGroupCache;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;
import dataAccess.repository.IGroupRepo;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GroupService implements IGroupService
{
	private IGroupRepo repo;
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
		Group res= cache.get(id);
		if(res!=null)
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

	@Override
	public void addUsers(Group client, List<User> users)
	{
		Iterator<User> userIterator = users.iterator();
		while (userIterator.hasNext())
		{
			User user = userIterator.next();
			user.setGroup(client);
			if(repo.isStudentsFetched(client))
			{
				client.addStudent(user);
			}
		}
		repo.save(users);
	}

	@Override
	public Set<User> getUsers(Group client)
	{
		return null;
	}

	@Override
	public void deleteUsers(List<User> users)
	{

	}

	@Override
	public void delete(Group client)
	{
		long id = client.getId();
		if(id!=-1L)
		{
			if(cache.contains(id))
			{
				cache.delete(id);
			}
			repo.delete(client);
		}
	}

	@Override
	public IGroupRepo getRepo()
	{
		return repo;
	}
}
