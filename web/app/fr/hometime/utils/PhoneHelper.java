package fr.hometime.utils;

/**
 * Helper for date manipulation
 * 
 * @author Raphael
 *
 */

public class PhoneHelper {
	public static String toReadableFormat(String rawPhoneNumber) {
		return rawPhoneNumber.replaceFirst("(\\d*)(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1 $2 $3 $4 $5");
	}
}