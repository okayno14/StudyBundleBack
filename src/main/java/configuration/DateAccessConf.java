package configuration;

import com.google.gson.annotations.Expose;

public class DateAccessConf
{
	@Expose
	String hibernateConf;
	@Expose
	String storagePath;
	private String supportedFormats[] = {"doc", "docx"};
	//По умолчанию 100 МБ
	private int    zipFileSizeLimit   = 100 * (int) Math.pow(2, 20);

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

	public String[] getSupportedFormats()
	{
		return supportedFormats;
	}

	public int getZipFileSizeLimit()
	{
		return zipFileSizeLimit;
	}
}


