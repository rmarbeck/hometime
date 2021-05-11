package fr.hometime.utils;

import java.util.Date;
import java.util.Optional;

import controllers.Application;
import controllers.SMS;
import models.AppointmentRequest;
import models.AppointmentRequest.Status;
import play.i18n.Messages;
import play.libs.F.Promise;


/**
 * Helper for appointment actions
 * 
 * @author Raphael
 *
 */

public class AppointmentRequestHelper {	
	private static String FRONT_END_URL = "https://www.hometime.fr";
	private static String APPOINTMENT_VALIDATION_URL = "/a/v/";
	
	public static Optional<AppointmentRequest> findByKey(String key) {
		AppointmentRequest appointmentFound = AppointmentRequest.findByKey(key);
		if (appointmentFound == null)
			return Optional.empty();
		return Optional.of(appointmentFound);
	}
	
	private static boolean isValidable(AppointmentRequest appointmentRequest) {
		return isStatusValidable(appointmentRequest) && ! isThereSameOptionAlreadyValidated(appointmentRequest);
	}
	
	private static boolean isStatusValidable(AppointmentRequest appointmentRequest) {
		return appointmentRequest.isInFuture() && appointmentRequest.status.equals(Status.WAITING_VALIDATION);
	}
	
	private static boolean isThereSameOptionAlreadyValidated(AppointmentRequest appointmentRequest) {
		return AppointmentRequest.findByAppointment(appointmentRequest.appointmentAsDate).stream().anyMatch(current -> current.status.equals(Status.VALIDATED));
	}
	
	public static void validate(AppointmentRequest toValidate) {
		if (isValidable(toValidate))
			toValidate.updateAfterConfirmation();
	}
	
	public static void cancel(AppointmentRequest toCancel) {
		toCancel.updateAfterCancelation();
	}
	
	public static Optional<AppointmentRequest> validate(String key) {
		Optional<AppointmentRequest> appointmentFound = findByKey(key);
		appointmentFound.ifPresent(AppointmentRequestHelper::validate);
		return appointmentFound;
	}
	
	public static Optional<AppointmentRequest> cancel(String key) {
		Optional<AppointmentRequest> appointmentFound = findByKey(key);
		appointmentFound.ifPresent(AppointmentRequestHelper::cancel);
		return appointmentFound;
	}
	
	public static String getAppointmentAsStringFromDate(Date appointmentAsDate) {
		return AppointmentOptionHelper.convertToLocalDateAsString(appointmentAsDate);
	}
	
	public static Date getAppointmentAsDateFromString(String appointmentAsString) {
		return AppointmentOptionHelper.convertToDateFromString(appointmentAsString);
	}
		
	public static Promise<models.SMS> sendSMSForAskingValidation(AppointmentRequest currentAppointment) {
		return sendSMS(currentAppointment, askingValidationMessage(currentAppointment));
	}
	
	private static String askingValidationMessage(AppointmentRequest appointmentRequest) {
		return Messages.get("sms.appointment.to.validate", FRONT_END_URL+APPOINTMENT_VALIDATION_URL, appointmentRequest.uniqueKey);
	}
	
	public static Promise<models.SMS> sendFirstSMSAfterValidation(AppointmentRequest currentAppointment) {
		return sendSMS(currentAppointment, Messages.get("sms.appointment.just.validated", currentAppointment.getNiceDisplayableDatetime()));
	}
	
	public static Promise<models.SMS> sendSecondSMSAfterValidation(AppointmentRequest currentAppointment) {
		return sendSMS(currentAppointment, Messages.get("sms.appointment.to.validate.from.admin.action", FRONT_END_URL+APPOINTMENT_VALIDATION_URL, currentAppointment.uniqueKey));
	}
	
	public static Promise<models.SMS> sendCancellationSMS(AppointmentRequest currentAppointment) {
		return sendSMS(currentAppointment, Messages.get("sms.appointment.just.canceled"));
	}
	
	public static Promise<models.SMS> sendSMS(AppointmentRequest currentAppointment, String message) {
		return SMS.sendSMS(currentAppointment.customerPhoneNumber, message);
	}
}