package fr.hometime.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import models.Invoice;
import models.OrderDocument;
import models.SellingDocument;
import play.db.ebean.Model;

public class UniqueAccountingNumber {
	private final static String SEPARATOR = "-";
	private final static Integer ordersYearShifting = 6000;
	private Integer prefix;
	private Integer serial;
	
	public static UniqueAccountingNumber getNextForInvoices() {
		return getNext(getLastForInvoices(), false);
	}
	
	public static UniqueAccountingNumber getNextForOrders() {
		return getNext(getLastForOrders(), true);
	}
	
	private UniqueAccountingNumber() {
		this(false);
	}
	
	private UniqueAccountingNumber(boolean isItForAnOrder) {
		this.prefix = getCurrentPrefix(isItForAnOrder);
		this.serial = 0;
	}

	private UniqueAccountingNumber(Integer prefix, Integer serial) {
		this.prefix = prefix;
		this.serial = serial;
	}
	
	private static UniqueAccountingNumber fromString(String uan, boolean isItForAnOrder) {
		if (isValid(uan))
			return new UniqueAccountingNumber(Integer.parseInt(uan.substring(0, 6)), Integer.parseInt(uan.substring(7)));
		
		return new UniqueAccountingNumber(isItForAnOrder);
	}

	private static UniqueAccountingNumber getNext(UniqueAccountingNumber previous, boolean isItForAnOrder) {
		UniqueAccountingNumber brandNewOne = createNew(isItForAnOrder);
		if (brandNewOne.isOlderThan(previous))
			return brandNewOne.skipToNextSerial(previous);
		return previous.plusOne();
	}
	
	private UniqueAccountingNumber plusOne() {
		return new UniqueAccountingNumber(this.prefix, this.serial+1);
	}
	
	private UniqueAccountingNumber skipToNextSerial(UniqueAccountingNumber previous) {
		return new UniqueAccountingNumber(this.prefix, previous.serial+1);
	}
	
	public static UniqueAccountingNumber getLastForInvoices() {
		Invoice fromInvoice = (Invoice) getLastUAN(Invoice.find);
		SellingDocument fromSellingDocument = (SellingDocument) getLastUAN(SellingDocument.find);
		
		if (fromInvoice == null && fromSellingDocument == null)
			return createNew(false);
		if (fromInvoice == null)
			return fromString(fromSellingDocument.getUniqueAccountingNumber(), false);
		if (fromSellingDocument == null)
			return fromString(fromInvoice.retrieveUniqueAccountingNumber(), false);

		return selectOlder(fromInvoice.retrieveUniqueAccountingNumber(), fromSellingDocument.getUniqueAccountingNumber());
	}
	
	public static UniqueAccountingNumber getLastForOrders() {
		OrderDocument fromOrder = (OrderDocument) getLastUAN(OrderDocument.find);
		
		if (fromOrder == null)
			return createNew(true);
		return fromString(fromOrder.getUniqueAccountingNumber(), true);
	}
	
	private static UniqueAccountingNumber selectOlder(String uanString1, String uanString2) {
		UniqueAccountingNumber uan1 = fromString(uanString1, false);
		UniqueAccountingNumber uan2 = fromString(uanString2, false);
		if(uan1.isOlderThan(uan2))
			return uan1;
		return uan2;
	}
	
	private static UniqueAccountingNumber createNew(boolean isItForAnOrder) {
		return fromString(null, isItForAnOrder);
	}
	
	private static Integer getCurrentPrefix(boolean isItForAnOrder) {
		LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
		Integer shifting = 0;
		if (isItForAnOrder)
			shifting = ordersYearShifting;
		Integer prefixBuilt = (ldt.getYear()+shifting)*100 + ldt.getMonthValue();
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
