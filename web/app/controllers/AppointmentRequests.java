package controllers;

import fr.hometime.utils.AppointmentRequestHelper;
import models.AppointmentRequest;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.F.Promise;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class AppointmentRequests extends Controller {
	public static Crud<models.AppointmentRequest, models.AppointmentRequest> crud = Crud.of(
			models.AppointmentRequest.of(),
			views.html.admin.appointment_request.ref(),
			views.html.admin.appointment_request_form.ref(),
			views.html.admin.appointment_requests.ref());
	
	public static Result validateAppointment(Long id) {
		AppointmentRequest toValidate = AppointmentRequest.findById(id);
		if (!toValidate.isValidated()) {
			AppointmentRequestHelper.validate(toValidate.uniqueKey);
			sendFirstSMS(toValidate)
				.filter(AppointmentRequests::isSMSSentCorrectly)
					.map(currentSMS -> sendSecondSMS(toValidate));
		}
		return CrudHelper.displayAll("AppointmentRequests", 10);
		
	}
	
	private static boolean isSMSSentCorrectly(models.SMS currentSMS) {
		return currentSMS.smsCount == 1;
	}
	
	private static Promise<models.SMS> sendFirstSMS(AppointmentRequest currentAppointment) {
		return sendSMS(currentAppointment, Messages.get("sms.appointment.just.validated", currentAppointment.getNiceDisplayableDatetime()));
	}
	
	private static Promise<models.SMS> sendSecondSMS(AppointmentRequest currentAppointment) {
		return sendSMS(currentAppointment, Messages.get("sms.appointment.to.validate.from.admin.action", Application.FRONT_END_URL+Application.APPOINTMENT_VALIDATION_URL, currentAppointment.uniqueKey));
	}
	
	private static Promise<models.SMS> sendSMS(AppointmentRequest currentAppointment, String message) {
		return SMS.sendSMS(currentAppointment.customerPhoneNumber, message);
	}
}
