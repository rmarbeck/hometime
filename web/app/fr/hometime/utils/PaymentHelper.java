package fr.hometime.utils;

import java.util.List;
import java.util.Optional;

import models.Invoice;
import models.Payment;

public class PaymentHelper {
    public static Optional<List<Payment>> findByInvoiceId(long invoiceId) {
    	Invoice invoiceFound = Invoice.findById(invoiceId);
    	List<Payment> paymentsFound = null;
    	if (invoiceFound != null)
    		paymentsFound = Payment.find.where().eq("invoice.id", invoiceId).findList();
    	
    	if (paymentsFound != null && !paymentsFound.isEmpty())
    		return Optional.of(paymentsFound);
    	return Optional.empty();
    }

}
