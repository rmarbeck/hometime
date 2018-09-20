package controllers;

import java.util.Date;

import fr.hometime.utils.CustomerWatchHelper;
import fr.hometime.utils.PartnerAndCustomerHelper;
import models.CustomerWatch.CustomerWatchStatus;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.customer_for_partner_form;
import views.html.admin.customer_form;
import views.html.admin.customer_watch_for_partner;
import views.html.admin.customer_watch_for_partner_waiting_quotation_form;
import views.html.admin.customer_watch_for_partner_work_in_progress_form;
import views.html.admin.customer_watches_for_partner;
import views.html.admin.customer_watches_for_partner_waiting_acceptation;
import views.html.admin.customer_watches_for_partner_waiting_quotation;
import views.html.admin.customer_watches_for_partner_work_in_progress;
import views.html.admin.customers_for_partner;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.PARTNER})
@With(NoCacheAction.class)
public class Partner extends Controller {
	
	public static Result LIST_WAITING_ACCEPTATION_WATCHES = redirect(
			routes.Partner.displayWaitingAcceptationWatches(0, "lastStatusUpdate", "desc", "", 0, "")
			);
	
	public static Result LIST_WAITING_QUOTATION_WATCHES = redirect(
			routes.Partner.displayWaitingQuotationWatches(0, "lastStatusUpdate", "desc", "", 0, "")
			);
	
	public static Result LIST_WORK_IN_PROGRESS_WATCHES = redirect(
			routes.Partner.displayWorkInProgressWatches(0, "lastStatusUpdate", "desc", "", 0, "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter, int size) {
        return displayWatches(page, sortBy, order, filter, size, "");
    }
	
	public static Result displayWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_partner.render(models.CustomerWatch.pageForPartner(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWaitingAcceptationWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_partner_waiting_acceptation.render(models.CustomerWatch.pageForPartnerWaitingAcceptation(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWaitingQuotationWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_partner_waiting_quotation.render(models.CustomerWatch.pageForPartnerWaitingQuotation(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWorkInProgressWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_partner_work_in_progress.render(models.CustomerWatch.pageForPartnerWorkInProgress(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayCustomers(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customers_for_partner.render(models.Customer.pageForPartner(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWatch(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerAndCustomerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
			return ok(customer_watch_for_partner.render(requestedWatch));
        return LIST_WAITING_ACCEPTATION_WATCHES;
    }
	
	public static Result displayWatchAlias(Long id) {
		return displayWatch(id);
	}
	
	public static Result displayWatchAlias2(Long id) {
		return displayWatch(id);
	}

	public static Result acceptWatch(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerAndCustomerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
			acceptWatchAndSave(requestedWatch);
        return LIST_WAITING_ACCEPTATION_WATCHES;
    }
	
	public static Result markWatchFinishedUnFinished(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerAndCustomerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
			markFinishedUnFinished(requestedWatch);
        return LIST_WORK_IN_PROGRESS_WATCHES;
    }
	
	public static Result prepareQuotation(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result prepareWorkInProgress(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customerWatchForWorkInProgressForm(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result manageQuotation() {
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
				CustomerWatchHelper.updateWatchEnsuringOnlyEditableDataByPartnerAreChanged(watch, session());
			}
		}
		return LIST_WAITING_QUOTATION_WATCHES;
	}
	

	
	public static Result manageWorkInProgress() {
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
				CustomerWatchHelper.updateWatchEnsuringOnlyEditableDataByPartnerAreChanged(watch, session());
			}
		}
		return LIST_WORK_IN_PROGRESS_WATCHES;
	}
	
	public static Result addCustomer() {
		return ok(customerForm(Form.form(models.Customer.class).fill(new models.Customer()), true));		
	}
	
	public static Result prepareCustomer(Long customerId) {
		models.Customer existingCustomer = models.Customer.findById(customerId);
		Form<models.Customer> customerForm = Form.form(models.Customer.class);
		if (existingCustomer != null)
			return ok(customerForm(Form.form(models.Customer.class).fill(existingCustomer), false));
		flash("error", "Unknown customer id");
		return badRequest(customer_form.render(customerForm, true));		
	}
	
	public static Result edit(Long customerId) {
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
				return badRequest(customerForm(customerForm, true));
			return badRequest(customerForm(customerForm, false));
		} else {
			models.Customer customer = customerForm.get();
			if ("save".equals(action)) {
				customer.save();
			} else if ("delete".equals(action)) {
				return badRequest(customerForm(customerForm, true));
			} else {
				customer.update();
			}
		}
		return LIST_WORK_IN_PROGRESS_WATCHES;
	}
	
	private static Html emptyNewWatchForm() {
		return customerWatchForm(Form.form(models.CustomerWatch.class), true);
	}
	
	private static Html customerWatchForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_for_partner_waiting_quotation_form.render(watchForm, isItANewWatch);
	}
	
	private static Html customerForm(Form<models.Customer> customerForm, boolean isItANewCustomer) {
		return customer_for_partner_form.render(customerForm, isItANewCustomer);
	}
	
	private static Html customerWatchForWorkInProgressForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_for_partner_work_in_progress_form.render(watchForm, isItANewWatch);
	}
	
	private static void acceptWatchAndSave(models.CustomerWatch requestedWatch) {
		requestedWatch.status = CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER;
		requestedWatch.firstEntryInPartnerWorkshopDate = new Date();
		requestedWatch.update();
	}
	
	private static void markFinishedUnFinished(models.CustomerWatch requestedWatch) {
		requestedWatch.serviceNeeded = ! requestedWatch.serviceNeeded;
		requestedWatch.update();
	}
}
