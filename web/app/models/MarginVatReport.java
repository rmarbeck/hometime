package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import models.Invoice.InvoiceType;

public class MarginVatReport {
	public Date date;
	public String invoiceName;
	public Float sellingPrice;
	public String purchaseInvoiceName = "";
	public Float purchasingPrice;
	
	private MarginVatReport(Invoice invoice) {
		this.date = invoice.document.creationDate;
		this.invoiceName = invoice.uniqueAccountingNumber;
		Optional<WatchToSell> watchFound = guessWatchSold(invoice);
		if (watchFound.isPresent()) {
			this.sellingPrice = Float.valueOf(watchFound.get().sellingPrice);
			this.purchasingPrice = Float.valueOf(watchFound.get().purchasingPrice);
			if (watchFound.get().purchaseInvoiceAvailable)
				this.purchaseInvoiceName = watchFound.get().purchaseInvoice.name;
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
	
	public static List<MarginVatReport> generateReport() {
		List<MarginVatReport> report = new ArrayList<MarginVatReport>();
		List<Invoice> invoices = Invoice.findAll();
		if (listNotEmpty(invoices))
			for(Invoice invoice : invoices)
				if (InvoiceType.MARGIN_VAT.equals(invoice.type))
					report.add(new MarginVatReport(invoice));
		return report;
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
			if (line.unitPrice!= null && line.unitPrice == watch.sellingPrice)
				return true;
		return false;
	}

}
