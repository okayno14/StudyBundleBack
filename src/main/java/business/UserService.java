package business;

import dataAccess.cache.IUserCache;
import dataAccess.entity.Course;
import dataAccess.entity.Role;
import dataAccess.entity.User;
import dataAccess.repository.IUserRepo;
import exception.Business.AuthenticationException;
import exception.Business.BusinessException;
import exception.Business.NoRightException;
import exception.Business.NoSuchStateAction;
import exception.DataAccess.DataAccessException;
import exception.DataAccess.ObjectNotFoundException;

import java.util.List;
import java.util.Set;

public class UserService implements IUserService
{
	private IUserRepo  userRepo;
	private IUserCache userCache;


	public UserService(IUserRepo userRepo, IUserCache userCache)
	{
		this.userRepo  = userRepo;
		this.userCache = userCache;
	}

	@Override
	public User get(long id)
	{
		if(userCache.contains(id))
		{
			return userCache.get(id);
		}
		User res = userRepo.get(id);
		userCache.put(res);
		return res;
	}

	@Override
	public void add(User client)
	{
		try
		{
			userRepo.save(client);
			userCache.put(client);
		}
		catch (DataAccessException e)
		{
			throw new BusinessException(e);
		}
	}

	@Override
	public boolean login(String token, long tokenExpires, String email, String pass)
	{
		User user = null;
		try
		{
			user = userCache.getByEmail(email);
			if(!user.getPass().equals(pass))
			{
				user=null;
			}
		}
		catch (DataAccessException e)
		{
			try
			{
				user = userRepo.get(email, pass);
				userCache.put(user);
			}
			catch (DataAccessException e1)
			{
			}
		}
		finally
		{
			if(user==null || user.getToken() != null || user.isEmailState() == false)
			{
				return false;
			}
			user.setToken(token);
			user.setTokenExpires(tokenExpires);
			return true;
		}
	}

	@Override
	public void logout(User client)
	{
		client.setToken(null);
		client.setTokenExpires(0L);
		userCache.delete(client.getId());
	}

	@Override
	public User get(String email)
	{
		try
		{
			return userCache.getByEmail(email);
		}
		catch (DataAccessException e)
		{
			try
			{
				User user = userRepo.get(email);
				userCache.put(user);
				return user;
			}
			catch (DataAccessException ee)
			{
				throw ee;
			}
		}
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
		return userRepo.filter(userList,c);
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
	public void updateMail(User client,String email)
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
		if(client.isEmailState())
		{
			throw new NoSuchStateAction("activated");
		}
		client.setEmailState(true);
		userRepo.save(client);
	}

	@Override
	public void delete(User initiator, User target)
	{
		if(initiator.getId()!= target.getId())
		{
			throw new BusinessException(new NoRightException());
		}
		//удалить из группы
		userRepo.delete(initiator);
		userCache.delete(initiator.getId());
	}
}
