package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fr.hometime.utils.AccountingDocumentHelper;
import models.AccountingLine.LineType;

public class InvoiceLineReport {
	protected final static String INVOICE_KEY = "admin.report.invoice.type.invoice";
	protected final static String SELLING_DOCUMENT_KEY = "admin.report.invoice.type.selling.document";
	
	public Date date;
	public String invoiceType;
	public String invoiceName;
	public LineType lineType;
	public Float lineAmount;
	public Float turnOverAmount;
	
	private InvoiceLineReport(AccountingLine accountingLine) {
		AccountingDocument currentDocument = accountingLine.document;
		this.date = currentDocument.creationDate;
		this.invoiceType = guessInvoiceType(currentDocument).orElse("");
		this.invoiceName = guessUAN(currentDocument).orElse("");
		lineType = accountingLine.type;
		lineAmount = 0f;
		if (accountingLine.unitPrice != null && accountingLine.unit != null)
			lineAmount = accountingLine.unitPrice * accountingLine.unit;
		
		turnOverAmount = 0f;
		switch (invoiceType) {
			case INVOICE_KEY:
				if (lineAmount>0)
				turnOverAmount = lineAmount;
				break;
			case SELLING_DOCUMENT_KEY:
				if (lineAmount<0)
				turnOverAmount = -lineAmount;
				break;
			default :
				turnOverAmount = 0f;					
		}
	}
	
	protected static Optional<String> guessInvoiceType(AccountingDocument document) {
		if (Invoice.findByAccountingDocument(document) != null)
			return Optional.of(INVOICE_KEY);
		if (SellingDocument.findByAccountingDocument(document) != null)
			return Optional.of(SELLING_DOCUMENT_KEY);
		
		return Optional.empty();
	}
	
	protected static Optional<String> guessUAN(AccountingDocument document) {
		Invoice foundInvoice = Invoice.findByAccountingDocument(document);
		if (foundInvoice != null)
			return Optional.of(foundInvoice.uniqueAccountingNumber);
		
		SellingDocument foundSellingDocument = SellingDocument.findByAccountingDocument(document);
		if (foundSellingDocument != null)
			return Optional.of(foundSellingDocument.uniqueAccountingNumber);
		
		return Optional.empty();
	}
	
	public static List<InvoiceLineReport> generateReport() {
		List<InvoiceLineReport> report = new ArrayList<InvoiceLineReport>();
		List<AccountingLine> lines = AccountingLine.findAllWithoutCustomerInfosByDescendingDate();
		for(AccountingLine line : lines)
			if (lineIsInvoice(line))
				if (lineIsNotEmpty(line))
					report.add(new InvoiceLineReport(line));
		return report;
	}
	
	public static List<InvoiceLineReport> generateReportEnhanced(Long nbOfMonths) {
		List<InvoiceLineReport> report = new ArrayList<InvoiceLineReport>();
		List<AccountingLine> lines = AccountingDocumentHelper.retrieveInvoiceLines(Math.abs(nbOfMonths.intValue())).get();
		for(AccountingLine line : lines)
			if (lineIsInvoice(line))
				if (lineIsNotEmpty(line))
					report.add(new InvoiceLineReport(line));
		return report;
	}
	
	public static List<InvoiceLineReport> generateReportEnhanced2() {
		List<InvoiceLineReport> report = new ArrayList<InvoiceLineReport>();
		List<Invoice> invoices = Invoice.findAllByDescendingDate();
		for(Invoice invoice : invoices) {
			List<AccountingLine> lines = AccountingLine.findByAccountingDocumentId(invoice.id);
			for(AccountingLine line : lines)
					if (lineIsNotEmpty(line))
						report.add(new InvoiceLineReport(line));
		}
		return report;
	}
	
	protected static boolean lineIsNotEmpty(AccountingLine line) {
		return ! lineIsEmpty(line);
	}
	
	protected static boolean lineIsEmpty(AccountingLine line) {
		return (line.unit == null || line.unit == 0f || line.unitPrice == null || line.unitPrice == 0f);
	}
	
	protected static boolean lineIsInvoice(AccountingLine line) {
		switch (guessInvoiceType(line.document).orElse("")) {
			case INVOICE_KEY:
				return true;
			case SELLING_DOCUMENT_KEY:
				return true;
			default :
				return false;					
		}
	}
	
}
