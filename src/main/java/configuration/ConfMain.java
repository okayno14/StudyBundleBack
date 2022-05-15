package configuration;

import com.google.gson.annotations.Expose;

public class ConfMain
{
	@Expose
	private HTTP_Conf             http_conf;
	@Expose
	private DateAccessConf        dateAccessConf;
	private BusinessConfiguration businessConfiguration = new BusinessConfiguration();
	@Expose
	private String                resourcesPath         = "resources";

	public ConfMain(HTTP_Conf http_conf, DateAccessConf dateAccessConf, String resourcesPath)
	{
		this.http_conf      = http_conf;
		this.dateAccessConf = dateAccessConf;
		this.resourcesPath  = resourcesPath;
	}

	public HTTP_Conf getHttp_conf()
	{
		return http_conf;
	}

	public DateAccessConf getDateAccessConf()
	{
		return dateAccessConf;
	}

	public BusinessConfiguration getBusinessConfiguration()
	{
		return businessConfiguration;
	}

	public void makeSubConfigs()
	{
		dateAccessConf.hibernateConf = resourcesPath+"/"+dateAccessConf.hibernateConf;
	}
}
