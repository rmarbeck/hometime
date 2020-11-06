package controllers;

import fr.hometime.utils.AppointmentRequestHelper;
import models.AppointmentRequest;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class AppointmentRequests extends Controller {
	public static Crud<models.AppointmentRequest, models.AppointmentRequest> crud = Crud.of(
			models.AppointmentRequest.of(),
			views.html.admin.appointment_request.ref(),
			views.html.admin.appointment_request_form.ref(),
			views.html.admin.appointment_requests.ref());
	
	public static Result validateAppointment(Long id) {
		AppointmentRequest toValidate = AppointmentRequest.findById(id);
		if (!toValidate.isValid()) {
			AppointmentRequestHelper.validate(toValidate.uniqueKey);
			SMS.sendSMS(toValidate.customerPhoneNumber, Messages.get("sms.appointment.just.validated", toValidate.getNiceDisplayableDatetime()));
			SMS.sendSMS(toValidate.customerPhoneNumber, Messages.get("sms.appointment.to.validate.from.admin.action", Application.FRONT_END_URL+Application.APPOINTMENT_VALIDATION_URL, toValidate.uniqueKey));
		}
		return CrudHelper.displayAll("AppointmentRequests", 10);
		
	}
}
