package fr.hometime.utils;

import java.util.List;

import play.Logger;

public class SendInBlueEmailProvider implements EmailProvider {
	
	public SendInBlueEmailProvider() {
		super();
	}

	@Override
	public void sendHtmlEmail(String title, String htmlMessage, List<String> emails, String fromAddress,
			String fromName, String textMessage) {
		try {
			SendInBlueAdapter.sendSimpleEmail(title, emails, fromName, fromAddress, htmlMessage, textMessage);
		} catch (MailAdapterException e) {
			Logger.error("Error when trying to send an HTML mail Enhanced with SendInBlue");
			e.printStackTrace();
		}		
	}

}
