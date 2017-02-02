package controllers;

import models.OrderRequest;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.customer_watch_form;
import views.html.admin.order_form;
import views.html.admin.orders;
import views.html.admin.order;

@Security.Authenticated(SecuredAdminAndReservedOnly.class)
@With(NoCacheAction.class)
public class Order extends Controller {
	
	public static Result LIST_ORDERS = redirect(
			routes.Order.displayAll(0, "creationDate", "desc", "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter) {
        return ok(orders.render(models.Order.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result display(Long orderId) {
		models.Order existingOrder = models.Order.findById(orderId);
		if (existingOrder != null)
			return ok(order.render(existingOrder));
		flash("error", "Unknown order id");
		return LIST_ORDERS;
    }
	
	public static Result addFromRequest(Long requestId) {
		OrderRequest request = OrderRequest.findById(requestId);
		Form<models.Order> orderForm = Form.form(models.Order.class);
		if (request != null) {
			return ok(orderForm(orderForm.fill(fromRequest(request)), true));
		}
		flash("error", "Unknown request id");
		return badRequest(orderForm(orderForm, true));
	}
	
	public static Result addFromCustomer(Long customerId) {
		models.Customer customer = models.Customer.findById(customerId);
		Form<models.Order> orderForm = Form.form(models.Order.class);
		if (customer != null) {
			return ok(orderForm(orderForm.fill(fromCustomer(customer)), true));
		}
		flash("error", "Unknown customer id");
		return badRequest(orderForm(orderForm, true));
	}
	
	public static Result addFromCustomerWatch(Long customerWatchId) {
		models.CustomerWatch watch = models.CustomerWatch.findById(customerWatchId);
		Form<models.Order> orderForm = Form.form(models.Order.class);
		if (watch != null) {
			return ok(orderForm(orderForm.fill(fromCustomerWatch(watch)), true));
		}
		flash("error", "Unknown watch id");
		return badRequest(orderForm(orderForm, true));
	}
	
	public static Result add() {
		return ok(orderForm(Form.form(models.Order.class), true));		
	}
	
	public static Result edit(Long orderId) {
		models.Order existingOrder = models.Order.findById(orderId);
		Form<models.Order> orderForm = Form.form(models.Order.class);
		if (existingOrder != null)
			return ok(orderForm(Form.form(models.Order.class).fill(existingOrder), false));
		flash("error", "Unknown order id");
		return badRequest(orderForm(orderForm, true));
		
	}
	
	public static Result manage() {
		final Form<models.Order> orderForm = Form.form(models.Order.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (orderForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(orderForm(orderForm, true));
			return badRequest(orderForm(orderForm, false));
		} else {
			models.Order order = orderForm.get();
			if ("save".equals(action)) {
				saveOrder(order);
			} else if ("delete".equals(action)) {
				models.Order orderInDB = models.Order.findById(order.id);
				orderInDB.delete();
			} else {
				order.update();
			}
		}
		return LIST_ORDERS;
	}
	
	private static Html orderForm(Form<models.Order> orderForm, boolean isItANewOrder) {
		return order_form.render(orderForm, isItANewOrder, models.Customer.findAll());
	}
	
	private static models.Order fromRequest(OrderRequest request) {
		return new models.Order(request);
	}

	private static models.Order fromCustomer(models.Customer customer) {
		return new models.Order(customer);
	}
	
	private static models.Order fromCustomerWatch(models.CustomerWatch watch) {
		return new models.Order(watch);
	}
	
	private static void saveOrder(models.Order order) {
		if (isItANewCustomerToSave(order))
				order.customer.save();
		order.save();
	}
	
	private static boolean isItANewCustomerToSave(models.Order order) {
		if (order.customer.id == null) {
			models.Customer existingCustomer = models.Customer.findByEmail(order.customer.email);
			if (existingCustomer == null)
				return true;
		}
		return false;
	}
}
