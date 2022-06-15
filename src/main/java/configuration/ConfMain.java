package configuration;

import com.google.gson.annotations.Expose;

public class ConfMain
{
	@Expose
	private HTTP_Conf             http_conf;
	@Expose
	private DateAccessConf        dateAccessConf;
	@Expose
	private LogConf               logConf;
	@Expose
	private MailConf              mailConf;
	private BusinessConfiguration businessConfiguration = new BusinessConfiguration();
	@Expose
	private String                resourcesPath         = "resources";

	public ConfMain(HTTP_Conf http_conf, DateAccessConf dateAccessConf, LogConf logConf,
					MailConf mailConf, String resourcesPath)
	{
		this.http_conf      = http_conf;
		this.dateAccessConf = dateAccessConf;
		this.logConf        = logConf;
		this.mailConf       = mailConf;
		this.resourcesPath  = resourcesPath;
	}

	public void makeSubConfigs()
	{
		dateAccessConf.hibernateConf = resourcesPath + "/" + dateAccessConf.hibernateConf;
		logConf.log4jConfPath        = resourcesPath + "/" + logConf.log4jConfPath;
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

	public LogConf getLogConf()
	{
		return logConf;
	}

	public MailConf getMailConf()
	{
		return mailConf;
	}

	public void setResourcesPath(String resourcesPath)
	{
		this.resourcesPath = resourcesPath;
	}
}
