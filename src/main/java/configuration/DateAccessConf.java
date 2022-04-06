package configuration;

public class DateAccessConf
{
	String hibernateConf;
	String storagePath;

	public DateAccessConf(String hibernateConf, String storagePath)
	{
		this.hibernateConf = hibernateConf;
		this.storagePath   = storagePath;
	}

	public String getHibernateConf()
	{
		return hibernateConf;
	}

	public String getStoragePath()
	{
		return storagePath;
	}
}


