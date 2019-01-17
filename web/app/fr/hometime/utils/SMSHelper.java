package fr.hometime.utils;

import java.util.List;

import models.OrderRequest;
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
	public static boolean canSendSubsequentSMS(OrderRequest request) {
		if (request == null)
			return false;
		return isItPossibleSendSMS(request.phoneNumber, false);
	}
	
	public static boolean canSendSubsequentSMS(String rawPhoneNumber) {
		return isItPossibleSendSMS(rawPhoneNumber, false);
	}
	
	public static boolean canSendFirstSMS(OrderRequest request) {
		if (request == null)
			return false;
		return isItPossibleSendSMS(request.phoneNumber, true);
	}
	
	public static boolean canSendFirstSMS(String rawPhoneNumber) {
		return isItPossibleSendSMS(rawPhoneNumber, true);
	}
	
	private static boolean isItPossibleSendSMS(String rawPhoneNumber, boolean shouldBlockSubsequentSMS) {
		FrenchPhoneNumber frenchPhoneNumber = PhoneNumberHelper.of(rawPhoneNumber);
		if (frenchPhoneNumber.isAMobileNumber())
			if (shouldBlockSubsequentSMS) {
				return isAuthorizedToSendSMS(frenchPhoneNumber);
			} else {
				return true;
			}
		Logger.debug("SMS Sending is not possible as the mobile phone is not recognised as a french mobile number : "+maskPhoneNumber(rawPhoneNumber));
		return false;
	}
	
	private static boolean isAuthorizedToSendSMS(FrenchPhoneNumber frenchPhoneNumber) {
		if (getNumberOfSMSAlreadySent(frenchPhoneNumber.getInInternationalFormat().get()) > 0) {
			Logger.debug("SMS Sending is not possible as the mobile phone has already received a previous SMS: "+maskPhoneNumber(frenchPhoneNumber.getRawNumber()));
			return false;
		}
		return true;
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