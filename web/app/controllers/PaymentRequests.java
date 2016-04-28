package controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import fr.watchnext.store.utils.payment.systempay.DataDictionnary;
import fr.watchnext.store.utils.payment.systempay.PaymentConfirmation;
import fr.watchnext.store.utils.payment.systempay.SingleImmediatePF;
import models.PaymentRequest;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class PaymentRequests extends Controller {
	public static Crud<PaymentRequest, PaymentRequest> crud = Crud.of(
			PaymentRequest.of(),
			views.html.admin.payment.payment_request.ref(),
			views.html.admin.payment.payment_request_form.ref(),
			views.html.admin.payment.payment_requests.ref());
	
	public static Result displayForm(String accessKey) {
		Optional<PaymentRequest> request  = PaymentRequest.getFromAccessKey(accessKey);
		if (request.isPresent())
			return ok(views.html.payment.display_form.render(request.get(), SingleImmediatePF.of(request.get())));
		return badRequest(views.html.payment.error.render());
    }
	
	
	public static Result error() {
		return ok(views.html.payment.error.render());
    }
	
	public static Result success() {
		return ok(views.html.payment.success.render());
    }
	
	public static Result manageBackOfficeAnswer() {
		DynamicForm requestData = Form.form().bindFromRequest();
		
		for (String key : requestData.data().keySet()) {
			Logger.error(key+" -> "+requestData.get(key));
		}
		
		PaymentConfirmation confirmation = PaymentConfirmation.of(requestData.data());
		
		String receivedSignature = requestData.get(DataDictionnary.SIGNATURE);
		String calculatedSignature = confirmation.getSignature();
		
		Logger.debug("receivedSignature : "+receivedSignature+", calculatedSignature : "+calculatedSignature);
		
		if (receivedSignature.equals(calculatedSignature)) {
			return ok("OK");	
		} else {
			return badRequest("Signature doesn't match");
		}
    }
}
