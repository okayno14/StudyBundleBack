package controller;

import business.IUserService;
import dataAccess.entity.Role;
import dataAccess.entity.User;
import exception.Business.AuthenticationException;
import exception.Business.BusinessException;
import exception.Controller.ControllerException;
import exception.Controller.TokenNotFound;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.NotUniqueException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserController implements IUserController
{
	private Controller   controller;
	private IUserService service;
	private Authoriser   authoriser = new Authoriser();

	private Role              guest    = null;
	private Map<String, User> guestMap = new HashMap<>();


	public UserController(Controller controller, IUserService userService)
	{
		this.controller = controller;
		this.service    = userService;
	}

	//пустышка
	@Override
	public User get(long id)
	{
		return null;
	}

	@Override
	public void add()
	{

	}

	@Override
	public void add(List<User> userList)
	{
		StringBuffer   log = new StringBuffer();
		Iterator<User> i   = userList.iterator();
		while (i.hasNext())
		{
			try
			{
				setClient(i.next());
				service.add();
			}
			catch (BusinessException e)
			{
				log.append(e.getCause().getMessage());
				log.append("\n");
			}
			if (log.length() != 0)
			{
				throw new DataAccessException(new NotUniqueException(log.toString()));
			}
		}
	}

	@Override
	public User getGuestUser()
	{
		if(guest==null)
		{
			guest=controller.roleController.getGuest();
		}
		User user = new User();
		user.setRole(guest);
		user.setToken(authoriser.genToken());
		guestMap.put(user.getToken(),user);
		return user;
	}

	@Override
	public User get(String email)
	{
		return null;
	}

	@Override
	public User get(User fio, Role role)
	{
		return null;
	}

	@Override
	public User getByGroup(User fio, String groupName)
	{
		return null;
	}

	@Override
	public List<User> getByCourse(User fio, String courseName)
	{
		return null;
	}

	@Override
	public void updateFIO(User fio)
	{

	}

	@Override
	public void updatePass(String pass)
	{

	}

	@Override
	public void updateMail(String email)
	{

	}

	@Override
	public void forgotPass(String email)
	{

	}

	@Override
	public void resetPass(String passHash, long id)
	{

	}

	@Override
	public void confirm(String email)
	{

	}

	@Override
	public void activate(long id)
	{

	}

	@Override
	public void delete()
	{

	}

	@Override
	public void setClient(User user)
	{
		service.setClient(user);
	}

	@Override
	public User getByToken(String token)
	{
		if(!authoriser.existsToken(token))
		{
			throw new ControllerException(new TokenNotFound());
		}
		User res;
		if((res=guestMap.get(token))==null)
		{
			res=service.getByToken(token);
		}
		return res;
	}

	@Override
	public boolean login(String token, String email, String pass)
	{

		if(service.login(token,email,pass))
		{
			guestMap.remove(token);
			return true;
		}
		return false;
	}

	@Override
	public void logout(String token)
	{
		authoriser.removeToken(token);
		if(guestMap.containsKey(token))
		{
			guestMap.remove(token);
			return;
		}
		service.logout(token);
	}
}
