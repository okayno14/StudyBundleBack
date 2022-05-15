package configuration;

public class LogConf
{
	String log4jConfPath;

	public LogConf(String log4jConfPath)
	{
		this.log4jConfPath = log4jConfPath;
	}

	public String getLog4jConfPath()
	{
		return log4jConfPath;
	}
}
