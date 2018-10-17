package controllers;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fr.hometime.utils.AccountingAnalyticsHelper;
import fr.hometime.utils.UniqueAccountingNumber;
import fr.hometime.utils.UsefullHelper;
import fr.hometime.utils.VATHelper;
import models.AccountingDocument;
import models.AccountingLine;
import models.AccountingLine.LineType;
import models.AccountingLineAnalyticPreset;
import models.Customer;
import models.CustomerWatch;
import models.Invoice;
import models.Invoice.InvoiceType;
import models.OrderDocument;
import models.PostSellingCertificate;
import models.PostServiceCertificate;
import models.SellingDocument;
import models.WatchToSell;
import play.data.Form;
import play.data.Form.Field;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.accounting.invoice_for_analytics;
import views.html.admin.accounting.invoice_form;
import views.html.admin.accounting.invoices;
import views.html.admin.accounting.order_document_form;
import views.html.admin.accounting.order_documents;
import views.html.admin.accounting.selling_document_form;
import views.html.admin.accounting.selling_documents;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
@With(NoCacheAction.class)
public class Accounting extends Controller {
	
	@FunctionalInterface
	public interface TriConsumer<T,U,S> {
		void accept(T t, U u, S s);
	}
	
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
			routes.Accounting.displayAllInvoice(0, "uniqueAccountingNumber", "desc", "")
			);
	
	public static Result displayAllInvoice(int page, String sortBy, String order, String filter) {
        return ok(invoices.render(models.Invoice.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result addInvoiceByWatchToSellId(Long id) {
		return ok(newInvoiceFormByWatchToSellId(id));		
	}
	
	public static Result addInvoiceFormByWatchToQuickServiceId(Long id, float price, boolean waterResistance) {
		return ok(newInvoiceFormByWatchToQuickServiceId(id, price, waterResistance));		
	}
	
	public static Result addInvoiceByOrderId(Long id) {
		return ok(newInvoiceFormByOrderId(id));		
	}
	
	public static Result addInvoiceFromCustomer(Long id) {
		return ok(newInvoiceFormByCustomerId(id));		
	}
	
	public static Result addInvoice() {
		return ok(emptyNewInvoiceForm());		
	}
	
	public static Result addOrderFormFormByWatchToService(Long id, boolean waterResistance) {
		return ok(newOrderFormByWatchToService(id, waterResistance));		
	}
	
	public static Result addOrderFormFormByWatchToRepair(Long id, boolean waterResistance) {
		return ok(newOrderFormByWatchToRepair(id, waterResistance));		
	}
	
	public static Result addInvoiceFormByWatchToQuickService(Long id, boolean waterResistance) {
		return ok(newOrderFormByWatchToQuickService(id, waterResistance));		
	}
	
	public static Result addInvoiceFormByWatchEmpty(Long id, boolean waterResistance) {
		return ok(newOrderFormByWatchEmpty(id, waterResistance));		
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
	
	public static Result viewInvoiceForAnalytics(Long invoiceId) {
		models.Invoice currentInvoice = models.Invoice.findById(invoiceId);
		if (currentInvoice != null)
			return ok(invoice_for_analytics.render(currentInvoice));
		return badRequest("error");
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
				manageAnalyticsBeforeSavingInvoice(invoiceForm, currentInvoice);
				currentInvoice.document.documentData = getInvoiceHtml(currentInvoice).body().getBytes(StandardCharsets.UTF_8);
				currentInvoice.save();
			} else if ("delete".equals(action)) {
				models.Invoice invoiceInDB = models.Invoice.findById(currentInvoice.id);
				invoiceInDB.delete();
			} else if ("show".equals(action)) {
				return displayInvoice(currentInvoice);
			} else {
				manageAnalyticsBeforeUpdatingInvoice(invoiceForm, currentInvoice);
				updateInvoice(invoiceForm);
			}
		}
		return LIST_INVOICES;
	}
	
	/*********************************************************************************************************************/
	
	private static Html invoiceForm(Form<models.Invoice> invoiceForm, boolean isItANewInvoice) {
		return invoice_form.render(invoiceForm, isItANewInvoice, models.Customer.findByNameAsc());
	}
	
	private static Html emptyNewInvoiceForm() {
		models.Invoice emptyInvoice = new Invoice();
		emptyInvoice.changeUniqueAccountingNumber(UniqueAccountingNumber.getNextForInvoices().toString());
		return invoiceForm(Form.form(models.Invoice.class).fill(emptyInvoice), true);
	}
	
	private static Html newInvoiceFormByWatchToSellId(long id) {
		models.WatchToSell watchToSell = WatchToSell.findById(id);
		if (watchToSell == null)
			return emptyNewInvoiceForm();
		models.Invoice newInvoice = new Invoice();
		newInvoice.changeUniqueAccountingNumber(UniqueAccountingNumber.getNextForInvoices().toString());
		newInvoice.document.customer = watchToSell.customerThatBoughtTheWatch;
		newInvoice.type = InvoiceType.MARGIN_VAT;
		newInvoice.description = Messages.get("admin.invoice.description.selling.a.watch", watchToSell.brand.display_name, watchToSell.model);
		newInvoice.addLine(LineType.WITHOUT_VAT_BY_UNIT, getMainLineForInvoice(watchToSell), Long.valueOf(1), Float.valueOf(watchToSell.sellingPrice), AccountingLineAnalyticPreset.findByMetaCode(AccountingAnalyticsHelper.findMetaCodeForSellingAWatch(watchToSell)), (float) watchToSell.purchasingPrice);
		newInvoice.addLine(LineType.FREE_INCLUDED, Messages.get("admin.invoice.waranty.line.selling.a.watch"), Long.valueOf(1), Float.valueOf(0));
		newInvoice.addLine(LineType.FREE_INCLUDED, Messages.get("admin.invoice.delivery.line.selling.a.watch"), Long.valueOf(1), Float.valueOf(0));
		return invoiceForm(Form.form(models.Invoice.class).fill(newInvoice), true);
	}
	
	
	private static Html newInvoiceFormByWatchToQuickServiceId(long id, float price, boolean waterResistance) {
		models.CustomerWatch watch = CustomerWatch.findById(id);
		if (watch == null)
			return emptyNewInvoiceForm();
		models.Invoice newInvoice = new Invoice();
		newInvoice.changeUniqueAccountingNumber(UniqueAccountingNumber.getNextForInvoices().toString());
		newInvoice.document.customer = watch.customer;
		newInvoice.type = InvoiceType.VAT;
		if (waterResistance) {
			newInvoice.description = Messages.get("admin.invoice.description.quick.servicing.a.watch.with.water.resistance", watch.brand, watch.model);
		} else {
			newInvoice.description = Messages.get("admin.invoice.description.quick.servicing.a.watch.without.water.resistance", watch.brand, watch.model);
		}
		newInvoice.addLine(LineType.WITH_VAT_BY_UNIT, getMainLineForInvoiceForQuickServicing(watch, waterResistance), Long.valueOf(1), price, AccountingLineAnalyticPreset.findByMetaCode(AccountingAnalyticsHelper.findMetaCodeForPartialServicingQuartzWatch(waterResistance, false)), -1f);
		if (waterResistance) {
			newInvoice.addLine(LineType.FREE_INCLUDED, Messages.get("admin.invoice.waranty.line.quick.servicing.a.watch.with.water.resistance"), Long.valueOf(1), Float.valueOf(0));
		} else {
			newInvoice.addLine(LineType.FREE_INCLUDED, Messages.get("admin.invoice.waranty.line.quick.servicing.a.watch.without.water.resistance"), Long.valueOf(1), Float.valueOf(0));
		}
		newInvoice.addLine(LineType.FREE_INCLUDED, Messages.get("admin.invoice.delivery.line.quick.servicing.a.watch"), Long.valueOf(1), Float.valueOf(0));
		return invoiceForm(Form.form(models.Invoice.class).fill(newInvoice), true);
	}
	
	private static Html newInvoiceFormByOrderId(long id) {
		models.OrderDocument orderToInspireFrom = OrderDocument.findById(id);
		if (orderToInspireFrom == null)
			return emptyNewInvoiceForm();
		models.Invoice newInvoice = new Invoice();
		newInvoice.changeUniqueAccountingNumber(UniqueAccountingNumber.getNextForInvoices().toString());
		newInvoice.document.customer = orderToInspireFrom.document.customer;
		newInvoice.type = orderToInspireFrom.type;
		newInvoice.description = orderToInspireFrom.description;
		if (orderToInspireFrom.document.retrieveLines() != null)
			for(AccountingLine line : orderToInspireFrom.document.retrieveLines())
				newInvoice.addLine(line.type, line.description, line.unit, line.unitPrice, line.preset, line.oneTimeCost);

		return invoiceForm(Form.form(models.Invoice.class).fill(newInvoice), true);
	}
	
	private static Html newInvoiceFormByCustomerId(long id) {
		models.Customer customer = Customer.findById(id);
		if (customer == null)
			return emptyNewInvoiceForm();
		models.Invoice newInvoice = new Invoice();
		newInvoice.changeUniqueAccountingNumber(UniqueAccountingNumber.getNextForInvoices().toString());
		newInvoice.document.customer = customer;
		return invoiceForm(Form.form(models.Invoice.class).fill(newInvoice), true);
	}
	
	private static String getMainLineForInvoice(WatchToSell watchToSell) {
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
	
	private static String getMainLineForInvoiceForQuickServicing(CustomerWatch watch, boolean waterResistance) {
		if (waterResistance) {
			return Messages.get("admin.invoice.main.line.quick.servicing.a.watch.with.water.resistance",
				watch.brand,
				watch.model);
		} else {
			return Messages.get("admin.invoice.main.line.quick.servicing.a.watch.without.water.resistance",
					watch.brand,
					watch.model);
		}
	}
	
	private static Html existingInvoiceForm(models.Invoice invoice) {
		invoice.document.reorderLines();
		return invoiceForm(Form.form(models.Invoice.class).fill(invoice), false);
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
	
	private static void updateInvoice(Form<models.Invoice> invoiceForm) {
		models.Invoice invoice = Invoice.findById(invoiceForm.get().id);
		cloneInvoice(invoice, invoiceForm.get());
		updateDocument(invoiceForm.get().document, Invoice.findById(invoice.id).document, getInvoiceHtml(invoiceForm.get()));
		invoice.update();
	}
	
	private static Html getInvoiceHtml(models.Invoice invoice) {
		if (invoice == null)
			return views.html.admin.accounting.invoice.render(null, 0f, 0f, 0f, 0f);
		Float htAmount = computeHTTotalAmountForVatLines(invoice.document.lines);
		Float totalAmount = VATHelper.getPriceAfterVAT(htAmount);
		Float vat = VATHelper.getVATAmountForNetPrice(htAmount);
		Float amountToPay = totalAmount + computeHTTotalAmountForNonVatLines(invoice.document.lines) - invoice.alreadyPayed;
		htAmount = computeHTTotalAmountForNonVatLines(invoice.document.lines);
		return views.html.admin.accounting.invoice.render(invoice, totalAmount, amountToPay, htAmount, vat);
	}
	
	private static models.Invoice getInvoiceFromForm(final Form<models.Invoice> invoiceForm) {
		models.Invoice invoice = invoiceForm.get();
		return invoice;
	}
	
	private static void cloneInvoice(Invoice existingInvoice, Invoice toClone) {
		if (toClone != null) {
			existingInvoice.description = toClone.description;
			existingInvoice.paymentConditions = toClone.paymentConditions;
			existingInvoice.supportedPaymentMethods = toClone.supportedPaymentMethods;
			existingInvoice.paymentMethodUsed = toClone.paymentMethodUsed;
			existingInvoice.alreadyPayed = toClone.alreadyPayed;
			existingInvoice.uniqueAccountingNumber = toClone.uniqueAccountingNumber;
			existingInvoice.type = toClone.type;
			existingInvoice.fromDate = toClone.fromDate;
			existingInvoice.toDate = toClone.toDate;
		}
	}
	
	
	private static void manageAnalyticsBeforeSavingInvoice(Form<models.Invoice> invoiceForm, models.Invoice invoice) {
		Field lines = invoiceForm.field("document.lines");
		lines.indexes().stream().forEach(index -> manageAnalyticForAccountingLine(invoice, index, UsefullHelper.getLongFromString(invoiceForm.data().get("document.lines["+index+"].preset.id")), UsefullHelper.getFloatFromString(invoiceForm.data().get("document.lines["+index+"].oneTimeCost"))));
	}
	
	private static void manageAnalyticsBeforeUpdatingInvoice(Form<models.Invoice> invoiceForm, models.Invoice invoice) {
		manageAnalyticsBeforeSavingInvoice(invoiceForm, invoice);
	}
	
	private static void manageAnalyticForAccountingLine(Invoice invoice, int index, Optional<Long> fpresetId, Optional<Float> foneTimeCost) {
		if (fpresetId.isPresent())
			AccountingAnalyticsHelper.addAnalyticsToLine(invoice, index, fpresetId.get(), foneTimeCost.orElse(-1f));
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
	
	public static Result addOrderDocumentByWatchToSellId(Long id) {
		return ok(newOrderDocumentFormByWatchToSellId(id));		
	}
	
	public static Result addOrderDocumentFromCustomer(Long id) {
		return ok(newOrderDocumentFormByCustomerId(id));		
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
				updateOrderDocument(orderDocumentForm);
			}
		}
		return LIST_ORDER_DOCUMENTS;
	}
	
	/*********************************************************************************************************************/
	
	private static Html orderDocumentForm(Form<models.OrderDocument> orderDocumentForm, boolean isItANewOrderDocument) {
		return order_document_form.render(orderDocumentForm, isItANewOrderDocument, models.Customer.findByNameAsc());
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
	
	private static Html newOrderDocumentFormByWatchToSellId(long id) {
		models.WatchToSell watchToSell = WatchToSell.findById(id);
		if (watchToSell == null)
			return emptyNewOrderDocumentForm();
		models.OrderDocument newOrder = new OrderDocument();
		newOrder.setUniqueAccountingNumber(UniqueAccountingNumber.getNextForOrders().toString());
		newOrder.document.customer = watchToSell.customerThatBoughtTheWatch;
		newOrder.type = InvoiceType.MARGIN_VAT;
		newOrder.description = Messages.get("admin.order.document.description.selling.a.watch", watchToSell.brand.display_name, watchToSell.model);
		newOrder.addLine(LineType.WITHOUT_VAT_BY_UNIT, getMainLineForOrder(watchToSell), Long.valueOf(1), Float.valueOf(watchToSell.sellingPrice), AccountingLineAnalyticPreset.findByMetaCode(AccountingAnalyticsHelper.findMetaCodeForSellingAWatch(watchToSell)), (float) watchToSell.purchasingPrice);
		newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.document.waranty.line.selling.a.watch"), Long.valueOf(1), Float.valueOf(0));
		newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.document.delivery.line.selling.a.watch"), Long.valueOf(1), Float.valueOf(0));
		return orderDocumentForm(Form.form(models.OrderDocument.class).fill(newOrder), true);
	}
	
	private static Html newOrderDocumentFormByCustomerId(long id) {
		models.Customer customer = Customer.findById(id);
		if (customer == null)
			return emptyNewOrderDocumentForm();
		models.OrderDocument newOrder = new OrderDocument();
		newOrder.setUniqueAccountingNumber(UniqueAccountingNumber.getNextForOrders().toString());
		newOrder.document.customer = customer;
		return orderDocumentForm(Form.form(models.OrderDocument.class).fill(newOrder), true);
	}
	
	private static Result displayOrderDocument(models.OrderDocument orderDocument) {
		return ok(getOrderDocumentHtml(orderDocument));
	}
	
	private static void updateOrderDocument(Form<models.OrderDocument> orderDocumentForm) {
		models.OrderDocument currentOrderDocument = OrderDocument.findById(orderDocumentForm.get().id);
		cloneOrderDocument(currentOrderDocument, orderDocumentForm.get());
		updateDocument(orderDocumentForm.get().document, OrderDocument.findById(currentOrderDocument.id).document, getOrderDocumentHtml(orderDocumentForm.get()));
		currentOrderDocument.update();
	}
	
	private static String getMainLineForOrder(WatchToSell watchToSell) {
		return Messages.get("admin.order.document.main.line.selling.a.watch",
							watchToSell.brand.display_name,
							watchToSell.model,
							watchToSell.additionnalModelInfos!=null && !watchToSell.additionnalModelInfos.equals("")?" "+watchToSell.additionnalModelInfos:"",
							watchToSell.isNew?Messages.get("admin.order.document.new.watch"):Messages.get("admin.order.document.used.watch"),
							watchToSell.reference,
							"",
							watchToSell.isNew?"":watchToSell.year!=null && !watchToSell.year.equals("")?Messages.get("admin.order.document.of.year",watchToSell.year):"",
							watchToSell.additionnalInfos!=null && !watchToSell.additionnalInfos.equals("")?" "+watchToSell.additionnalInfos:"",
							watchToSell.hasBox?(watchToSell.hasPapers?Messages.get("admin.order.document.box.and.papers"):Messages.get("admin.order.document.box.only")):(watchToSell.hasPapers?Messages.get("admin.order.document.papers.only"):Messages.get("admin.order.document.default")));
	}
	
	private static Html getOrderDocumentHtml(models.OrderDocument orderDocument) {
		if (orderDocument == null)
			return views.html.admin.accounting.order_document.render(null, 0f, 0f, 0f, 0f);
		Float htAmount = computeHTTotalAmountForVatLines(orderDocument.document.lines);
		Float totalAmount = VATHelper.getPriceAfterVAT(htAmount);
		Float vat = VATHelper.getVATAmountForNetPrice(htAmount);
		Float amountToPay = totalAmount + computeHTTotalAmountForNonVatLines(orderDocument.document.lines);

		return views.html.admin.accounting.order_document.render(orderDocument, totalAmount, amountToPay, htAmount, vat);
	}
	
	private static models.OrderDocument getOrderDocumentFromForm(final Form<models.OrderDocument> orderDocumentForm) {
		models.OrderDocument orderDocument = orderDocumentForm.get();

		return orderDocument;
	}
	
	private static void cloneOrderDocument(OrderDocument existingOrderDocument, OrderDocument toClone) {
		if (toClone != null) {
			existingOrderDocument.description = toClone.description;
			existingOrderDocument.paymentConditions = toClone.paymentConditions;
			existingOrderDocument.supportedPaymentMethods = toClone.supportedPaymentMethods;
			existingOrderDocument.uniqueAccountingNumber = toClone.uniqueAccountingNumber;
			existingOrderDocument.type = toClone.type;
			existingOrderDocument.delay = toClone.delay;
			existingOrderDocument.detailedInfos = toClone.detailedInfos;
			existingOrderDocument.validUntilDate = toClone.validUntilDate;
		}
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
	
	private static Html newOrderFormByWatchToService(long id, boolean waterResistance) {
		return newOrderGeneric(id, waterResistance, Accounting::populateOrderFormByWatchService);
	}
	
	private static Html newOrderFormByWatchToQuickService(long id, boolean waterResistance) {
		return newOrderGeneric(id, waterResistance, Accounting::populateOrderFormByWatchToQuickService);
	}
	
	private static Html newOrderFormByWatchToRepair(long id, boolean waterResistance) {
		return newOrderGeneric(id, waterResistance, Accounting::populateOrderFormByWatchToRepair);
	}
	
	private static Html newOrderFormByWatchEmpty(long id, boolean waterResistance) {
		return newOrderGeneric(id, waterResistance, Accounting::populateOrderFormByWatchEmpty);
	}
	
	private static void populateOrderFormByWatchService(models.OrderDocument newOrder, models.CustomerWatch watch, boolean waterResistance) {
		newOrder.description = Messages.get("admin.order.document.description.servicing.a.watch", watch.brand, watch.model);
		newOrder.detailedInfos = Messages.get("admin.order.document.details.servicing.a.watch");
		String mainLineContent = "";
		if (waterResistance) {
			mainLineContent = Messages.get("admin.order.description.servicing.a.watch.with.water.resistance", watch.brand, watch.model);
		} else {
			mainLineContent = Messages.get("admin.order.description.servicing.a.watch.without.water.resistance", watch.brand, watch.model);
		}
		newOrder.addLine(LineType.WITH_VAT_BY_UNIT, mainLineContent, Long.valueOf(1), watch.finalCustomerServicePrice.floatValue(), AccountingLineAnalyticPreset.findByMetaCode(AccountingAnalyticsHelper.findMetaCodeForPartialServicingMecanicalWatch(waterResistance, false)), -1f);
		if (waterResistance) {
			newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.waranty.line.servicing.a.watch.with.water.resistance"), Long.valueOf(1), Float.valueOf(0));
		} else {
			newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.waranty.line.servicing.a.watch.without.water.resistance"), Long.valueOf(1), Float.valueOf(0));
		}
		populateOrderFormGeneric(newOrder, watch, waterResistance);
	}
	
	private static void populateOrderFormByWatchToQuickService(models.OrderDocument newOrder, models.CustomerWatch watch, boolean waterResistance) {
		newOrder.description = Messages.get("admin.order.document.description.quick.servicing.a.watch", watch.brand, watch.model);
		String mainLineContent = "";
		if (waterResistance) {
			mainLineContent = Messages.get("admin.order.description.quick.servicing.a.watch.with.water.resistance", watch.brand, watch.model);
		} else {
			mainLineContent = Messages.get("admin.order.description.quick.servicing.a.watch.without.water.resistance", watch.brand, watch.model);
		}
		newOrder.addLine(LineType.WITH_VAT_BY_UNIT, mainLineContent, Long.valueOf(1), watch.finalCustomerServicePrice.floatValue(), AccountingLineAnalyticPreset.findByMetaCode(AccountingAnalyticsHelper.findMetaCodeForPartialServicingMecanicalWatch(waterResistance, false)), -1f);
		if (waterResistance) {
			newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.waranty.line.quick.servicing.a.watch.with.water.resistance"), Long.valueOf(1), Float.valueOf(0));
		}
		populateOrderFormGeneric(newOrder, watch, waterResistance);
	}
	
	private static void populateOrderFormByWatchToRepair(models.OrderDocument newOrder, models.CustomerWatch watch, boolean waterResistance) {
		newOrder.description = Messages.get("admin.order.document.description.repairing.a.watch", watch.brand, watch.model);
		newOrder.detailedInfos = Messages.get("admin.order.document.details.repairing.a.watch");
		String mainLineContent = "";
		if (waterResistance) {
			mainLineContent = Messages.get("admin.order.description.repairing.a.watch.with.water.resistance", watch.brand, watch.model);
		} else {
			mainLineContent = Messages.get("admin.order.description.repairing.a.watch.without.water.resistance", watch.brand, watch.model);
		}
		newOrder.addLine(LineType.WITH_VAT_BY_UNIT, mainLineContent, Long.valueOf(1), watch.finalCustomerServicePrice.floatValue(), AccountingLineAnalyticPreset.findByMetaCode(AccountingAnalyticsHelper.findMetaCodeForPartialServicingMecanicalWatch(waterResistance, false)), -1f);
		newOrder.addLine(LineType.WITH_VAT_BY_UNIT, "", Long.valueOf(1), 0f, AccountingLineAnalyticPreset.findByMetaCode(AccountingAnalyticsHelper.findMetaCodeForPartialServicingMecanicalWatch(waterResistance, false)), -1f);
		if (waterResistance) {
			newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.waranty.line.repairing.a.watch.with.water.resistance"), Long.valueOf(1), Float.valueOf(0));
		} else {
			newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.waranty.line.repairing.a.watch.without.water.resistance"), Long.valueOf(1), Float.valueOf(0));
		}
		populateOrderFormGeneric(newOrder, watch, waterResistance);
	}
	
	private static void populateOrderFormByWatchEmpty(models.OrderDocument newOrder, models.CustomerWatch watch, boolean waterResistance) {
		newOrder.description = Messages.get("admin.order.description.empty", watch.brand, watch.model);
		newOrder.addLine(LineType.WITH_VAT_BY_UNIT, Messages.get("admin.order.description.empty", watch.brand, watch.model), Long.valueOf(1), watch.finalCustomerServicePrice.floatValue(), AccountingLineAnalyticPreset.findByMetaCode(AccountingAnalyticsHelper.findMetaCodeForPartialServicingMecanicalWatch(waterResistance, false)), -1f);
		if (waterResistance) {
			newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.waranty.line.empty"), Long.valueOf(1), Float.valueOf(0));
		}
		populateOrderFormGeneric(newOrder, watch, waterResistance);
	}
	
	private static void populateOrderFormGeneric(models.OrderDocument newOrder, models.CustomerWatch watch, boolean waterResistance) {
		newOrder.addLine(LineType.FREE_INCLUDED, Messages.get("admin.order.delivery.line"), Long.valueOf(1), Float.valueOf(0));
	}
	
	private static Html newOrderGeneric(long id, boolean waterResistance, TriConsumer<models.OrderDocument, models.CustomerWatch, Boolean> populater) {
		models.CustomerWatch watch = CustomerWatch.findById(id);
		if (watch == null)
			return emptyNewOrderDocumentForm();
		models.OrderDocument newOrder = new OrderDocument();
		newOrder.setUniqueAccountingNumber(UniqueAccountingNumber.getNextForOrders().toString());
		newOrder.document.customer = watch.customer;
		newOrder.type = InvoiceType.VAT;
		newOrder.paymentConditions= Messages.get("admin.order.document.conditions.generic");
		newOrder.supportedPaymentMethods= Messages.get("admin.order.document.methods.generic");
		newOrder.delay= Messages.get("admin.order.document.delay.generic");
		populater.accept(newOrder, watch, waterResistance);
		return orderDocumentForm(Form.form(models.OrderDocument.class).fill(newOrder), true);
	}
	
	/*
	 * SELLING DOCUMENT
	 */
	
	public static Result LIST_SELLING_DOCUMENTS = redirect(
			routes.Accounting.displayAllSellingDocument(0, "uniqueAccountingNumber", "desc", "")
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
		return selling_document_form.render(documentForm, isItANewDocument, models.Customer.findByNameAsc());
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
				updateSellingDocument(documentForm);
				//updateDocument(documentForm.get().document, SellingDocument.findById(currentDocument.id).document, getSellingDocumentHtml(documentForm.get()));
			}
		}
		return LIST_SELLING_DOCUMENTS;
	}
	
	/*********************************************************************************************************************/
	
	
	private static Result displaySellingDocument(models.SellingDocument document) {
		return ok(getSellingDocumentHtml(document));
	}
	
	private static Html getSellingDocumentHtml(models.SellingDocument document) {
		if (document == null)
			return views.html.admin.accounting.selling_document.render(null, 0f);

		return views.html.admin.accounting.selling_document.render(document, 0f);
	}
	
	private static void updateSellingDocument(Form<models.SellingDocument> sellingDocumentForm) {
		models.SellingDocument currentSellingDocument = SellingDocument.findById(sellingDocumentForm.get().id);
		cloneSellingDocument(currentSellingDocument, sellingDocumentForm.get());
		updateDocument(sellingDocumentForm.get().document, SellingDocument.findById(currentSellingDocument.id).document, getSellingDocumentHtml(sellingDocumentForm.get()));
		currentSellingDocument.update();
	}
	
	private static void cloneSellingDocument(SellingDocument existingSellingDocument, SellingDocument toClone) {
		if (toClone != null) {
			existingSellingDocument.description = toClone.description;
			existingSellingDocument.uniqueAccountingNumber = toClone.uniqueAccountingNumber;
			existingSellingDocument.paymentMethodUsed = toClone.paymentMethodUsed;
			existingSellingDocument.date = toClone.date;
		}
	}

	
	/*
	 * POST SERVICE CERTIFICATE
	 */
	
	public static Result addPServiceCByWatchId(Long id) {
		return PostServiceCertificates.crud.create(newPServiceCByWatchId(id));
	}
	
	private static Form<PostServiceCertificate> newPServiceCByWatchId(long id) {
		models.CustomerWatch customerWatch = CustomerWatch.findById(id);
		models.PostServiceCertificate newCertificate = new PostServiceCertificate();
		newCertificate.displayPushingCrownTip = true;
		newCertificate.displayWaterLeakTip = true;
		newCertificate.displayWindingTip = true;
		
		if (customerWatch == null)
			return Form.form(models.PostServiceCertificate.class).fill(newCertificate);
		
		newCertificate.owner = customerWatch.customer;
		newCertificate.watch = customerWatch;
		newCertificate.workDone = customerWatch.serviceInfos;
		newCertificate.nextWaterproofingRecommendedYear = getYear(customerWatch.nextPartialService);
		newCertificate.nextServiceRecommendedYear = getYear(customerWatch.nextService);
		newCertificate.waterproofWarantyDate = customerWatch.endOfWaterWaranty;
		newCertificate.workingWarantyDate = customerWatch.endOfMainWaranty;
		
		
		return Form.form(models.PostServiceCertificate.class).fill(newCertificate);
	}
	
	/*
	 * POST SELLING CERTIFICATE
	 */
	
	public static Result addPSellingCByWatchId(Long id) {
		return PostSellingCertificates.crud.create(newPSellingCByWatchId(id));
	}
	
	private static Form<PostSellingCertificate> newPSellingCByWatchId(long id) {
		models.WatchToSell customerWatch = WatchToSell.findById(id);
		models.PostSellingCertificate newCertificate = new PostSellingCertificate();
		newCertificate.displayPushingCrownTip = true;
		newCertificate.displayWaterLeakTip = true;
		newCertificate.displayWindingTip = true;
		
		if (customerWatch == null)
			return Form.form(models.PostSellingCertificate.class).fill(newCertificate);
		
		newCertificate.owner = customerWatch.customerThatBoughtTheWatch;
		newCertificate.watch = customerWatch;
		if (customerWatch.isNew) {
			newCertificate.brandWaranted = true;
			newCertificate.brandWarantyDate = Date.from(LocalDate.now().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).withYear(LocalDate.now().getYear()+2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
			newCertificate.sellerWaranted = false;
			newCertificate.waterproofWaranted = false;
			newCertificate.testResult = Messages.get("admin.postsellingcertificate.form.testresult.watch.is.new");
			newCertificate.nextWaterproofingRecommendedYear = getYear(newCertificate.brandWarantyDate) + 1;
			newCertificate.nextServiceRecommendedYear = getYear(newCertificate.brandWarantyDate) + 1;
		} else {
			newCertificate.brandWaranted = false;
			newCertificate.sellerWaranted = true;
			newCertificate.sellerWarantyDate = Date.from(LocalDate.now().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).withYear(LocalDate.now().getYear()+2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
			newCertificate.waterproofWaranted = false;
			newCertificate.testResult = Messages.get("admin.postsellingcertificate.form.testresult.watch.has.been.tested");
			newCertificate.nextWaterproofingRecommendedYear = getYear(newCertificate.brandWarantyDate) + 1;
			newCertificate.nextServiceRecommendedYear = getYear(newCertificate.brandWarantyDate) + 1;
		}
		
		return Form.form(models.PostSellingCertificate.class).fill(newCertificate);
	}
	
	/*********************************************************************************************************************/
	
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
	

	private static int getYear(Date date) {
		if (date == null)
			date = new Date();
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault()).getYear();
	}
	
	
}
