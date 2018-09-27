package fr.hometime.utils;

import play.Play;

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
}
