package controllers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import fr.hometime.utils.AppointmentRequestHelper;
import models.AppointmentRequest;
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
		return doActionOnAppointment(id, AppointmentRequest::isWaitingValidation, (request) -> {
			AppointmentRequestHelper.validate(request);
			AppointmentRequestHelper.sendFirstSMSAfterValidation(request)
				.filter(AppointmentRequests::isSMSSentCorrectly)
					.map(currentSMS -> AppointmentRequestHelper.sendSecondSMSAfterValidation(request));
		});
	}
	
	public static Result sendValidationLink(Long id) {
		return doActionOnAppointment(id, AppointmentRequest::isWaitingValidation, (request) -> AppointmentRequestHelper.sendSMSForAskingValidation(request));
	}
	
	public static Result cancelAppointment(Long id) {
		return doActionOnAppointment(id, AppointmentRequest::isValidated, (request) -> {
			request.updateAfterCancelation();
			AppointmentRequestHelper.sendCancellationSMS(request);
		});
	}
	
	private static Result doActionOnAppointment(Long id, Predicate<AppointmentRequest> filter, Consumer<AppointmentRequest> toDo) {
		AppointmentRequest toOperateOn= AppointmentRequest.findById(id);
		if (filter.test(toOperateOn))
			toDo.accept(toOperateOn);
		return CrudHelper.displayAll("AppointmentRequests", 10);
	}
	
	private static boolean isSMSSentCorrectly(models.SMS currentSMS) {
		return currentSMS.smsCount == 1;
	}
	
	/**
	private static Optional<ObjectNode> cancelAppointmentAndSendSMS(String uniqueKey) {
		boolean alreadyCanceled = isItCanceled(uniqueKey);
		Optional<AppointmentRequest> appointment = AppointmentRequestHelper.cancel(uniqueKey);
		if (appointment.isPresent() && appointment.get().isCanceled()) {
			if (!alreadyCanceled) {
				AppointmentRequestHelper.sendCancellationSMS(appointment.get());
				ActionHelper.asyncTryToNotifyTeamByEmail("Rendez-vous annulé", getAppointementValidationMessage(appointment.get()));
			}
			return Optional.of(getAppointmentAsJsonNode(appointment.get()));
		}
		return Optional.empty();
	}
	 * 
	 */
}
