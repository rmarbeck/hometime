package controllers;

import java.util.Date;

import models.CustomerWatch.CustomerWatchStatus;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.customer;
import views.html.admin.customer_form;
import views.html.admin.customer_watch_form;
import views.html.admin.customer_watches;
import views.html.admin.customer_watch;
import views.html.admin.customer_watches_for_partner;

@Security.Authenticated(SecuredAdminAndReservedOnly.class)
@With(NoCacheAction.class)
public class CustomerWatch extends Controller {
	
	public static Result LIST_CUSTOMER_WATCHES = redirect(
			routes.CustomerWatch.displayAll(0, "lastStatusUpdate", "desc", "", "")
			);

	@Security.Authenticated(SecuredAdminOnly.class)
	public static Result displayAll(int page, String sortBy, String order, String filter, String status) {
        return ok(customer_watches.render(models.CustomerWatch.page(page, 10, sortBy, order, filter, status), sortBy, order, filter, status));
    }

	@Security.Authenticated(SecuredAdminOnly.class)
	public static Result display(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customer_watch.render(existingWatch));
		flash("error", "Unknown watch id");
		return LIST_CUSTOMER_WATCHES;
    }
	
	@Security.Authenticated(SecuredAdminOnly.class)
	public static Result addFromCustomer(Long customerId) {
		models.Customer existingCustomer = models.Customer.findById(customerId);
		if (existingCustomer != null) {
			models.CustomerWatch newWatch = new models.CustomerWatch(existingCustomer);
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(newWatch), true));
		}
		flash("error", "Unknown customer id");
		return badRequest(emptyNewWatchForm());
	}
	
	@Security.Authenticated(SecuredAdminOnly.class)
	public static Result addFromOrder(Long orderId) {
		models.Order existingOrder = models.Order.findById(orderId);
		if (existingOrder != null) {
			models.CustomerWatch newWatch = new models.CustomerWatch(existingOrder);
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(newWatch), true));
		}
		flash("error", "Unknown customer id");
		return badRequest(emptyNewWatchForm());
	}
	
	@Security.Authenticated(SecuredAdminOnly.class)
	public static Result add() {
		return ok(emptyNewWatchForm());		
	}
	
	@Security.Authenticated(SecuredAdminOnly.class)
	public static Result edit(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	@Security.Authenticated(SecuredAdminOnly.class)
	public static Result setBackToCustomer(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null) {
			existingWatch.status = CustomerWatchStatus.BACK_TO_CUSTOMER;
			existingWatch.lastStatusUpdate = new Date();
			existingWatch.update();
		}
		return Admin.INDEX;
	}
	
	@Security.Authenticated(SecuredAdminOnly.class)
	public static Result manage() {
		final Form<models.CustomerWatch> watchForm = Form.form(models.CustomerWatch.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (watchForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(customerWatchForm(watchForm, true));
			return badRequest(customerWatchForm(watchForm, false));
		} else {
			models.CustomerWatch watch = watchForm.get();
			if ("save".equals(action)) {
				watch.save();
			} else if ("delete".equals(action)) {
				models.CustomerWatch customerWatchInDB = models.CustomerWatch.findById(watch.id);
				customerWatchInDB.delete();
			} else {
				watch.update();
			}
		}
		return LIST_CUSTOMER_WATCHES;
	}
	
	private static Html customerWatchForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_form.render(watchForm, isItANewWatch, models.Customer.findByNameAsc(), models.Partner.findAllByEmailAsc());
	}
	
	private static Html emptyNewWatchForm() {
		return customerWatchForm(Form.form(models.CustomerWatch.class), true);
	}
	
	@Security.Authenticated(SecuredAdminAndReserved2Only.class)
	public static Result displayWatchesForPartner(int page, String sortBy, String order, String filter, String status) {
        return ok(customer_watches_for_partner.render(models.CustomerWatch.pageForPartner(page, 10, sortBy, order, filter, status, session()), sortBy, order, filter, status));
    }
	
}
