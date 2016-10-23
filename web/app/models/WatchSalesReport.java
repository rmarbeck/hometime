package models;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fr.hometime.utils.ListHelper;
import fr.hometime.utils.VATHelper;

public class WatchSalesReport {
	public Date date;
	public String invoiceName;
	public Float sellingPrice;
	public String purchaseInvoiceName = "";
	public Float purchasingPrice;
	public Predicate<Invoice> invoiceFilterIn;
	
	
	public static List<WatchSalesReport> generateReport(Predicate<Invoice> invoiceFilterIn) {
		return new ListHelper<Invoice>(Invoice.findAllByDescendingDate()).streamFromNullableList().filter(invoiceFilterIn).map(WatchSalesReport::new).collect(Collectors.toList());
	}
	
	private WatchSalesReport(Invoice invoice) {
		this.date = invoice.document.creationDate;
		this.invoiceName = invoice.uniqueAccountingNumber;
		guessWatchSold(invoice).ifPresent(watchFound -> {
			this.sellingPrice = Float.valueOf(watchFound.sellingPrice);
			this.purchasingPrice = Float.valueOf(watchFound.purchasingPrice);
			if (watchFound.purchaseInvoiceAvailable)
				if (watchFound.purchaseInvoice != null) {
					this.purchaseInvoiceName = watchFound.purchaseInvoice.name;	
				} else {
					this.purchaseInvoiceName = "unfound";
				}
		});
	}
	
	private Optional<WatchToSell> guessWatchSold(Invoice invoice) {
		return new ListHelper<WatchToSell>(WatchToSell.findByCustomer(invoice.document.customer)).streamFromNullableList().filter(watch -> doesWatchMatches(watch, invoice)).findAny();
	}
	
	private static boolean doesWatchMatches(WatchToSell watch, Invoice invoice) {
		return doesReferenceMatches(watch, invoice) && doesPriceMatches(watch, invoice);
	}
	
	private static boolean doesReferenceMatches(WatchToSell watch, Invoice invoice) {
		if (watch.reference != null && watch.reference != "")
			return new ListHelper<AccountingLine>(invoice.document.lines).streamFromNullableList().anyMatch(line -> line.description.contains(watch.reference));
		return false;
	}
	
	private static boolean doesPriceMatches(WatchToSell watch, Invoice invoice) {
		return new ListHelper<AccountingLine>(invoice.document.lines).streamFromNullableList().anyMatch(line -> line.unitPrice!= null && line.unitPrice != 0 && priceSeemsToBeTheSame(line.unitPrice, watch.sellingPrice));
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
