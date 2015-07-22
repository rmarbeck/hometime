package controllers;

import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import java.util.List;
import views.html.admin.invoice;
import views.html.admin.invoice_form;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Accounting extends Controller {
	
	public static Result INVOICE = redirect(
			routes.Accounting.display()
			);
	
	public static Result display() {
        return displayInvoice(null);
    }
	
	public static Result addInvoice() {
		return ok(emptyNewInvoiceForm());		
	}
	
	private static Html invoiceForm(Form<models.Invoice> invoiceForm, boolean isItANewInvoice) {
		return invoice_form.render(invoiceForm, isItANewInvoice, models.Customer.findAll());
	}
	
	private static Html emptyNewInvoiceForm() {
		return invoiceForm(Form.form(models.Invoice.class), true);
	}
	
	public static Result manageInvoice() {
		final Form<models.Invoice> invoiceForm = Form.form(models.Invoice.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (invoiceForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(invoiceForm(invoiceForm, true));
			return badRequest(invoiceForm(invoiceForm, false));
		} else {
			models.Invoice curentInvoice = invoiceForm.get();
			if ("save".equals(action)) {
				curentInvoice.save();
			} else if ("delete".equals(action)) {
				models.Invoice invoiceInDB = models.Invoice.findById(curentInvoice.id);
				invoiceInDB.delete();
			} else if ("show".equals(action)) {
				return displayInvoice(curentInvoice);
			} else {
				curentInvoice.update();
			}
		}
		return INVOICE;
	}
	
	private static Result displayInvoice(models.Invoice invoice) {
		if (invoice == null)
			return ok(views.html.admin.invoice.render(null, 0f, 0f, 0f, 0f));
		Float htAmount = computeHTTotalAmount(invoice);
		Float totalAmount = htAmount * 1.20f;
		Float vat = htAmount * 0.20f;
		Float amountToPay = totalAmount - invoice.alreadyPayed;

		return ok(views.html.admin.invoice.render(invoice, totalAmount, amountToPay, htAmount, vat));
	}
	
	private static Float computeHTTotalAmount(models.Invoice invoice) {
		List<models.AccountingLine> lines = invoice.lines;
		if (lines == null)
			return 0f;
		Float result = 0f;
		for (models.AccountingLine line : lines)
			if (line.type.equals(models.AccountingLine.LineType.WITH_VAT_BY_UNIT))
				result+=line.unitPrice*line.unit;
			
		return result;
	}
	
	/*public static Result LIST_CUSTOMERS = redirect(
			routes.Customer.displayAll(0, "creationDate", "desc", "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter) {
        return ok(customers.render(models.Customer.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result display(Long customerId) {
		models.Customer existingCustomer = models.Customer.findById(customerId);
		if (existingCustomer != null)
			return ok(customer.render(existingCustomer, models.Order.findByCustomer(existingCustomer), models.CustomerWatch.findByCustomer(existingCustomer)));
		flash("error", "Unknown customer id");
		return LIST_CUSTOMERS;
    }
	
	public static Result addFromRequest(Long requestId) {
		OrderRequest request = OrderRequest.findById(requestId);
		Form<models.Customer> customerForm = Form.form(models.Customer.class);
		if (request != null) {
			models.Customer existingCustomer = models.Customer.findByEmail(request.email);
			if (existingCustomer != null)
				return ok(customer_form.render(customerForm.fill(existingCustomer), false));
			return ok(customer_form.render(customerForm.fill(models.Customer.getOrCreateCustomerFromOrderRequest(request)), true));
		}
		flash("error", "Unknown request id");
		return badRequest(customer_form.render(customerForm, true));
	}
	
	public static Result add() {
		return ok(customer_form.render(Form.form(models.Customer.class), true));		
	}
	
	public static Result edit(Long customerId) {
		models.Customer existingCustomer = models.Customer.findById(customerId);
		Form<models.Customer> customerForm = Form.form(models.Customer.class);
		if (existingCustomer != null)
			return ok(customer_form.render(Form.form(models.Customer.class).fill(existingCustomer), false));
		flash("error", "Unknown customer id");
		return badRequest(customer_form.render(customerForm, true));
		
	}
	
	public static Result manage() {
		final Form<models.Customer> customerForm = Form.form(models.Customer.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (customerForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(customer_form.render(customerForm, true));
			return badRequest(customer_form.render(customerForm, false));
		} else {
			models.Customer customer = customerForm.get();
			if ("save".equals(action)) {
				customer.save();
			} else if ("delete".equals(action)) {
				models.Customer customerInDB = models.Customer.findById(customer.id);
				customerInDB.delete();
			} else {
				customer.update();
			}
		}
		return LIST_CUSTOMERS;
	}*/
}
