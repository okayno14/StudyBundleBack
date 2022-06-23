package view.mail;

import configuration.MailConf;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailAgent
{
	private Session session;
	private String  mailAddress;

	public MailAgent(MailConf mailConf) throws MessagingException
	{
		mailAddress = mailConf.getMailAddress();

		Properties props = new Properties();

		Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+@");
		Matcher matcher = pattern.matcher(mailAddress);
		matcher.find();
		String username = matcher.group();
		username = username.replace("@", "");

		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", mailConf.getSmtpHost());
		props.setProperty("mail.smtp.port", mailConf.getSmtpPort());
		props.setProperty("mail.smtp.user", username);
		props.setProperty("mail.smtp.ssl.enable", "true");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.debug", "true");

		Authenticator authenticator = new Authenticator(
				new PasswordAuthentication("okayno14", "Lolki191"));

		session = Session.getInstance(props, authenticator);
		session.setDebug(true);
	}

	public void sayHello() throws MessagingException
	{
		sendSimpleMessage("Hello, from Java","okayno14@gmail.com","GreetingsAuto");
	}

	public void sendConfirmMail(String link, String recipient) throws MessagingException
	{
		sendSimpleMessage(link,recipient,"Account activation");
	}

	public void sendPass()
	{

	}

	private void sendSimpleMessage(String body, String recipient, String subject)
			throws MessagingException
	{
		Message message = new MimeMessage(session);
		message.setFrom(InternetAddress.parse(mailAddress)[0]);
		message.addRecipient(Message.RecipientType.TO, InternetAddress.parse(recipient)[0]);
		message.setSubject(subject);

		message.setText(body);

		Transport.send(message);
	}
}
