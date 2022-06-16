package controller;

import business.IUserService;
import business.UserValidationService;
import configuration.BusinessConfiguration;
import dataAccess.entity.Course;
import dataAccess.entity.Role;
import dataAccess.entity.User;
import exception.Business.BusinessException;
import exception.Controller.ControllerException;
import exception.Controller.TokenNotFound;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;

import java.util.*;

public class UserController implements IUserController
{
	private Controller            controller;
	private IUserService          service;
	private UserValidationService userValidationService;
	private Authoriser            authoriser;

	private Role GUEST = null;
	private Role ADMIN = null;

	private Map<String, User> guestMap = new HashMap<>();
	private Map<String, User> authenticatedMap = new HashMap<>();

	public UserController(Controller controller, IUserService userService,
						  UserValidationService userValidationService,
						  BusinessConfiguration businessConf)
	{
		this.controller            = controller;
		this.service               = userService;
		this.userValidationService = userValidationService;

		GUEST = controller.roleController.getGuest();
		ADMIN = controller.roleController.getAdmin();

		authoriser = new Authoriser(businessConf.getTOKEN_LENGTH(),
									businessConf.getAUTHENTICATION_TIME());
	}

	@Override
	public void add(User client)
	{

	}

	@Override
	public void add(List<User> userList)
	{
		Iterator<User> i     = userList.iterator();
		int            count = 0;
		List<Role> roleList= controller.roleController.get();

		while (i.hasNext())
		{
			try
			{
				count++;
				User client = i.next();
				if (!userValidationService.check(client))
				{
					continue;
				}
				long roleID = client.getRole().getId();
				Role role = new Role();
				role.setId(roleID);

				Iterator<Role> roleIterator = roleList.iterator();
				Role toCompare = roleIterator.next();

				while(!role.equals(toCompare) && roleIterator.hasNext())
				{
					toCompare = roleIterator.next();
				}
				if(!role.equals(toCompare))
				{
					throw new BusinessException(null);
				}
				role=toCompare;
				client.setRole(role);
				service.add(client);
			}
			catch (BusinessException e)
			{
				//сделать лог
			}
		}
	}

	@Override
	public boolean login(String token, long tokenExpires, String email, String pass)
	{
		//Если токен, полученный на вход валидный и не сгорел,
		//то попробуем поискать пользователя по полученным входным данным
		if (authoriser.existsToken(token) &&
				authoriser.timeLeft(token) > System.currentTimeMillis() &&
				service.login(token, tokenExpires, email, pass))
		{
			guestMap.remove(token);
			User user = get(email);
			authenticatedMap.put(user.getToken(),user);
			return true;
		}
		return false;
	}

	@Override
	public void logout(User client)
	{
		String token = client.getToken();
		authoriser.removeToken(token);
		if (guestMap.containsKey(token))
		{
			guestMap.remove(token);
			return;
		}
		authenticatedMap.remove(token);
		service.logout(client);
	}

	@Override
	public User getGuestUser()
	{
		User user = new User();
		user.setRole(GUEST);
		String token = authoriser.genToken();
		user.setToken(token);
		user.setTokenExpires(authoriser.timeLeft(token));
		guestMap.put(user.getToken(), user);
		return user;
	}
	@Override
	public User get(long id)
	{
		return service.get(id);
	}

	@Override
	public User get(String email)
	{
		return service.get(email);
	}

	@Override
	public boolean contains(String token)
	{
		return authenticatedMap.containsKey(token);
	}

	@Override
	public User getByToken(String token)
	{
		if (!authoriser.existsToken(token))
		{
			throw new ControllerException(new TokenNotFound());
		}
		User res;
		if ((res = guestMap.get(token)) == null)
		{
			res = authenticatedMap.get(token);
		}
		return res;
	}

	@Override
	public User get(User fio, Role role)
	{
		return null;
	}

	@Override
	public List<User> getByCourse(User fio, String courseName)
	{
		return null;
	}

	@Override
	public Set<User> filter(List<User> userList, Course c)
	{
		return service.filter(userList, c);
	}

	@Override
	public void updateFIO(User client, User fio)
	{

	}

	@Override
	public void updatePass(User client, String pass)
	{

	}

	@Override
	public void updateMail(User client, String email)
	{

	}

	@Override
	public void forgotPass(User client, String email)
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
	public void activate(User client)
	{
		service.activate(client);
	}

	@Override
	public void logoutAll()
	{
		for(User user:authenticatedMap.values())
		{
			service.logout(user);
		}
		authenticatedMap.clear();
		guestMap.clear();
		authoriser.clearTokens();
	}

	@Override
	public void delete(User initiator, User target)
	{
		if (initiator.getRole().getId() == controller.roleController.getAdmin().getId())
		{
			initiator = target;
		}

		//удалить все бандлы
		controller.bundleController.delete(initiator, target);
		try
		{
			//удалить все курсы
			List<Course> courseList = controller.courseController.getByOwner(initiator);
			controller.courseController.delete(initiator, courseList);
		}
		catch (DataAccessException e)
		{
			if (e.getCause().getClass() != ObjectNotFoundException.class)
			{
				throw e;
			}
		}


		service.delete(initiator, target);
	}

	@Override
	public void delete(List<User> userList)
	{

	}
}
