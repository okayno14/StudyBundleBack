package business;

import dataAccess.cache.IRoleCache;
import dataAccess.entity.Role;
import dataAccess.repository.IRoleRepo;

import java.util.List;

public class RoleService implements IRoleService
{
	private IRoleRepo repo;
	private IRoleCache cache;

	public RoleService(IRoleRepo repo, IRoleCache cache)
	{
		this.repo  = repo;
		this.cache = cache;
	}

	@Override
	public List<Role> get()
	{
		return cache.get();
	}
}
