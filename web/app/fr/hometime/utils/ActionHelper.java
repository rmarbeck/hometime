package fr.hometime.utils;

import models.LiveConfig;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import play.Logger;
import play.Play;
import play.mvc.Http.Session;


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
	
	private static String TEAM_ADDRESS_1 = "contact@hometime.fr";
	private static String TEAM_FROM_ADDRESS = "rmarbeck@gmail.com";

	/**
	 * Send a mail to the team
	 * 
	 * @param toCheck
	 * @return validity check status
	 */
	public static void notifyTeamByEmail(String title, String message) {
		// Looking if liveConfig exists and says to send e-mail
		if ((LiveConfig.isKeyDefined(NTBE_ACTIVE) && LiveConfig.getBoolean(NTBE_ACTIVE))
				|| (!LiveConfig.isKeyDefined(NTBE_ACTIVE) && Play.application().configuration().getBoolean("notifyTeam")) ) {
			MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
	    	mail.setSubject(title);
	    	String from = LiveConfig.getString(NBTE_FROM);
	    	if (from == null)
	    		from = TEAM_FROM_ADDRESS;
	    	mail.setFrom(TEAM_FROM_ADDRESS);
	    	Long numberOfRecepients = LiveConfig.getLong(NBTE_TO_HOW_MANY);
	    	if (numberOfRecepients == null) {
	    		mail.setRecipient(TEAM_ADDRESS_1);
	    	} else {
	    		StringBuilder builder = new StringBuilder();
	    		for (int i = 1; i <= numberOfRecepients ; i++) {
	    			if (i!=1)
	    				builder.append(",");
	    			builder.append(LiveConfig.getString(NBTE_TO+"_"+i));
	    		}
	    		mail.setRecipient(builder.toString());
	    	}

	    	//mail.setReplyTo("xx@yy");
	    	Logger.info("About to send a mail");
	    	mail.send(message);

	    	/*
	    	//sends html
	    	mail.sendHtml("<html>html</html>" );
	    	//sends text/text
	    	mail.send( "text" );
	    	//sends both text and html
	    	mail.send( "text", "<html>html</html>");
	    	*/
		}
		return;
	}
	
	public static void tryToNotifyTeamByEmail(String title, String message) {
		try {
			notifyTeamByEmail(title, message);
		} catch (Throwable t) {
			Logger.error("Unable to send email {}", t.getMessage());
			t.printStackTrace();
		}
	}
}
