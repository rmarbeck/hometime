package fr.hometime.utils;

import java.util.Date;
import java.util.Optional;

import models.AppointmentRequest;
import models.AppointmentRequest.Status;


/**
 * Helper for appointment actions
 * 
 * @author Raphael
 *
 */

public class AppointmentRequestHelper {	
	public static Optional<AppointmentRequest> findByKey(String key) {
		AppointmentRequest appointmentFound = AppointmentRequest.findByKey(key);
		if (appointmentFound == null)
			return Optional.empty();
		return Optional.of(appointmentFound);
	}
	
	private static boolean isValidable(Optional<AppointmentRequest> appointmentRequest) {
		return appointmentRequest.map(current -> isStatusValidable(current) && ! isThereSameOptionAlreadyValidated(current)).orElse(false);
	}
	
	private static boolean isStatusValidable(AppointmentRequest appointmentRequest) {
		return appointmentRequest.isInFuture() && appointmentRequest.status.equals(Status.WAITING_VALIDATION);
	}
	
	private static boolean isThereSameOptionAlreadyValidated(AppointmentRequest appointmentRequest) {
		return AppointmentRequest.findByAppointment(appointmentRequest.appointmentAsDate).stream().anyMatch(current -> current.status.equals(Status.VALIDATED));
	}
	
	public static Optional<AppointmentRequest> validate(String key) {
		Optional<AppointmentRequest> appointmentFound = findByKey(key); 
		if (isValidable(appointmentFound))
			appointmentFound.get().updateAfterConfirmation();
		return appointmentFound;
	}
	
	public static Optional<AppointmentRequest> cancel(String key) {
		Optional<AppointmentRequest> appointmentFound = findByKey(key);
		if (appointmentFound.isPresent())
			appointmentFound.get().updateAfterCancelation();
		return appointmentFound;
	}
	
	public static String getAppointmentAsStringFromDate(Date appointmentAsDate) {
		return AppointmentOptionHelper.convertToLocalDateAsString(appointmentAsDate);
	}
	
	public static Date getAppointmentAsDateFromString(String appointmentAsString) {
		return AppointmentOptionHelper.convertToDateFromString(appointmentAsString);
	}
}