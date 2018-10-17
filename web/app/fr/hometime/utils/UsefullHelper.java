package fr.hometime.utils;

import java.util.Optional;

/**
 * Helper for different usefull functions
 * 
 * @author Raphael
 *
 */

public class UsefullHelper {
	public static Optional<Float> getFloatFromString(String supposedFloat) {
		if (supposedFloat!= null && !supposedFloat.equals(""))
		    try {
		      return Optional.of(Float.valueOf(supposedFloat.trim()));
		    } catch (NumberFormatException nfe) {
		      return Optional.empty();
		    }
		return Optional.empty();
	}
	
	public static Optional<Long> getLongFromString(String supposedLong) {
		if (supposedLong!= null && !supposedLong.equals(""))
		    try {
		      return Optional.of(Long.valueOf(supposedLong.trim()));
		    } catch (NumberFormatException nfe) {
		      return Optional.empty();
		    }
		return Optional.empty();
	}
}