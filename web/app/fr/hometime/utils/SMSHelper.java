package fr.hometime.utils;

import java.util.List;
import models.SMS;
import fr.hometime.utils.PhoneNumberHelper.FrenchPhoneNumber;
import play.Logger;

/**
 * Helper for date manipulation
 * 
 * @author Raphael
 *
 */

public class SMSHelper {
	public static boolean canSendSMS(String rawPhoneNumber) {
		return isItPossibleSendSMS(rawPhoneNumber);
	}
	
	private static boolean isItPossibleSendSMS(String rawPhoneNumber) {
		FrenchPhoneNumber frenchPhoneNumber = PhoneNumberHelper.of(rawPhoneNumber);
		if (frenchPhoneNumber.isAMobileNumber())
			return isAuthorizedToSendSMS(frenchPhoneNumber);
		Logger.info("SMS Sending is not possible as the mobile phone is not recognised as a french mobile number : "+maskPhoneNumber(rawPhoneNumber));
		return false;
	}
	
	private static boolean isAuthorizedToSendSMS(FrenchPhoneNumber frenchPhoneNumber) {
		if (getNumberOfSMSAlreadySent(frenchPhoneNumber.getInInternationalFormat().get()) > 0)
			return true;
		return false;
	}
	
	private static int getNumberOfSMSAlreadySent(String phoneNumber) {
		List<SMS> listOfSMS = SMS.findByPhoneNumber(phoneNumber);
		if (listOfSMS != null)
			return listOfSMS.size();
		return 0;
	}
	
	private static String maskPhoneNumber(String rawPhoneNumber) {
		if (rawPhoneNumber != null) {
			int length = rawPhoneNumber.length();
			if (length > 2) {
				return rawPhoneNumber.substring(0, length - 2)+"XX";
			} else {
				return "XX";
			}
		}
		return "empty";
	}
}