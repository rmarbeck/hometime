package controllers;

import java.util.Optional;

import models.Invoice;
import models.OrderDocument;
import models.PaymentRequest;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mails.notify_order;
import fr.hometime.payment.systempay.DataDictionnary;
import fr.hometime.payment.systempay.PaymentConfirmation;
import fr.hometime.payment.systempay.SingleImmediatePF;
import fr.hometime.utils.ActionHelper;
import fr.hometime.utils.PaymentRequestHelper;

public class PaymentRequestsFreeOfLogin extends Controller {
	public static Result displayForm(String accessKey) {
		Optional<PaymentRequest> request  = PaymentRequestHelper.getValidRequestFromAccessKey(accessKey);
		if (request.isPresent())
			return ok(views.html.payment.display_form.render(request.get(), SingleImmediatePF.of(request.get())));
		return badRequest(views.html.payment.error.render());
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
			Optional<PaymentRequest> foundRequest = PaymentRequestHelper.getLastFromOrderId(confirmation.getOrderId());
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
