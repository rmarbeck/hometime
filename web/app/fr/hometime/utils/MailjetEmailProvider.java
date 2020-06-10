package fr.hometime.utils;

import java.util.List;

import play.Logger;

public class MailjetEmailProvider implements EmailProvider {
	
	public MailjetEmailProvider() {
		super();
	}

	@Override
	public void sendHtmlEmail(String title, String htmlMessage, List<String> emails, String fromAddress,
			String fromName, String textMessage) {
		try {
			MailjetAdapterv3_1.sendSimpleEmail(title, emails, fromName, fromAddress, htmlMessage, textMessage);
		} catch (MailAdapterException e) {
			Logger.error("Error when trying to send an HTML mail Enhanced with Mailjet");
			e.printStackTrace();
		}		
	}

}
