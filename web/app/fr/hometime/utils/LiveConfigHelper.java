package fr.hometime.utils;

import models.LiveConfig;
import play.Play;

import static fr.hometime.utils.LiveConfigConstants.*;

import java.util.Date;
import java.util.Optional;

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
	
	public static boolean isStoreClosed() {
		return tryToGetBoolean(STORE_CLOSED).orElse(false);
	}
	
    
    /**
     * Get Date value
     */
    public static Optional<Date> tryToGetDate(String key) {
    	return tryToGetConfig(key).map(config -> Optional.of(config.valuedate)).orElse(Optional.empty());
    }
    
    /**
     * Get String value
     */
    public static Optional<String> tryToGetString(String key) {
    	return tryToGetConfig(key).map(config -> Optional.of(config.valuestring)).orElse(Optional.empty());
    }
    
    /**
     * Get boolean value
     */
    public static Optional<Boolean> tryToGetBoolean(String key) {
    	return tryToGetConfig(key).map(config -> Optional.of(Boolean.valueOf(config.valueboolean))).orElse(Optional.empty());
    }
    
    /**
     * Get Long value
     */
    public static Optional<Long> tryToGetLong(String key) {
    	return tryToGetConfig(key).map(config -> Optional.of(config.valuelong)).orElse(Optional.empty());
    }
    
    private static Optional<LiveConfig> tryToGetConfig(String key) {
    	if (key != null) {
    		LiveConfig config = LiveConfig.find.byId(key);
    		if (config != null)
    			return Optional.of(config);
    	}
    	return Optional.empty();
    }
}
