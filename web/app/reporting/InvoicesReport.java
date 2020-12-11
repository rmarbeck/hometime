package reporting;

import static fr.hometime.utils.ListHelper.streamFromNullableList;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import controllers.Payments;
import models.Invoice;

public class InvoicesReport {
	public Date date;
	public Invoice invoice;
	public String invoiceName;
	public String invoiceDescription;
	public Float invoiceAmount;
	public Float remainingAmountToPay;
	public String invoicePaymentMethod;
	
	public static List<InvoicesReport> generateReport(Predicate<Invoice> invoiceFilterIn) {
		return streamFromNullableList(Invoice.findAllByDescendingDate()).filter(invoiceFilterIn).map(InvoicesReport::new).collect(Collectors.toList());
	}
	
	private InvoicesReport(Invoice invoice) {
		this.invoice = invoice;
		this.date = invoice.document.creationDate;
		this.invoiceName = invoice.uniqueAccountingNumber;
		this.invoiceDescription = invoice.description;
		this.invoiceAmount = Float.valueOf(Math.round(invoice.document.getBottomLinePriceIncludingVAT()));
		this.remainingAmountToPay = Payments.remainingAmountToPay(invoice);
		this.invoicePaymentMethod = invoice.paymentMethodUsed;
	}
	
	public static Predicate<Invoice> isAlreadyPayed() {
		return invoice -> Payments.remainingAmountToPay(invoice) > 0.01f;
	}

}
