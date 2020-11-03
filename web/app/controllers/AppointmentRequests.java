package controllers;

import play.mvc.Controller;
import play.mvc.Security;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class AppointmentRequests extends Controller {
	public static Crud<models.AppointmentRequest, models.AppointmentRequest> crud = Crud.of(
			models.AppointmentRequest.of(),
			views.html.admin.appointment_request.ref(),
			views.html.admin.appointment_request_form.ref(),
			views.html.admin.appointment_requests.ref());
}
