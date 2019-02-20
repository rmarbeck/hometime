package fr.hometime.utils;

import models.LiveConfig;
import play.Play;

import static fr.hometime.utils.LiveConfigConstants.*;

/**
 * Helper for Live Config operations.
 * 
 * @author Raphael
 *
 */

public class LiveConfigHelper {
	public static boolean isInIntegrationMode() {
		return Play.application().configuration().getBoolean("integrationMode");
	}
	
	public static boolean isNotifyingAuthorized(String secret) {
		if (LiveConfig.isKeyDefined(NOTIFYING_SECRET))
			if (secret != null && secret.equals(LiveConfig.getString(NOTIFYING_SECRET)))
				return true;
		return false;
	}
}
