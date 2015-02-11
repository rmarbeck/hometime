package fr.hometime.utils;

import java.util.List;

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
}
