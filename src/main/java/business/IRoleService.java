package business;

import dataAccess.entity.Role;

import java.util.List;

public interface IRoleService
{
	List<Role> get();
	Role getGuest();
}
