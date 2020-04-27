package fr.hometime.utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringHelper {
	public static String maskPartially(String toMask) {
		return maskPartially(toMask, '*');
	}
	
	public static String maskPartially(String toMask, int clearEnds) {
		return maskPartiallyImpl(toMask, '*', clearEnds);
	}
	
	private static String maskPartially(String toMask, char maskChar) {
		return maskPartiallyImpl(toMask, maskChar, 2);
	}

	private static String maskPartiallyImpl(String toMask, char maskChar, int clearEnds) {
		if (toMask == null || clearEnds < 0)
			return null;
		
		char[] array = toMask.toCharArray();
		return IntStream.range(0, toMask.length()).mapToObj(index -> {
			if (index >= clearEnds && index <= (toMask.length() - 1) - clearEnds) {
				return maskChar;
			} else {
				return array[index];
			}
		}).map(String::valueOf).collect(Collectors.joining());
	}
}
