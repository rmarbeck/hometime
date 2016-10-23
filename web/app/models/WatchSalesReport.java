package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fr.hometime.utils.VATHelper;

public class WatchSalesReport {
	public Date date;
	public String invoiceName;
	public Float sellingPrice;
	public String purchaseInvoiceName = "";
	public Float purchasingPrice;
	public Predicate<Invoice> invoiceFilterIn;
	
	
	public static List<WatchSalesReport> generateReport(Predicate<Invoice> invoiceFilterIn) {
		/*List<WatchSalesReport> report = new ArrayList<WatchSalesReport>();
		List<Invoice> invoices = Invoice.findAllByDescendingDate();
		if (listNotEmpty(invoices))
			for(Invoice invoice : invoices)
				if (invoiceFilterIn.test(invoice))
					report.add(new WatchSalesReport(invoice));
		List<WatchSalesReport> report = invoices.stream().filter(invoiceFilterIn).map(WatchSalesReport::new).collect(Collectors.toList());
		return report;*/
		return Invoice.tryTofindAllByDescendingDate().orElse(new ArrayList<Invoice>()).stream().filter(invoiceFilterIn).map(WatchSalesReport::new).collect(Collectors.toList());
	}
	
	private WatchSalesReport(Invoice invoice) {
		this.date = invoice.document.creationDate;
		this.invoiceName = invoice.uniqueAccountingNumber;
		Optional<WatchToSell> watchFound = guessWatchSold(invoice);
		if (watchFound.isPresent()) {
			this.sellingPrice = Float.valueOf(watchFound.get().sellingPrice);
			this.purchasingPrice = Float.valueOf(watchFound.get().purchasingPrice);
			if (watchFound.get().purchaseInvoiceAvailable)
				if (watchFound.get().purchaseInvoice != null) {
					this.purchaseInvoiceName = watchFound.get().purchaseInvoice.name;	
				} else {
					this.purchaseInvoiceName = "unfound";
				}
		}
	}
	
	private Optional<WatchToSell> guessWatchSold(Invoice invoice) {
		List<WatchToSell> watches =  WatchToSell.findByCustomer(invoice.document.customer);
		if (listNotEmpty(watches))
			for (WatchToSell watch : watches)
				if (doesWatchMatches(watch, invoice))
					return Optional.of(watch);
		return Optional.empty();
	}
	
	private static boolean listNotEmpty(List<?> list) {
		return (list != null && list.size() != 0);
	}
	
	private static boolean doesWatchMatches(WatchToSell watch, Invoice invoice) {
		return doesReferenceMatches(watch, invoice) && doesPriceMatches(watch, invoice);
	}
	
	private static boolean doesReferenceMatches(WatchToSell watch, Invoice invoice) {
		if (watch.reference != null && watch.reference != "")
			for (AccountingLine line : invoice.document.lines)
				if (line.description.contains(watch.reference))
					return true;
		return false;
	}
	
	private static boolean doesPriceMatches(WatchToSell watch, Invoice invoice) {
		for (AccountingLine line : invoice.document.lines)
			if (line.unitPrice!= null && priceSeemsToBeTheSame(line.unitPrice, watch.sellingPrice))
				return true;
		return false;
	}
	
	private static boolean priceSeemsToBeTheSame(Float unitPrice, long sellingPrice) {
		if (unitPrice == sellingPrice || VATHelper.getPriceAfterVAT(unitPrice) == sellingPrice)
			return true;
		return false;
	}

}
