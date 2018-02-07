package controllers;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import fr.hometime.utils.CustomerWatchHelper;
import fr.hometime.utils.PartnerAndCustomerHelper;
import models.BuyRequest;
import models.Customer;
import models.CustomerWatch;
import models.OrderRequest;
import models.CustomerWatch.CustomerWatchStatus;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.customer_watch_for_customer;
import views.html.admin.customer_watch_for_customer_form;
import views.html.admin.index;
import views.html.admin.index_customer_content;

@Security.Authenticated(SecuredAdminOrCustomerOnly.class)
@With(NoCacheAction.class)
public class CustomerAdmin extends Controller {
	
	public static Result INDEX = redirect(
			routes.CustomerAdmin.index()
			);
	
	public static Result index() {
		return ok(index.render("", Customer.findWithOpenTopic(), OrderRequest.findAllUnReplied(), BuyRequest.findAllUnReplied(), models.CustomerWatch.findAllUnderOurResponsability()));
    }
	
	public static Result displayWatch(Long watchId) {
		return checkCustomerForWatch(watchId, (foundWatch) -> ok(customer_watch_for_customer.render(foundWatch)));
    }
	
	public static Result prepareNewWatchCreation() {
		Optional<Customer> currentCustomer = PartnerAndCustomerHelper.getLoggedInCustomer(session()); 
		if (currentCustomer.isPresent()) {
			models.CustomerWatch newWatch = new models.CustomerWatch(currentCustomer.get());
			newWatch.status = CustomerWatchStatus.BACK_TO_CUSTOMER;
			return ok(customer_watch_for_customer_form.render(Form.form(models.CustomerWatch.class).fill(newWatch), true));
		}
		return INDEX;
    }
	
	public static Result prepareWatchEdition(Long watchId) {
		return checkCustomerForWatch(watchId, (foundWatch) -> ok(customer_watch_for_customer_form.render(Form.form(models.CustomerWatch.class).fill(foundWatch), false)));
    }
	
	public static Result acceptQuotation(Long watchId) {
		return checkCustomerForWatch(watchId, (foundWatch) -> {
			foundWatch.finalCustomerServicePriceAccepted = true;
			if (PartnerAndCustomerHelper.isWatchAllocatedToInternalPartner(foundWatch))
				foundWatch.servicePriceAccepted = true;
			foundWatch.update();
			return INDEX;
		});
    }
	
	public static Result prepareRefuseQuotation(Long watchId) {
		flash("warning", "Expliquer dans la zone \"Informations spécifiques\" les raisons du refus de devis.");
		return checkCustomerForWatch(watchId, (foundWatch) -> ok(customer_watch_for_customer_form.render(Form.form(models.CustomerWatch.class).fill(foundWatch), false)));
    }
	
	private static Result checkCustomerForWatch(Long watchId, Function<models.CustomerWatch, Result> manageWatch) {
		return checkCustomerAndThen(() -> {
			models.CustomerWatch foundWatch = models.CustomerWatch.findById(watchId);
			if (foundWatch != null && PartnerAndCustomerHelper.isWatchOneOfLoggedInCustomer(foundWatch, session()))
				return manageWatch.apply(foundWatch);
			return INDEX;
		});
	}
	
	private static Result checkCustomerAndThen(Supplier<Result> manage) {
		Optional<Customer> currentCustomer = PartnerAndCustomerHelper.getLoggedInCustomer(session()); 
		if (currentCustomer.isPresent()) {
			return manage.get();
		}
		return INDEX;
	}
	
	public static Result manageWatch() {
		final Form<models.CustomerWatch> watchForm = Form.form(models.CustomerWatch.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (watchForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(customer_watch_for_customer_form.render(watchForm, true));
			return badRequest(customer_watch_for_customer_form.render(watchForm, false));
		} else {
			models.CustomerWatch watch = watchForm.get();
			if ("save".equals(action)) {
				watch.partner = PartnerAndCustomerHelper.findInternalPartner();
				watch.save();
			} else if ("delete".equals(action)) {
				models.CustomerWatch customerWatchInDB = models.CustomerWatch.findById(watch.id);
				customerWatchInDB.delete();
			} else {
				CustomerWatchHelper.updateWatchEnsuringOnlyEditableDataByCustomerAreChanged(watch);
			}
		}
		return INDEX;
	}

	
	
	/*
	public static Result LIST_WAITING_ACCEPTATION_WATCHES = redirect(
			routes.AdminForCustomer.displayWaitingAcceptationWatches(0, "lastStatusUpdate", "desc", "", 20, "")
			);
	
	public static Result displayWaitingAcceptationWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_customer_waiting_acceptation.render(models.CustomerWatch.pageForCustomerWaitingAcceptation(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result acceptWatchBack(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerAndCustomerHelper.isWatchOneOfLoggedInCustomer(requestedWatch, session()))
			acceptWatchBackAndSave(requestedWatch);
        return LIST_WAITING_ACCEPTATION_WATCHES;
    }
	
	private static void acceptWatchBackAndSave(models.CustomerWatch requestedWatch) {
		requestedWatch.status = CustomerWatchStatus.BACK_TO_CUSTOMER;
		requestedWatch.backToCustomerDate = new Date();
		requestedWatch.update();
	}
	
	public static Result acceptServicePrice(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerAndCustomerHelper.isWatchOneOfLoggedInCustomer(requestedWatch, session()))
			acceptWatchBackAndSave(requestedWatch);
        return LIST_WAITING_ACCEPTATION_WATCHES;
    }
	
	private static void acceptServicePriceAndSave(models.CustomerWatch requestedWatch) {
		requestedWatch.status = CustomerWatchStatus.BACK_TO_CUSTOMER;
		requestedWatch.backToCustomerDate = new Date();
		requestedWatch.update();
	}
	*/
	
	/*public static Result LIST_WAITING_ACCEPTATION_WATCHES = redirect(
			routes.AdminForCustomer.displayWaitingAcceptationWatches(0, "lastStatusUpdate", "desc", "", 20, "")
			);
	
	public static Result LIST_WAITING_QUOTATION_WATCHES = redirect(
			routes.AdminForCustomer.displayWaitingQuotationWatches(0, "lastStatusUpdate", "desc", "", 20, "")
			);
	
	public static Result LIST_WORK_IN_PROGRESS_WATCHES = redirect(
			routes.AdminForCustomer.displayWorkInProgressWatches(0, "lastStatusUpdate", "desc", "", 20, "")
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
	
	public static Result displayWatch(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
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
		if (PartnerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
			acceptWatchAndSave(requestedWatch);
        return LIST_WAITING_ACCEPTATION_WATCHES;
    }
	
	public static Result markWatchFinishedUnFinished(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
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
				watch.update();
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
				watch.update();
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
	}*/
}
