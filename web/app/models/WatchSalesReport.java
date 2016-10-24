package models;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.hometime.utils.ListHelper.streamFromNullableList;
import fr.hometime.utils.VATHelper;

public class WatchSalesReport {
	public Date date;
	public String invoiceName;
	public Float sellingPrice;
	public String purchaseInvoiceName = "unknown";
	public Float purchasingPrice;
	public Predicate<Invoice> invoiceFilterIn;
	
	
	public static List<WatchSalesReport> generateReport(Predicate<Invoice> invoiceFilterIn) {
		return streamFromNullableList(Invoice.findAllByDescendingDate()).filter(invoiceFilterIn).flatMap(WatchSalesReport::guessWatchesSoldAsStream).filter(watch -> watch.sellingPrice != 0).collect(Collectors.toList());
	}
	
	private WatchSalesReport(Invoice invoice, WatchToSell watch) {
		this.date = invoice.document.creationDate;
		this.invoiceName = invoice.uniqueAccountingNumber;
		this.sellingPrice = Float.valueOf(watch.sellingPrice);
		this.purchasingPrice = Float.valueOf(watch.purchasingPrice);
		if (watch.purchaseInvoiceAvailable)
			if (watch.purchaseInvoice != null) {
				this.purchaseInvoiceName = watch.purchaseInvoice.name;	
			} else {
				this.purchaseInvoiceName = "unfound";
			}
	}
	
	private static Stream<WatchSalesReport> guessWatchesSoldAsStream(Invoice invoice) {
		return streamFromNullableList(WatchToSell.findByCustomer(invoice.document.customer)).filter(watch -> doesWatchMatches(watch, invoice)).map(watch -> new WatchSalesReport(invoice, watch)).collect(Collectors.toList()).stream();
	}
	
	private static boolean doesWatchMatches(WatchToSell watch, Invoice invoice) {
		return doesReferenceMatches(watch, invoice) && doesPriceMatches(watch, invoice);
	}
	
	private static boolean doesReferenceMatches(WatchToSell watch, Invoice invoice) {
		if (watch.reference != null && watch.reference != "")
			return streamFromNullableList(invoice.document.lines).anyMatch(line -> line.description.contains(watch.reference));
		return false;
	}
	
	private static boolean doesPriceMatches(WatchToSell watch, Invoice invoice) {
		return streamFromNullableList(invoice.document.lines).anyMatch(line -> line.unitPrice!= null && line.unitPrice != 0 && priceSeemsToBeTheSame(line.unitPrice, watch.sellingPrice));
	}
	
	private static boolean priceSeemsToBeTheSame(Float unitPrice, long sellingPrice) {
		if (unitPrice == sellingPrice || vatPriceIsAlmostTheSame(unitPrice, sellingPrice) )
			return true;
		return false;
	}
	
	private static boolean vatPriceIsAlmostTheSame(Float unitPrice, long sellingPrice) {
		float diff = VATHelper.getPriceAfterVAT(unitPrice) - (float) sellingPrice;
		if (Math.abs(diff) < 1)
			return true;
		return false;
	}

}
