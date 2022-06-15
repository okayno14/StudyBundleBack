package view.mail;

import javax.mail.PasswordAuthentication;

public class Authenticator extends javax.mail.Authenticator
{
	private PasswordAuthentication passAuth;

	public Authenticator(PasswordAuthentication passAuth)
	{
		this.passAuth = passAuth;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication()
	{
		return passAuth;
	}
}
