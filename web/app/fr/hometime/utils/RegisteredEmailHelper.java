package fr.hometime.utils;

import models.RegisteredEmail;

public class RegisteredEmailHelper {
	public static boolean doesAlreadyExist(String email) {
		return RegisteredEmail.findByEmail(email) != null;
	}
	
	public static RegisteredEmail createNewRegisteredEmail(String email) {
		RegisteredEmail newRegisteredEmail = new RegisteredEmail(email);
		newRegisteredEmail.save();
		return newRegisteredEmail;
	}
}
