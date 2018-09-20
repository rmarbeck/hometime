package controllers;

import models.Invoice;
import models.OrderDocument;
import models.PaymentRequest;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
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
