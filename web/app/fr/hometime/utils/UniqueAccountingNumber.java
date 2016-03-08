package fr.hometime.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import models.Invoice;
import models.OrderDocument;
import models.SellingDocument;
import play.Logger;
import play.db.ebean.Model;

public class UniqueAccountingNumber {
	private final static String SEPARATOR = "-";
	private Integer prefix;
	private Integer serial;
	
	public static UniqueAccountingNumber getNextForInvoices() {
		return getNext(getLastForInvoices());
	}
	
	public static UniqueAccountingNumber getNextForOrders() {
		return getNext(getLastForOrders());
	}
	
	private UniqueAccountingNumber() {
		this.prefix = getCurrentPrefix();
		this.serial = 0;
	}

	private UniqueAccountingNumber(Integer prefix, Integer serial) {
		this.prefix = prefix;
		this.serial = serial;
	}
	
	private static UniqueAccountingNumber fromString(String uan) {
		if (isValid(uan))
			return new UniqueAccountingNumber(Integer.parseInt(uan.substring(0, 6)), Integer.parseInt(uan.substring(7)));
		
		return new UniqueAccountingNumber();
	}

	private static UniqueAccountingNumber getNext(UniqueAccountingNumber previous) {
		UniqueAccountingNumber brandNewOne = createNew();
		if (brandNewOne.isOlderThan(previous))
			return brandNewOne.plusOne();
		return previous.plusOne();
	}
	
	private UniqueAccountingNumber plusOne() {
		return new UniqueAccountingNumber(this.prefix, this.serial+1);
	}
	
	private static UniqueAccountingNumber getLastForInvoices() {
		Invoice fromInvoice = (Invoice) getLastUAN(Invoice.find);
		SellingDocument fromSellingDocument = (SellingDocument) getLastUAN(SellingDocument.find);
		
		if (fromInvoice == null && fromSellingDocument == null)
			return createNew();
		if (fromInvoice == null)
			return fromString(fromSellingDocument.getUniqueAccountingNumber());
		if (fromSellingDocument == null)
			return fromString(fromInvoice.retrieveUniqueAccountingNumber());

		return selectOlder(fromInvoice.retrieveUniqueAccountingNumber(), fromSellingDocument.getUniqueAccountingNumber());
	}
	
	private static UniqueAccountingNumber getLastForOrders() {
		OrderDocument fromOrder = (OrderDocument) getLastUAN(OrderDocument.find);
		
		if (fromOrder == null)
			return createNew();
		return fromString(fromOrder.getUniqueAccountingNumber());
	}
	
	private static UniqueAccountingNumber selectOlder(String uanString1, String uanString2) {
		UniqueAccountingNumber uan1 = fromString(uanString1);
		UniqueAccountingNumber uan2 = fromString(uanString2);
		if(uan1.isOlderThan(uan2))
			return uan1;
		return uan2;
	}
	
	private static UniqueAccountingNumber createNew() {
		return fromString(null);
	}
	
	private static Integer getCurrentPrefix() {
		LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
		Integer prefixBuilt = ldt.getYear()*100 + ldt.getMonthValue();
		return prefixBuilt;
	}
	
	private static Object getLastUAN(Model.Finder<String, ?> find) {
		List<?> results = find.orderBy("unique_accounting_number DESC").findList();
		if(results != null && results.size() != 0)
			return results.get(0);
		return null;
	}
	
	private static boolean isValid(String uan) {
		if (uan != null	&& uan.matches("^[0-9]{6}\\-[0-9]{2,3}$"))
			return true;
		return false;
	}
	
	public String toString() {
		return prefix + SEPARATOR + serialAsString();
	}
	
	private String serialAsString() {
		if (serial<10)
			return "0"+serial;
		return ""+serial;
	}
	
	public boolean equals(UniqueAccountingNumber toCompare) {
		return (this.prefix == toCompare.prefix && this.serial == toCompare.serial);
	}
	
	public boolean isOlderThan(UniqueAccountingNumber toCompare) {
		if (this.prefix.intValue() > toCompare.prefix.intValue())
			return true;
		if (this.prefix.intValue() == toCompare.prefix.intValue() && this.serial.intValue() > toCompare.serial.intValue())
			return true;
		return false;
	}
}
