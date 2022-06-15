package configuration;

import com.google.gson.annotations.Expose;

public class MailConf
{
	@Expose
	private final String mailAddress;
	@Expose
	private final String password;
	@Expose
	private final String smtpHost;
	@Expose
	private final String smtpPort;

	public MailConf(String mailAddress, String password, String smtpHost, String smtpPort)
	{
		this.mailAddress = mailAddress;
		this.password    = password;
		this.smtpHost    = smtpHost;
		this.smtpPort    = smtpPort;
	}

	public String getMailAddress()
	{
		return mailAddress;
	}

	public String getPassword()
	{
		return password;
	}

	public String getSmtpHost()
	{
		return smtpHost;
	}

	public String getSmtpPort()
	{
		return smtpPort;
	}
}
