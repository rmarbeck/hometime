package controllers;

import models.OrderRequest;
import controllers.Admin.QuotationForm;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.customers;
import views.html.admin.customer_form;
import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;

@Security.Authenticated(SecuredAdminOnly.class)
public class Customer extends Controller {
	
	public static Result LIST_CUSTOMERS = redirect(
			routes.Customer.displayAll(0, "creationDate", "desc", "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter) {
        return ok(customers.render(models.Customer.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result addNewCustomer(Long requestId) {
		OrderRequest request = OrderRequest.findById(requestId);
		Form<models.Customer> customerForm = Form.form(models.Customer.class);
		if (request != null) {
			models.Customer existingCustomer = models.Customer.findByEmail(request.email);
			if (existingCustomer != null)
				return ok(customer_form.render(customerForm.fill(existingCustomer), false));
			return ok(customer_form.render(customerForm.fill(models.Customer.getOrCreateCurstomerForOrderRequest(request)), true));
		}
		flash("error", "Unknown request id");
		return badRequest(customer_form.render(customerForm, true));
		
	}
	
	public static Result editCustomer(Long customerId) {
		models.Customer existingCustomer = models.Customer.findById(customerId);
		Form<models.Customer> customerForm = Form.form(models.Customer.class);
		if (existingCustomer != null)
			return ok(customer_form.render(Form.form(models.Customer.class).fill(existingCustomer), false));
		flash("error", "Unknown customer id");
		return badRequest(customer_form.render(customerForm, true));
		
	}
	
	public static Result manageCustomer() {
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
				customer.delete();
			} else {
				customer.update();
			}
		}
		return LIST_CUSTOMERS;
	}
}
