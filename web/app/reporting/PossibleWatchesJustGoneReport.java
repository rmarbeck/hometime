package reporting;

import static fr.hometime.utils.ListHelper.parallelStreamFromNullableList;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import models.Customer;
import models.CustomerWatch;
import models.Invoice;

public class PossibleWatchesJustGoneReport implements MesurableReport {
	public Date date;
	public Invoice invoice;
	public String invoiceName;
	public String invoiceDescription;
	public Customer customer;
	public List<CustomerWatch> watches;
	
	public static List<PossibleWatchesJustGoneReport> generateReport(Predicate<Invoice> invoiceFilterIn) {
		return parallelStreamFromNullableList(Invoice.findAllByDescendingDate()).filter(invoiceFilterIn).map(PossibleWatchesJustGoneReport::new).collect(Collectors.toList());
	}
	
	private PossibleWatchesJustGoneReport(Invoice invoice) {
		this.invoice = invoice;
		this.date = invoice.document.creationDate;
		this.invoiceName = invoice.uniqueAccountingNumber;
		this.invoiceDescription = invoice.description;
		this.customer = invoice.document.customer;
		this.watches = CustomerWatch.findByCustomerStoredByUs(this.customer);
	}
	
	public static Predicate<Invoice> atLeastAWatchIsStillMarkedBeingHere() {
		return invoice -> isNotEmpty(CustomerWatch.findByCustomerStoredByUs(invoice.document.customer));
	}
	
	private static boolean isNotEmpty(List<CustomerWatch> list) {
		return list != null && !list.isEmpty();
	}
	
	public String listWatches() {
		return this.watches.stream().map(CustomerWatch::getId).map((value) -> "#"+value.toString()).collect(Collectors.joining(", "));
	}
	
	public String getCustomerName() {
		return this.customer.getDisplayName();
	}
}
