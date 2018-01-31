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
}
