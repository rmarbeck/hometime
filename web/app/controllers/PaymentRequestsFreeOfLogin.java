package controllers;

import java.util.Optional;

import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.hometime.payment.systempay.DataDictionnary;
import fr.hometime.payment.systempay.PaymentConfirmation;
import fr.hometime.payment.systempay.SingleImmediatePF;
import fr.hometime.utils.ActionHelper;
import fr.hometime.utils.PaymentRequestHelper;
import models.PaymentRequest;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class PaymentRequestsFreeOfLogin extends Controller {
	public static Result displayForm(String accessKey) {
		Optional<PaymentRequest> request  = PaymentRequestHelper.getValidRequestFromAccessKey(accessKey);
		if (request.isPresent())
			return ok(views.html.payment.display_form.render(request.get(), SingleImmediatePF.of(request.get())));
		return badRequest(views.html.payment.error.render());
    }
	
	public static Result displayFormFromFriendlyLocation(String accessKey) {
		if (Webservices.isAuthorized()) {
			Optional<PaymentRequest> request  = PaymentRequestHelper.getValidRequestFromAccessKey(accessKey);
			if (request.isPresent()) {
				ObjectNode resultAsJson = Json.newObject();
    			ObjectNode paymentRequestAsJson = Json.newObject();
    			paymentRequestAsJson.put("priceInEuros", request.get().priceInEuros);
    			paymentRequestAsJson.put("orderNumber", request.get().orderNumber);
    			paymentRequestAsJson.put("description", request.get().description);
    			resultAsJson.put("PaymentRequest", paymentRequestAsJson);
    			resultAsJson.put("PaymentForm", Json.toJson(SingleImmediatePF.of(request.get())));
    			return ok(resultAsJson);
			}
			return badRequest();
		} else {
			return unauthorized();
		}
	}
	
	public static Result error() {
		return ok(views.html.payment.error.render());
    }
	
	public static Result errorPost() {
		return ok(views.html.payment.error.render());
    }
	
	public static Result success() {
		return ok(views.html.payment.success.render());
    }
	
	public static Result manageBackOfficeAnswer() {
		DynamicForm requestData = Form.form().bindFromRequest();
		
		PaymentConfirmation confirmation = PaymentConfirmation.of(requestData.data());
		
		String receivedSignature = requestData.get(DataDictionnary.SIGNATURE);
		String calculatedSignature = confirmation.getSignature();
		
		if (receivedSignature.equals(calculatedSignature)) {
			Optional<PaymentRequest> foundRequest = PaymentRequestHelper.getBestFitFromOrderId(confirmation.getOrderId(), confirmation.getEffectiveAmount());
			if (foundRequest.isPresent()) {
				foundRequest.get().updateAfterConfirmationResult(confirmation);
				sendEmailNotification(foundRequest.get());
				return ok("OK");
			}
			Logger.error("Unknown payment request - "+confirmation.getOrderId());
			return badRequest("Unknown payment request");
		} else {
			Logger.error("Signature doesn't match");
			return badRequest("Signature doesn't match");
		}
    }
	
	private static void sendEmailNotification(PaymentRequest request) {
		String emailTitle;
		switch (request.requestStatus) {
		case VALIDATED_NO_WARNING:
			emailTitle = "Payment successfull";
			break;
		case VALIDATED_WITH_WARNING:
			emailTitle = "Payment WITH WARNING successfull";
			break;
		default:
			emailTitle = "ERROR in Payment";
			break;
		}
			
		ActionHelper.tryToSendHtmlEmail(emailTitle, views.html.admin.payment.payment_request_email.render(request).body().toString());	
	}
}
