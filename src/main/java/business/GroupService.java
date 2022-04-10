package business;

import dataAccess.cache.IGroupCache;
import dataAccess.entity.Group;
import dataAccess.repository.IGroupRepo;

public class GroupService implements IGroupService
{
	private IGroupRepo repo;
	private IGroupCache cache;
	private Group client;

	public GroupService(IGroupRepo repo, IGroupCache cache)
	{
		this.repo  = repo;
		this.cache = cache;
	}
}
