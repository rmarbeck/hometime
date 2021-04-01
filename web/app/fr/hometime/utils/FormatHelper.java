package fr.hometime.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatHelper {
	public static String noDigitCurrency(float value) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
		formatter.setMaximumFractionDigits(0);
		return formatter.format(value);
	}

}
