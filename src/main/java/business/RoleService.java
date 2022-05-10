package business;

import dataAccess.cache.IRoleCache;
import dataAccess.entity.Role;
import dataAccess.repository.IRoleRepo;

import java.util.Iterator;
import java.util.List;

public class RoleService implements IRoleService
{
	private IRoleRepo  repo;
	private IRoleCache cache;
	private long       reservedRoleId[];

	private final long ADMIN;
	private final long TEACHER;
	private final long STUDENT;
	private final long GUEST;

	public RoleService(IRoleRepo repo, IRoleCache cache, long reservedRoleId[])
	{
		this.repo           = repo;
		this.cache          = cache;
		this.reservedRoleId = reservedRoleId;

		int i = 0;
		ADMIN   = reservedRoleId[i++];
		TEACHER = reservedRoleId[i++];
		STUDENT = reservedRoleId[i++];
		GUEST   = reservedRoleId[i++];

		List<Role>     res      = repo.get();
		Iterator<Role> iterator = res.iterator();
		while (iterator.hasNext())
		{
			cache.put(iterator.next());
		}
	}

	@Override
	public List<Role> get()
	{
		return cache.get();
	}

	@Override
	public Role getGuest()
	{
		return cache.get(GUEST);
	}

	@Override
	public Role getAdmin()
	{
		return cache.get(ADMIN);
	}
}
