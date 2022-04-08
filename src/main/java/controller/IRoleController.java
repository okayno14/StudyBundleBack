package controller;

import business.IRoleService;
import dataAccess.entity.Role;

import java.util.List;

public interface IRoleController extends IRoleService
{
	@Override
	List<Role> get();
}
