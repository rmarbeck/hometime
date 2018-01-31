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

@Security.Authenticated(SecuredAdminOrCollaboratorOnly.class)
public class PaymentRequests extends Controller {
	public static Crud<PaymentRequest, PaymentRequest> crud = Crud.of(
			PaymentRequest.of(),
			views.html.admin.payment.payment_request.ref(),
			views.html.admin.payment.payment_request_form.ref(),
			views.html.admin.payment.payment_requests.ref(),
			Form.form(PaymentRequest.class).fill(PaymentRequest.getDefaultValues()));
	
	public static Result createFromOrder(long id) {
		PaymentRequest instance = PaymentRequest.getDefaultValues();
		OrderDocument document = OrderDocument.findById(id);
		
		if (document != null) {
			instance.orderNumber = document.getUniqueAccountingNumber();
			instance.customer = document.document.customer;
			instance.description = document.description;
			instance.priceInEuros = Math.round(document.document.getBottomLinePriceIncludingVAT());
		}
		return crud.create(Form.form(PaymentRequest.class).fill(instance));
    }
	
	public static Result createFromInvoice(long id) {
		PaymentRequest instance = PaymentRequest.getDefaultValues();
		Invoice document = Invoice.findById(id);
		
		if (document != null) {
			instance.orderNumber = document.retrieveUniqueAccountingNumber();
			instance.customer = document.document.customer;
			instance.description = document.description;
			instance.priceInEuros = Math.round(document.document.getBottomLinePriceIncludingVAT());
		}
		return crud.create(Form.form(PaymentRequest.class).fill(instance));
    }
	
	@Security.Authenticated(SecuredNoCheck.class)
	public static Result displayForm(String accessKey) {
		Optional<PaymentRequest> request  = PaymentRequest.getValidRequestFromAccessKey(accessKey);
		if (request.isPresent())
			return ok(views.html.payment.display_form.render(request.get(), SingleImmediatePF.of(request.get())));
		return badRequest(views.html.payment.error.render());
    }
	
	@Security.Authenticated(SecuredNoCheck.class)
	public static Result error() {
		return ok(views.html.payment.error.render());
    }
	
	@Security.Authenticated(SecuredNoCheck.class)
	public static Result errorPost() {
		return ok(views.html.payment.error.render());
    }
	
	@Security.Authenticated(SecuredNoCheck.class)
	public static Result success() {
		return ok(views.html.payment.success.render());
    }
	
	@Security.Authenticated(SecuredNoCheck.class)
	public static Result manageBackOfficeAnswer() {
		DynamicForm requestData = Form.form().bindFromRequest();
		
		PaymentConfirmation confirmation = PaymentConfirmation.of(requestData.data());
		
		String receivedSignature = requestData.get(DataDictionnary.SIGNATURE);
		String calculatedSignature = confirmation.getSignature();
		
		if (receivedSignature.equals(calculatedSignature)) {
			Optional<PaymentRequest> foundRequest = PaymentRequest.getLastFromOrderId(confirmation.getOrderId());
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
