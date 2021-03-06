package controllers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fr.hometime.utils.DateHelper;
import fr.hometime.utils.PaymentHelper;
import models.Invoice;
import models.Payment;
import models.Payment.PaymentMethod;
import models.PaymentRequest;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class Payments extends Controller {
	public static Crud<Payment, Payment> crud = Crud.of(
			Payment.of(),
			views.html.admin.payment.payment.ref(),
			views.html.admin.payment.payment_form.ref(),
			views.html.admin.payment.payments.ref());
	
	public static Result createFromInvoice(long id) {
		Payment instance = new Payment();
		Invoice document = Invoice.findById(id);
		
		if (document != null) {
			instance.invoice = document;
			instance.description2 = document.paymentMethodUsed;
			instance.amountInEuros = remainingAmountToPay(document);
		}
		instance.paymentDate = new Date();
		instance.inBankDate = DateHelper.toDate(Instant.now().plus(2, ChronoUnit.DAYS));
		return crud.create(Form.form(Payment.class).fill(instance));
    }
	
	public static Result createFromPaymentRequest(long id) {
		Payment instance = new Payment();
		PaymentRequest request = PaymentRequest.findById(id);
		
		instance.paymentDate = new Date();
		if (request != null) {
			instance.invoice = guessInvoiceByPaymentRequest(request);
			instance.paymentMethod = PaymentMethod.CB;
			instance.amountInEuros = request.priceInEuros;
			instance.paymentDate = request.closingDate;
		}
		instance.inBankDate = DateHelper.toDate(Instant.now().plus(2, ChronoUnit.DAYS));
		return crud.create(Form.form(Payment.class).fill(instance));
    }
	
	public static float remainingAmountToPay(Invoice currentInvoice) {
		float invoiceAmount = Math.round(currentInvoice.document.getBottomLinePriceIncludingVAT());
		float alreadyPayed = existingPaymentsAmount(currentInvoice);
		return invoiceAmount - alreadyPayed;
	}
	
	private static float existingPaymentsAmount(Invoice currentInvoice) {
		Optional<List<Payment>> payments = Optional.empty();
		if (currentInvoice.payments != null && currentInvoice.payments.size() > 0) {
			payments = Optional.of(currentInvoice.payments);
		} else {
			payments = PaymentHelper.findByInvoiceId(currentInvoice.id);
		}
		
		if (payments.isPresent())
			return (float) payments.get().stream().mapToDouble(p -> p.amountInEuros).sum();
		return 0f;
	}
	
	private static Invoice guessInvoiceByPaymentRequest(PaymentRequest request) {
		return Invoice.findLastByCustomer(request.getCustomer());
	}
}
