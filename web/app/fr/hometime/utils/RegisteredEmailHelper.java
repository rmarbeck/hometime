package fr.hometime.utils;

import java.util.List;
import java.util.Optional;

import models.RegisteredEmail;
import play.Logger;

public class RegisteredEmailHelper {
	private static final int DEFAULT_NUMBER_OF_REGISTRATION_TODAY = 50;
	private static final String MAX_NUMBER_OF_REGISTRATION_TODAY = "max_number_of_registration_of_emails_per_day";
	
	public static boolean doesAlreadyExist(String email) {
		return RegisteredEmail.findByEmail(email) != null;
	}
	
	public static Optional<RegisteredEmail> createNewRegisteredEmailIfAuthorized(String email) {
		if (!hasRegisteredTwoMuchEmailToDay())
			return Optional.of(createNewRegisteredEmail(email));
		Logger.error("Registering email is not allowed");
		return Optional.empty();
	}
	
	private static boolean hasRegisteredTwoMuchEmailToDay() {
		List<RegisteredEmail> todayRegistrations = RegisteredEmail.findAllRegisteredToday();
		if (todayRegistrations != null)
			return todayRegistrations.size() > getMaxNumberOfRegistrationsAllowedPerDay();
		return false;
	}
	
	private static int getMaxNumberOfRegistrationsAllowedPerDay() {
		return LiveConfigHelper.tryToGetLong(MAX_NUMBER_OF_REGISTRATION_TODAY).orElse(Long.valueOf(DEFAULT_NUMBER_OF_REGISTRATION_TODAY)).intValue();
	}
	
	private static RegisteredEmail createNewRegisteredEmail(String email) {
		RegisteredEmail newRegisteredEmail = new RegisteredEmail(email);
		newRegisteredEmail.save();
		return newRegisteredEmail;
	}
}
