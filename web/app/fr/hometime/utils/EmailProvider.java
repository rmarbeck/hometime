package fr.hometime.utils;

import java.util.List;

public interface EmailProvider {
	public void sendHtmlEmail(String title, String htmlMessage, List<String> emails, String fromAddress, String fromName, String textMessage);
}
