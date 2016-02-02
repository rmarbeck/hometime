package controllers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.AccountingDocument;
import models.AccountingLine;
import models.AccountingLine.LineType;
import models.Customer;
import models.Invoice;
import models.Invoice.InvoiceType;
import models.OrderDocument;
import models.SellingDocument;
import models.WatchToSell;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.accounting.invoice_form;
import views.html.admin.accounting.invoices;
import views.html.admin.accounting.order_document_form;
import views.html.admin.accounting.order_documents;
import views.html.admin.accounting.selling_document_form;
import views.html.admin.accounting.selling_documents;
import fr.hometime.utils.UniqueAccountingNumber;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Accounting extends Controller {
	
	public static Result INVOICE = redirect(
			routes.Accounting.display()
			);
	
	public static Result display() {
        return displayInvoice(null);
    }
	
	/*
	 * INVOICES
	 */
	
	public static Result LIST_INVOICES = redirect(
			routes.Accounting.displayAllInvoice(0, "description", "desc", "")
			);
	
	public static Result displayAllInvoice(int page, String sortBy, String order, String filter) {
        return ok(invoices.render(models.Invoice.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result addInvoiceByWatchToSellId(Long id) {
		return ok(newInvoiceFormByWatchToSellId(id));		
	}
	
	public static Result addInvoice() {
		return ok(emptyNewInvoiceForm());		
	}
	
	public static Result editInvoice(Long invoiceId) {
		models.Invoice currentInvoice = models.Invoice.findById(invoiceId);
		if (currentInvoice != null)
			return ok(existingInvoiceForm(currentInvoice));
		return badRequest("error");		
	}
	
	public static Result viewInvoice(Long invoiceId) {
		models.Invoice currentInvoice = models.Invoice.findById(invoiceId);
		if (currentInvoice != null)
			return ok(new String(currentInvoice.document.documentData, StandardCharsets.UTF_8)).as("text/html; charset=utf-8");
		return badRequest("error");
	}
	
	private static Html invoiceForm(Form<models.Invoice> invoiceForm, boolean isItANewInvoice) {
		return invoice_form.render(invoiceForm, isItANewInvoice, models.Customer.findByFirstNameAndNameAsc());
	}
	
	private static Html emptyNewInvoiceForm() {
		models.Invoice emptyInvoice = new Invoice();
		emptyInvoice.setUniqueAccountingNumber(UniqueAccountingNumber.getNextForInvoices().toString());
		return invoiceForm(Form.form(models.Invoice.class).fill(emptyInvoice), true);
	}
	
	private static Html newInvoiceFormByWatchToSellId(long id) {
		models.WatchToSell watchToSell = WatchToSell.findById(id);
		if (watchToSell == null)
			return emptyNewInvoiceForm();
		models.Invoice newInvoice = new Invoice();
		newInvoice.setUniqueAccountingNumber(UniqueAccountingNumber.getNextForInvoices().toString());
		newInvoice.document.customer = watchToSell.customerThatBoughtTheWatch;
		newInvoice.type = InvoiceType.MARGIN_VAT;
		newInvoice.description = Messages.get("admin.invoice.description.selling.a.watch", watchToSell.brand.display_name, watchToSell.model);
		newInvoice.addLine(LineType.WITHOUT_VAT_BY_UNIT, getMainLine(watchToSell), Long.valueOf(1), Float.valueOf(watchToSell.sellingPrice));
		newInvoice.addLine(LineType.FREE_INCLUDED, Messages.get("admin.invoice.waranty.line.selling.a.watch"), Long.valueOf(1), Float.valueOf(0));
		newInvoice.addLine(LineType.FREE_INCLUDED, Messages.get("admin.invoice.delivery.line.selling.a.watch"), Long.valueOf(1), Float.valueOf(0));
		return invoiceForm(Form.form(models.Invoice.class).fill(newInvoice), true);
	}
	
	private static String getMainLine(WatchToSell watchToSell) {
		return Messages.get("admin.invoice.main.line.selling.a.watch",
							watchToSell.brand.display_name,
							watchToSell.model,
							watchToSell.additionnalModelInfos!=null && !watchToSell.additionnalModelInfos.equals("")?" "+watchToSell.additionnalModelInfos:"",
							watchToSell.isNew?Messages.get("admin.invoice.new.watch"):Messages.get("admin.invoice.used.watch"),
							watchToSell.reference,
							watchToSell.serial,
							watchToSell.isNew?"":watchToSell.year!=null && !watchToSell.year.equals("")?Messages.get("admin.invoice.of.year",watchToSell.year):"",
							watchToSell.additionnalInfos!=null && !watchToSell.additionnalInfos.equals("")?" "+watchToSell.additionnalInfos:"",
							watchToSell.hasBox?(watchToSell.hasPapers?Messages.get("admin.invoice.box.and.papers"):Messages.get("admin.invoice.box.only")):(watchToSell.hasPapers?Messages.get("admin.invoice.papers.only"):Messages.get("admin.invoice.default")));
	}
	
	private static Html existingInvoiceForm(models.Invoice invoice) {
		invoice.document.reorderLines();
		return invoiceForm(Form.form(models.Invoice.class).fill(invoice), false);
	}

	public static Result manageInvoice() {
		final Form<models.Invoice> invoiceForm = Form.form(models.Invoice.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (invoiceForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(invoiceForm(invoiceForm, true));
			return badRequest(invoiceForm(invoiceForm, false));
		} else {
			models.Invoice currentInvoice = getInvoiceFromForm(invoiceForm);
			if ("save".equals(action)) {
				currentInvoice.document.documentData = getInvoiceHtml(currentInvoice).body().getBytes(StandardCharsets.UTF_8);
				currentInvoice.save();
			} else if ("delete".equals(action)) {
				models.Invoice invoiceInDB = models.Invoice.findById(currentInvoice.id);
				invoiceInDB.delete();
			} else if ("show".equals(action)) {
				return displayInvoice(currentInvoice);
			} else {
				updateDocument(invoiceForm.get().document, Invoice.findById(currentInvoice.id).document, getInvoiceHtml(currentInvoice));
			}
		}
		return LIST_INVOICES;
	}
	
	private static void updateDocument(AccountingDocument inFormDocument, AccountingDocument toUpdateDocument, Html htmlData) {
		Long customerId = inFormDocument.customer.id;
		Date creationDate = inFormDocument.creationDate;
		List<AccountingLine> lines = inFormDocument.lines;
		
		toUpdateDocument.customer = Customer.findById(customerId);
		toUpdateDocument.creationDate = creationDate;
		toUpdateDocument.deleteLines();
		toUpdateDocument.lines = lines;
		toUpdateDocument.documentData = htmlData.body().getBytes(StandardCharsets.UTF_8);
		toUpdateDocument.update();
	}
	
	private static Result displayInvoice(models.Invoice invoice) {
		return ok(getInvoiceHtml(invoice));
	}
	
	private static Html getInvoiceHtml(models.Invoice invoice) {
		if (invoice == null)
			return views.html.admin.accounting.invoice.render(null, 0f, 0f, 0f, 0f);
		Float htAmount = computeHTTotalAmountForVatLines(invoice.document.lines);
		Float totalAmount = htAmount * 1.20f;
		Float vat = htAmount * 0.20f;
		Float amountToPay = totalAmount + computeHTTotalAmountForNonVatLines(invoice.document.lines) - invoice.alreadyPayed;
		htAmount = computeHTTotalAmountForNonVatLines(invoice.document.lines);
		return views.html.admin.accounting.invoice.render(invoice, totalAmount, amountToPay, htAmount, vat);
	}
	
	private static models.Invoice getInvoiceFromForm(final Form<models.Invoice> invoiceForm) {
		models.Invoice invoice = invoiceForm.get();
		return invoice;
	}
	
	/*
	 * ORDER DOCUMENT
	 */
	
	public static Result addOrderDocument() {
		return ok(emptyNewOrderDocumentForm());
	}
	
	public static Result editOrderDocument(Long orderDocumentId) {
		models.OrderDocument currentOrderDocument = models.OrderDocument.findById(orderDocumentId);
		if (currentOrderDocument != null)
			return ok(existingOrderDocumentForm(currentOrderDocument));
		return badRequest("error");		
	}
	
	
	private static Html orderDocumentForm(Form<models.OrderDocument> orderDocumentForm, boolean isItANewOrderDocument) {
		return order_document_form.render(orderDocumentForm, isItANewOrderDocument, models.Customer.findByFirstNameAndNameAsc());
	}
	
	private static Html emptyNewOrderDocumentForm() {
		models.OrderDocument emptyOrder = new OrderDocument();
		emptyOrder.setUniqueAccountingNumber(UniqueAccountingNumber.getNextForOrders().toString());
		return orderDocumentForm(Form.form(models.OrderDocument.class).fill(emptyOrder), true);
	}
	
	private static Html existingOrderDocumentForm(models.OrderDocument orderDocument) {
		orderDocument.document.reorderLines();
		return orderDocumentForm(Form.form(models.OrderDocument.class).fill(orderDocument), false);
	}

	public static Result manageOrderDocument() {
		final Form<models.OrderDocument> orderDocumentForm = Form.form(models.OrderDocument.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (orderDocumentForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(orderDocumentForm(orderDocumentForm, true));
			return badRequest(orderDocumentForm(orderDocumentForm, false));
		} else {
			models.OrderDocument currentOrderDocument = getOrderDocumentFromForm(orderDocumentForm);
			if ("save".equals(action)) {
				currentOrderDocument.document.documentData = getOrderDocumentHtml(currentOrderDocument).body().getBytes(StandardCharsets.UTF_8);
				currentOrderDocument.save();
			} else if ("delete".equals(action)) {
				models.OrderDocument orderDocumentInDB = models.OrderDocument.findById(currentOrderDocument.id);
				orderDocumentInDB.delete();
			} else if ("show".equals(action)) {
				return displayOrderDocument(currentOrderDocument);
			} else {
				updateDocument(orderDocumentForm.get().document, OrderDocument.findById(currentOrderDocument.id).document, getOrderDocumentHtml(currentOrderDocument));
			}
		}
		return LIST_ORDER_DOCUMENTS;
	}
	
	private static Result displayOrderDocument(models.OrderDocument orderDocument) {
		return ok(getOrderDocumentHtml(orderDocument));
	}
	
	private static Html getOrderDocumentHtml(models.OrderDocument orderDocument) {
		if (orderDocument == null)
			return views.html.admin.accounting.order_document.render(null, 0f, 0f, 0f, 0f);
		Float htAmount = computeHTTotalAmountForVatLines(orderDocument.document.lines);
		Float totalAmount = htAmount * 1.20f;
		Float vat = htAmount * 0.20f;
		Float amountToPay = totalAmount + computeHTTotalAmountForNonVatLines(orderDocument.document.lines);

		return views.html.admin.accounting.order_document.render(orderDocument, totalAmount, amountToPay, htAmount, vat);
	}
	
	private static models.OrderDocument getOrderDocumentFromForm(final Form<models.OrderDocument> orderDocumentForm) {
		models.OrderDocument orderDocument = orderDocumentForm.get();
		return orderDocument;
	}
	
	public static Result LIST_ORDER_DOCUMENTS = redirect(
			routes.Accounting.displayAllOrderDocument(0, "uniqueAccountingNumber", "desc", "")
			);
	
	public static Result displayAllOrderDocument(int page, String sortBy, String order, String filter) {
        return ok(order_documents.render(models.OrderDocument.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result viewOrderDocument(Long orderDocumentId) {
		models.OrderDocument currentOrderDocument = models.OrderDocument.findById(orderDocumentId);
		if (currentOrderDocument != null)
			return ok(new String(currentOrderDocument.document.documentData, StandardCharsets.UTF_8)).as("text/html; charset=utf-8");
		return badRequest("error");
	}
	
	/*
	 * SELLING DOCUMENT
	 */
	
	public static Result LIST_SELLING_DOCUMENTS = redirect(
			routes.Accounting.displayAllSellingDocument(0, "description", "desc", "")
			);
	
	public static Result displayAllSellingDocument(int page, String sortBy, String order, String filter) {
        return ok(selling_documents.render(models.SellingDocument.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result viewSellingDocument(Long documentId) {
		models.SellingDocument currentDocument = models.SellingDocument.findById(documentId);
		if (currentDocument != null)
			return ok(new String(currentDocument.document.documentData, StandardCharsets.UTF_8)).as("text/html; charset=utf-8");
		return badRequest("error");
	}

	public static Result addSellingDocument() {
		return ok(emptyNewsellingDocumentForm());		
	}
	
	public static Result editSellingDocument(Long documentId) {
		models.SellingDocument currentDocument = models.SellingDocument.findById(documentId);
		if (currentDocument != null)
			return ok(existingSellingDocumentForm(currentDocument));
		return badRequest("error");		
	}
	
	private static Html sellingDocumentForm(Form<models.SellingDocument> documentForm, boolean isItANewDocument) {
		return selling_document_form.render(documentForm, isItANewDocument, models.Customer.findByFirstNameAndNameAsc());
	}
	
	private static Html emptyNewsellingDocumentForm() {
		models.SellingDocument emptySellingDocument = new SellingDocument();
		emptySellingDocument.setUniqueAccountingNumber(UniqueAccountingNumber.getNextForInvoices().toString());
		return sellingDocumentForm(Form.form(models.SellingDocument.class).fill(emptySellingDocument), true);
	}
	
	private static Html existingSellingDocumentForm(models.SellingDocument sellingDocument) {
		sellingDocument.document.reorderLines();
		return sellingDocumentForm(Form.form(models.SellingDocument.class).fill(sellingDocument), false);
	}
	
	public static Result manageSellingDocument() {
		final Form<models.SellingDocument> documentForm = Form.form(models.SellingDocument.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (documentForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(sellingDocumentForm(documentForm, true));
			return badRequest(sellingDocumentForm(documentForm, false));
		} else {
			models.SellingDocument currentDocument = getSellingDocumentFromForm(documentForm);
			if ("save".equals(action)) {
				currentDocument.document.documentData = getSellingDocumentHtml(currentDocument).body().getBytes(StandardCharsets.UTF_8);
				currentDocument.save();
			} else if ("delete".equals(action)) {
				models.SellingDocument documentInDB = models.SellingDocument.findById(currentDocument.id);
				documentInDB.delete();
			} else if ("show".equals(action)) {
				return displaySellingDocument(currentDocument);
			} else {
				updateDocument(documentForm.get().document, SellingDocument.findById(currentDocument.id).document, getSellingDocumentHtml(currentDocument));
			}
		}
		return LIST_SELLING_DOCUMENTS;
	}
	
	
	private static Result displaySellingDocument(models.SellingDocument document) {
		return ok(getSellingDocumentHtml(document));
	}
	
	private static Html getSellingDocumentHtml(models.SellingDocument document) {
		if (document == null)
			return views.html.admin.accounting.selling_document.render(null, 0f);

		return views.html.admin.accounting.selling_document.render(document, 0f);
	}
	
	private static Float computeHTTotalAmountForVatLines(List<models.AccountingLine> lines) {
		if (lines == null)
			return 0f;
		Float result = 0f;
		for (models.AccountingLine line : lines)
			if (line!= null && models.AccountingLine.LineType.WITH_VAT_BY_UNIT.equals(line.type))
				result+=line.unitPrice*line.unit;
			
		return result;
	}
	
	private static Float computeHTTotalAmountForNonVatLines(List<models.AccountingLine> lines) {
		if (lines == null)
			return 0f;
		Float result = 0f;
		for (models.AccountingLine line : lines)
			if (line!= null && models.AccountingLine.LineType.WITHOUT_VAT_BY_UNIT.equals(line.type))
				result+=line.unitPrice*line.unit;
			
		return result;
	}
	
	private static models.SellingDocument getSellingDocumentFromForm(final Form<models.SellingDocument> documentForm) {
		models.SellingDocument document = documentForm.get();
		return document;
	}
	

	
	
}
