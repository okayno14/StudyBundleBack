package dataAccess.cache;

import dataAccess.entity.Role;

import java.util.List;

public interface IRoleCache
{
	boolean contains(long id);
	boolean isEmpty();
	Role get(long id);
	List<Role> get();
	void put(Role role);
	void delete(long id);
}
