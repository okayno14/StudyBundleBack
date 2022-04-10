package business;

import dataAccess.cache.IRoleCache;
import dataAccess.cache.IUserCache;
import dataAccess.entity.Role;
import dataAccess.entity.User;
import dataAccess.repository.IRoleRepo;
import dataAccess.repository.IUserRepo;

import java.util.Iterator;
import java.util.List;

public class UserService implements IUserService
{
	private IUserRepo userRepo;
	private IRoleRepo roleRepo;

	private IUserCache userCache;
	private IRoleCache roleCache;

	public UserService(IUserRepo userRepo, IRoleRepo roleRepo, IUserCache userCache,
					   IRoleCache roleCache)
	{
		this.userRepo  = userRepo;
		this.roleRepo  = roleRepo;
		this.userCache = userCache;
		this.roleCache = roleCache;

		List<Role>     res      = roleRepo.get();
		Iterator<Role> iterator = res.iterator();
		while (iterator.hasNext())
		{
			roleCache.put(iterator.next());
		}
	}

	@Override
	public User get(long id)
	{
		User res = userRepo.get(id);
		userCache.put(res);
		return res;
	}
}
