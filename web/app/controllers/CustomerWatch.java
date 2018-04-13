package controllers;

import java.util.Date;

import fr.hometime.utils.CustomerWatchActions;
import fr.hometime.utils.CustomerWatchHelper;
import fr.hometime.utils.PartnerAndCustomerHelper;
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

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class CustomerWatch extends Controller {
	
	public static Result LIST_CUSTOMER_WATCHES = redirect(
			routes.CustomerWatch.displayAll(0, "creationDate", "desc", "", "")
			);

	public static Result displayAll(int page, String sortBy, String order, String filter, String status) {
        return ok(customer_watches.render(models.CustomerWatch.page(page, 10, sortBy, order, filter, status), sortBy, order, filter, status));
    }

	public static Result display(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customer_watch.render(existingWatch));
		flash("error", "Unknown watch id");
		return LIST_CUSTOMER_WATCHES;
    }
	
	public static Result addFromCustomer(Long customerId) {
		models.Customer existingCustomer = models.Customer.findById(customerId);
		if (existingCustomer != null) {
			models.CustomerWatch newWatch = new models.CustomerWatch(existingCustomer);
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(newWatch), true));
		}
		flash("error", "Unknown customer id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result addFromOrder(Long orderId) {
		models.Order existingOrder = models.Order.findById(orderId);
		if (existingOrder != null) {
			models.CustomerWatch newWatch = new models.CustomerWatch(existingOrder);
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(newWatch), true));
		}
		flash("error", "Unknown customer id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result acceptWatchFromCustomer(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (acceptWatch(existingWatch))
			return Customer.display(existingWatch.customer.id);
		flash("error", "Unknown watch id");
		return LIST_CUSTOMER_WATCHES;
	}
	
	protected static boolean acceptWatch(models.CustomerWatch watchToAccept) {
		if (watchToAccept != null) {
			watchToAccept.collectingDate = new Date();
			watchToAccept.status = CustomerWatchStatus.STORED_BY_WATCH_NEXT_OUTSIDE;
			watchToAccept.status = CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER;
			watchToAccept.partner = PartnerAndCustomerHelper.findInternalPartner();
			watchToAccept.update();
			return true;
		}
		return false;
		
	}
	
	public static Result add() {
		return ok(emptyNewWatchForm());		
	}
	
	@Security.Authenticated(SecuredAdminOrCollaboratorOnly.class)
	public static Result doAction(String actionName, Long watchId) {
		CustomerWatchActions.doAction(CustomerWatchActions.CustomerWatchActionList.fromString(actionName), models.CustomerWatch.findById(watchId), session());
		return LIST_CUSTOMER_WATCHES;
	}
	
	public static Result edit(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result duplicate(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null) {
			existingWatch.additionnalModelInfos = "duplicate";
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(existingWatch), true));
		}
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result setBackToCustomer(Long watchId) {
		return updateStatus(watchId, CustomerWatchStatus.BACK_TO_CUSTOMER);
	}
	
	public static Result setStoredByPartner(Long watchId) {
		return updateStatus(watchId, CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER);
	}
	
	public static Result setStoredByUs(Long watchId) {
		return updateStatus(watchId, CustomerWatchStatus.STORED_BY_WATCH_NEXT);
	}
	
	public static Result setStoredByUsOutside(Long watchId) {
		return updateStatus(watchId, CustomerWatchStatus.STORED_BY_WATCH_NEXT_OUTSIDE);
	}
	
	private static Result updateStatus(Long watchId, CustomerWatchStatus newStatus) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null) {
			existingWatch.status = newStatus;
			existingWatch.lastStatusUpdate = new Date();
			existingWatch.update();
		}
		return Admin.INDEX;
	}
	
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
	
	protected static Html customerWatchForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_form.render(watchForm, isItANewWatch, models.Customer.findByNameAsc(), models.Partner.findAllByEmailAsc());
	}
	
	protected static Html emptyNewWatchForm() {
		return customerWatchForm(Form.form(models.CustomerWatch.class), true);
	}
}
