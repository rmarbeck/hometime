package fr.hometime.payment.systempay;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.IntStream;

import play.Logger;
import models.PaymentRequest;

public class Helper {
	private static final String DATE_TRANS_FORMAT = "YYYYMMddHHmmss";
	private static final String PAYMENT_SITE_ID_CONFIGURATION_KEY = "fr.hometime.payment.systempay.site.id";
	private static final String PAYMENT_MODE_CONFIGURATION_KEY = "fr.hometime.payment.systempay.mode";
	private static final String PAYMENT_KEY_CONFIGURATION_KEY = "fr.hometime.payment.systempay.key";

	public static String getParameterPrefix() {
		return DataDictionnary.PARAMETER_PREFIX;
	}
	
	public static String getSiteId() {
		return getFromConfiguration(PAYMENT_SITE_ID_CONFIGURATION_KEY);
	}
	
	public static String getCtxMode() {
		return getFromConfiguration(PAYMENT_MODE_CONFIGURATION_KEY);
	}
	
	public static String generateTransDate() {
		LocalDateTime nowInUtc = LocalDateTime.now(Clock.systemUTC());
		return nowInUtc.format(DateTimeFormatter.ofPattern(DATE_TRANS_FORMAT));
	}
	
	public static String generateTransId() {
		LocalDateTime nowInUtc = LocalDateTime.now(Clock.systemUTC());
		String nbOfSecondsToday = "" + ( nowInUtc.getHour() * 3600 + nowInUtc.getMinute() * 60 + nowInUtc.getSecond() );
		int nbOfMissingZerosToFillString = 6 - nbOfSecondsToday.length();
		StringBuilder leadingZeros = new StringBuilder();
		IntStream.range(0, nbOfMissingZerosToFillString).forEach(
				i -> leadingZeros.append("0")
			);
		
		return leadingZeros.toString() + nbOfSecondsToday;
	}
	
	
	public static int getCurrencyEuroCode() {
		return DataDictionnary.CURRENCY_CODE_EURO;
	}
	
	public static String getInteractiveActionMode() {
		return DataDictionnary.ACTION_MODE_INTERACTIVE;
	}
	
	public static String getPaymentPageAction() {
		return DataDictionnary.PAGE_ACTION_PAYMENT;
	}
	
	public static String getCurrentVersion() {
		return DataDictionnary.CURRENT_VERSION;
	}
	
	public static String getSinglePaymentConfig() {
		return DataDictionnary.PAYMENT_CONFIG_SINGLE;
	}
	
	public static int getCaptureDelay(PaymentRequest request) {
		switch (request.typeOfPayment) {
			case ONE_SHOT_IMMEDIATE:
				return 0;
			case ONE_SHOT_POSTPONED:
				return request.delayInDays;
			default:
				return 0;
		}
	}
	
	public static String getTransId(PaymentRequest request) {
		return generateTransId();
	}
	
	public static String getOrderId(PaymentRequest request) {
		return request.orderNumber;
	}
	
	public static String getSinglePaymentConfig(PaymentRequest request) {
		switch (request.typeOfPayment) {
			case ONE_SHOT_IMMEDIATE:
			case ONE_SHOT_POSTPONED:
				return DataDictionnary.PAYMENT_CONFIG_SINGLE;
			case MULTI:
				return DataDictionnary.PAYMENT_CONFIG_MULTI;
			default:
				return DataDictionnary.PAYMENT_CONFIG_SINGLE;
		}
	}
	
	public static int getAutomaticValidationMode() {
		return DataDictionnary.AUTOMATIC_VALIDATION_MODE;
	}
	
	public static int getPriceFromRequest(PaymentRequest request) {
		return Float.valueOf(request.priceInEuros*100f).intValue();
	}
	
	
	public static String generateSignature(PaymentForm form) {
		SortedSet<String> vadsFields = new TreeSet<String>();
		Iterator<String> paramNames = form.getParameterFullNames().iterator();
		while (paramNames.hasNext()) {
			String paramName = paramNames.next();
			if (paramName.startsWith( "vads_" )) {
				vadsFields.add(paramName);
			}
		}
		String sep = Sha.SEPARATOR;
		StringBuilder sb = new StringBuilder();
		for (String vadsParamName : vadsFields) {
			String vadsParamValue = form.getParameterValueAsString(vadsParamName);
			if (vadsParamValue != null) {
				sb.append(vadsParamValue);
			}
			sb.append(sep);
		}
		Logger.error("!!!!!!!!! : " + getFromConfiguration(PAYMENT_KEY_CONFIGURATION_KEY));
		sb.append( getFromConfiguration(PAYMENT_KEY_CONFIGURATION_KEY) );
		String c_sign = Sha.encode(sb.toString());
		return c_sign;
	}
	
	public static String getParameterFullName(String parameterShortName) {
		return getParameterPrefix()+parameterShortName;
	}
	
	private static String getFromConfiguration(String key) {
		return play.Configuration.root().getString(key);
	}
}
