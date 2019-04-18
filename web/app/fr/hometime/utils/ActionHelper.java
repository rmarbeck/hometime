package fr.hometime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import models.LiveConfig;
import play.Logger;
import play.Play;
import play.mvc.Http.Context;
import play.mvc.Http.Flash;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;


/**
 * Helper for different actions
 * 
 * @author Raphael
 *
 */

public class ActionHelper {
	public static String NTBE_ACTIVE = "notify_team_by_email_active";
	public static String NBTE_TO_HOW_MANY = "notify_team_by_email_to_how_many";
	public static String NBTE_TO = "notify_team_by_email_to";
	public static String NBTE_FROM = "notify_team_by_email_from";
	
	private static String FLASH_ORIGIN = "flashscope_origin";
	
	private static String TEAM_ADDRESS_1 = "contact@hometime.fr";
	private static String TEAM_FROM_ADDRESS = "contact@hometime.fr";


	/**
	 * Send a mail to the team
	 * 
	 * @param toCheck
	 * @return validity check status
	 */
	public static void notifyTeamByEmail(String title, String message) {
		MailerAPI mail = prepareEmail(title);
    	Logger.info("About to send a mail");
    	mail.send(message);
	}
	
	public static void notifyTeamByEmailEnhanced(String title, String message) {
		sendHtmlEmailEnhanced(title, message);
	}
	
	/**
	 * Send an HTML email to the team
	 * 
	 * @param toCheck
	 * @return validity check status
	 */
	public static void sendHtmlEmail(String title, String htmlMessage) {
		MailerAPI mail = prepareEmail(title);
    	Logger.info("About to send an HTML mail");
    	mail.sendHtml(htmlMessage);
	}
	
	public static void sendHtmlEmailEnhanced(String title, String htmlMessage) {
		if (isConfigurationSaysEmailIsSupposerToBeSent()) {
			Logger.info("About to send an HTML mail Enhanced");
			try {
				MailjetAdapterv3_1.sendSimpleEmail(title, getListOfRecipientsFromConfiguration(), getFromAddressFromConfiguration(), getFromAddressFromConfiguration(), htmlMessage, "empty");
			} catch (MailAdapterException e) {
				Logger.error("Error when trying to send an HTML mail Enhanced");
				e.printStackTrace();
			}
		}
	}
	
	public static void asyncTryToNotifyTeamByEmail(String title, String message) {
		CompletableFuture.supplyAsync(() -> {
			ActionHelper.tryToNotifyTeamByEmail(title, message);
			return "OK";
		});
	}
	
	public static void asyncTryToSendHtmlEmail(String title, String htmlMessage) {
		CompletableFuture.supplyAsync(() -> {
			ActionHelper.tryToSendHtmlEmail(title, htmlMessage);
			return "OK";
		});
	}
	
	public static void tryToNotifyTeamByEmail(String title, String message) {
		try {
			notifyTeamByEmailEnhanced(title, message);
		} catch (Throwable t) {
			Logger.error("Unable to send email {}", t.getMessage());
			t.printStackTrace();
		}
	}
	
	public static void tryToSendHtmlEmail(String title, String htmlMessage) {
		try {
			sendHtmlEmailEnhanced(title, htmlMessage);
		} catch (Throwable t) {
			Logger.error("Unable to send email {}", t.getMessage());
			t.printStackTrace();
		}
	}
	
	public static void setOriginOfCall(Context ctx) {
		setOriginOfCall(ctx.flash(), ctx.request().uri());
	}
	
	public static void setOriginOfCall(Flash flash, String value) {
		flash.put(FLASH_ORIGIN, value);
	}
	
	public static String getOriginOfCall(Context ctx) {
		return getOriginOfCall(ctx.flash());
	}
	
	public static String getOriginOfCall(Flash flash) {
		return flash.get(FLASH_ORIGIN);
	}
	
	public static void rememberOriginOfLastCall(Context ctx) {
		rememberOriginOfLastCall(ctx.flash());
	}
	
	public static void rememberOriginOfLastCall(Flash flash) {
		setOriginOfCall(flash, getOriginOfCall(flash));
	}
	
	private static MailerAPI prepareEmail(String title) {
		// Looking if liveConfig exists and says to send e-mail
		if (isConfigurationSaysEmailIsSupposerToBeSent()) {
			MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
	    	mail.setSubject(title);

	    	mail.setFrom(getFromAddressFromConfiguration());
	    	
	    	List<String> recipients = getListOfRecipientsFromConfiguration();
	    	mail.setRecipient(recipients.toArray(new String[]{"empty"}));
	    	
	    	return mail;
		}
		return null;
	}
	
	private static boolean isConfigurationSaysEmailIsSupposerToBeSent() {
		if ((LiveConfig.isKeyDefined(NTBE_ACTIVE) && LiveConfig.getBoolean(NTBE_ACTIVE))
				|| (!LiveConfig.isKeyDefined(NTBE_ACTIVE) && Play.application().configuration().getBoolean("notifyTeam")) )
			return true;
		return false;
	}
	
	private static String getFromAddressFromConfiguration() {
		if (LiveConfig.getString(NBTE_FROM) != null)
			return LiveConfig.getString(NBTE_FROM);
		return TEAM_FROM_ADDRESS;
	}
	
	private static List<String> getListOfRecipientsFromConfiguration() {
		List<String> recipients = new ArrayList<String>();
		Long numberOfRecepients = LiveConfig.getLong(NBTE_TO_HOW_MANY);
    	if (numberOfRecepients == null) {
    		recipients.add(TEAM_ADDRESS_1);
    	} else {
    		for (int i = 1; i <= numberOfRecepients ; i++) {
    			recipients.add(LiveConfig.getString(NBTE_TO+"_"+i));
    		}
    	}
    	return recipients;
	}
}