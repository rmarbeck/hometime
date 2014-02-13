package controllers;

import fr.hometime.utils.ServiceTestHelper;
import models.OrderRequest;
import models.ServiceTest;
import models.ServiceTest.TestResult;
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
	
	public static Result LIST_SERVICE_TESTS = redirect(
			routes.Admin.displayServiceTests(0, "requestDate", "asc", "")
			);
	
	public static Result displayServiceTests(int page, String sortBy, String order, String filter) {
        return ok(service_tests.render(ServiceTest.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result displayServiceTest(long id) {
		if (serviceTestIsValid(id)) {
			ServiceTest serviceTestFound = ServiceTest.findById(id);
			TestResult result = ServiceTestHelper.whenDoServiceIsRecommended(serviceTestFound);
			
			return ok(service_test.render(serviceTestFound, result));
		}
		flash("error", "Unknown id");
		return LIST_SERVICE_TESTS;
    }
	
	private static boolean orderIsValid(long id) {
		OrderRequest orderFound = OrderRequest.findById(id);
		return orderFound != null;
	}
	
	private static boolean serviceTestIsValid(long id) {
		ServiceTest serviceTestFound = ServiceTest.findById(id);
		return serviceTestFound != null;
	}
}
