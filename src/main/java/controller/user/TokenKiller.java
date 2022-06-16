package controller.user;

import configuration.BusinessConfiguration;
import dataAccess.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

public class TokenKiller implements Runnable
{
	private Thread t;
	private UserController userController;
	private Logger logger = LoggerFactory.getLogger(TokenKiller.class);
	private final long AUTH_TIMER_CLOCK_MS;


	public TokenKiller(UserController userController, BusinessConfiguration businessConfiguration)
	{
		this.userController = userController;

		this.AUTH_TIMER_CLOCK_MS= businessConfiguration.getAUTH_TIMER_CLOCK_MS();


		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Thread.sleep(AUTH_TIMER_CLOCK_MS);
				int counter = 0;
				counter+=cleanMap(userController.getGuestMap());
				counter+=cleanMap(userController.getAuthenticatedMap());
				logger.trace("Уничтожено {} токенов",counter);
			}
			catch (InterruptedException e)
			{
				logger.warn("Ошибка остановки потока");
			}
		}
	}

	private int cleanMap(Map<String, User> map)
	{
		int counter=0;
		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<String,User> entry = (Map.Entry<String,User>) it.next();
			User user = entry.getValue();
			String token = user.getToken();
			long timeLeft = user.getTokenExpires() - System.currentTimeMillis();
			if(user.getTokenExpires()!=0L && timeLeft<=0)
			{
				userController.logout(user);
				counter++;
			}
		}
		return counter;
	}
}
