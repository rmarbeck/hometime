package fr.hometime.utils;

import java.util.Optional;


/**
 * Helper for date manipulation
 * 
 * @author Raphael
 *
 */

public class PhoneNumberHelper {
	private static PhoneNumberHelper singleton = null;
	
	public class FrenchPhoneNumber {
		private String rawNumber = null;
		private String internationalNumber = null;
		
		public FrenchPhoneNumber(String rawNumber) {
			if (rawNumber != null) {
				this.rawNumber = rawNumber;
				PhoneNumberHelper.tryToGetFrenchPhoneNumberInInternationalFormat(rawNumber).ifPresent(value -> this.internationalNumber = value);
			}
		}
		
		public boolean isValid() {
			return internationalNumber != null;
		}
		
		public boolean isAMobileNumber() {
			if (isValid())
				return isItAFrenchMobilePhoneNumber(internationalNumber);
			return false;
		}
		
		public Optional<String> getInInternationalFormat() {
			if (internationalNumber != null)
				return Optional.of(internationalNumber);
			return Optional.empty();
		}
	}
	
	public static FrenchPhoneNumber of(String rawNumber) {
		if (singleton == null)
			singleton = new PhoneNumberHelper();
		return singleton.new FrenchPhoneNumber(rawNumber);
	}
	
	public static Optional<String> tryToGetFrenchPhoneNumberInInternationalFormat(String phoneNumber) {
		String phoneNumberFound = null;
		if (isItAFrenchMobilePhoneNumber(phoneNumber))
			phoneNumberFound = removeSpaces(phoneNumber).replaceAll("^((\\+|00)33|0)([67])(\\d{8})$", "+33$3$4");
		if (phoneNumberFound != null)
			return Optional.of(phoneNumberFound);
		return Optional.empty();
	}
	
	public static String getFrenchPhoneNumberInInternationalFormat(String phoneNumber) {
		if (isItAFrenchMobilePhoneNumber(phoneNumber))
			return removeSpaces(phoneNumber).replaceAll("^((\\+|00)33|0)([67])(\\d{8})$", "+33$3$4");
		return null;
	}
	
	public static boolean isItAFrenchMobilePhoneNumber(String phoneNumber) {
		return removeSpaces(phoneNumber).matches("^((\\+|00)33|0)([67])(\\d{8})$");
	}
	
	public static boolean isItAValidPhoneNumber(String phoneNumber) {
		return removeSpaces(phoneNumber).matches("^((\\+|00)\\d{6,15})$");
	}
	
	private static String removeSpaces(String rawNumber) {
		if (rawNumber != null)
			return rawNumber.replaceAll("\\s","");
		return "";
	}
}