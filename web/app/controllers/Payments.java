package controllers;

import java.util.List;
import java.util.Optional;

import models.Invoice;
import models.Payment;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import fr.hometime.utils.ActionHelper;

public class Payments extends Controller {
	public static Crud<Payment, Payment> crud = Crud.of(
			Payment.of(),
			views.html.admin.payment.payment.ref(),
			views.html.admin.payment.payment_form.ref(),
			views.html.admin.payment.payments.ref(),
			Form.form(Payment.class));
	
	public static Result createFromInvoice(long id) {
		Payment instance = Payment.of();
		Invoice document = Invoice.findById(id);
		
		if (document != null) {
			instance.invoice = document;
			instance.amountInEuros = document.document.getBottomLinePrice();
		}
		return crud.create(Form.form(Payment.class).fill(instance));
    }
	
	private static float remainingAmountToPay(Invoice currentInvoice) {
		float invoiceAmount = currentInvoice.document.getBottomLinePrice();
		float alreadyPayed = existingPaymentsAmount(currentInvoice);
		return invoiceAmount - alreadyPayed;
	}
	
	private static float existingPaymentsAmount(Invoice currentInvoice) {
		Optional<List<Payment>> payments = Payment.findByInvoiceId(currentInvoice.id);
		if (payments.isPresent())
			return (float) payments.get().stream().mapToDouble(p -> p.amountInEuros).sum();
		return 0f;
	}
}
