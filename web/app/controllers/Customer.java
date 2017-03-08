package controllers;

import controllers.routes;
import models.OrderRequest;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.admin.customer_form;
import views.html.admin.customers;
import views.html.admin.customer;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Customer extends Controller {
	
	public static Result LIST_CUSTOMERS = redirect(
			routes.Customer.displayAll(0, "creationDate", "desc", "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter) {
        return ok(customers.render(models.Customer.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result display(Long customerId) {
		models.Customer existingCustomer = models.Customer.findById(customerId);
		if (existingCustomer != null)
			return ok(customer.render(existingCustomer, models.Order.findByCustomer(existingCustomer), models.CustomerWatch.findByCustomer(existingCustomer), models.OrderDocument.findByCustomer(existingCustomer), models.Invoice.findByCustomer(existingCustomer)));
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
		return ok(customer_form.render(Form.form(models.Customer.class).fill(new models.Customer()), true));		
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
	}
}
