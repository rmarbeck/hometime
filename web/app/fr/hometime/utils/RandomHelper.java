package fr.hometime.utils;

import java.util.List;
import java.util.Random;

/**
 * Helper for random operations.
 * 
 * @author Raphael
 *
 */

public class RandomHelper {
	public static int getRandomInt(int lowerBoundIncluded, int upperBoundIncluded) {
		int nbOfValuesPossible = upperBoundIncluded - lowerBoundIncluded + 1;
		double valueToAddToLowerBound = Math.random()*(nbOfValuesPossible);
		int roundedOfValueToAddToLowerBound = (int) (Math.floor(valueToAddToLowerBound));
		return lowerBoundIncluded+roundedOfValueToAddToLowerBound;
	}
	
	public static long getRandomLong(long lowerBoundIncluded, long upperBoundIncluded) {
		long nbOfValuesPossible = upperBoundIncluded - lowerBoundIncluded + 1;
		double valueToAddToLowerBound = Math.random()*(nbOfValuesPossible);
		long roundedOfValueToAddToLowerBound = (long) (Math.floor(valueToAddToLowerBound));
		return lowerBoundIncluded+roundedOfValueToAddToLowerBound;
	}
	
	public static String getRandomAlphanumericString(int size) {
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    Random random = new Random();
	 
	    return random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(size)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	}
}
