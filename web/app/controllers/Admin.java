package controllers;

import models.OrderRequest;
import play.mvc.*;
import play.data.Form;
import views.html.admin.*;

@Security.Authenticated(SecuredAdminOnly.class)
public class Admin extends Controller {
	public static Result index() {
        return ok(index.render("", OrderRequest.findAllLatestFirst()));
    }
	
	public static Result LIST_ORDERS = redirect(
			routes.Admin.displayOrders(0, "requestDate", "asc", "")
			);
	
	public static Result displayOrders(int page, String sortBy, String order, String filter) {
        return ok(orders.render(OrderRequest.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result displayOrder(long id) {
		if (orderIsValid(id))
			return ok(order.render(OrderRequest.findById(id)));
		flash("error", "Unknown id");
		return LIST_ORDERS;
    }
	
	private static boolean orderIsValid(long id) {
		OrderRequest orderFound = OrderRequest.findById(id);
		return orderFound != null;
	}
}
