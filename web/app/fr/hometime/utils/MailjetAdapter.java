package fr.hometime.utils;

import java.util.ArrayList;
import java.util.List;

import models.LiveConfig;
import models.ServiceTest;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import play.Logger;
import play.Play;


/**
 * Helper for different actions
 * 
 * @author Raphael
 *
 */

public class MailjetAdapter {
	public static String MAILJET_API_KEY = "mailjet_api_key";
	public static String MAILJET_SECRET_KEY = "mailjet_secret_key";
	public static String ORDERS_SENDER_ADDRESS = "orders_sender_email_address";
	
	public static String MAILJET_HOST = "in.mailjet.com";
	public static String MAILJET_SOCKET_FACTORY_PORT = "465";
	public static String MAILJET_PORT = "465";
	public static String MAILJET_CLASS = "javax.net.ssl.SSLSocketFactory";
	
	
	/**
	 * Send a message through mailjet
	 * 
	 * @param toCheck
	 * @return validity check status
	 */
	public static void sendMessage(String title, String to, String content) throws MessagingException {
		if (!LiveConfig.isKeyDefined(MAILJET_API_KEY) || !LiveConfig.isKeyDefined(MAILJET_SECRET_KEY) || !LiveConfig.isKeyDefined(ORDERS_SENDER_ADDRESS)) {
			Logger.error("Mailjet adapter is misconfigured, at least one LiveConfig value is missing.");
			return;
		}
		
		final String APIKey = LiveConfig.getString(MAILJET_API_KEY);
		final String SecretKey = LiveConfig.getString(MAILJET_SECRET_KEY);
		String From = LiveConfig.getString(ORDERS_SENDER_ADDRESS);
 
		Properties props = new Properties ();
 
		props.put ("mail.smtp.host", MAILJET_HOST);
		props.put ("mail.smtp.socketFactory.port", MAILJET_SOCKET_FACTORY_PORT);
		props.put ("mail.smtp.socketFactory.class", MAILJET_CLASS);
		props.put ("mail.smtp.auth", "true");
		props.put ("mail.smtp.ssl.trust", "*");
		props.put ("mail.smtp.port", MAILJET_PORT);
 
		Session session = Session.getInstance (props,
			new javax.mail.Authenticator ()
			{
				protected PasswordAuthentication getPasswordAuthentication ()
				{
					return new PasswordAuthentication (APIKey, SecretKey);
				}
			});
 
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(From));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		message.setSubject(title);
		//message.setText ("Your mail from Mailjet, sent by Java.");
		message.setContent(message, "text/html; charset=utf-8");
		
		Transport.send(message);
	}
	
	public static void tryToSendMessage(String title, String to, String content) {
		try {
			sendMessage(title, to, content);
		} catch (Throwable t) {
			Logger.error("Unable to send email {}", t.getMessage());
			t.printStackTrace();
		}
	}
}