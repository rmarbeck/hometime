package controllers;

import java.util.Date;

import models.CustomerWatch.CustomerWatchStatus;
import fr.hometime.utils.PartnerHelper;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.customer_watch_form;
import views.html.admin.customer_watch_for_partner_waiting_quotation_form;
import views.html.admin.customer_watches_for_partner;
import views.html.admin.customer_watches_for_partner_waiting_acceptation;
import views.html.admin.customer_watches_for_partner_waiting_quotation;

@Security.Authenticated(SecuredAdminAndReserved2Only.class)
@With(NoCacheAction.class)
public class Partner extends Controller {
	
	public static Result LIST_WAITING_ACCEPTATION_WATCHES = redirect(
			routes.Partner.displayWaitingAcceptationWatches(0, "lastStatusUpdate", "desc", "", 20, "")
			);
	
	public static Result LIST_WAITING_QUOTATION_WATCHES = redirect(
			routes.Partner.displayWaitingQuotationWatches(0, "lastStatusUpdate", "desc", "", 20, "")
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

	public static Result acceptWatch(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
			acceptWatchAndSave(requestedWatch);
        return LIST_WAITING_ACCEPTATION_WATCHES;
    }
	
	public static Result prepareQuotation(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
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
	
	private static Html emptyNewWatchForm() {
		return customerWatchForm(Form.form(models.CustomerWatch.class), true);
	}
	
	private static Html customerWatchForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_for_partner_waiting_quotation_form.render(watchForm, isItANewWatch);
	}
	
	private static void acceptWatchAndSave(models.CustomerWatch requestedWatch) {
		requestedWatch.status = CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER;
		requestedWatch.firstEntryInPartnerWorkshopDate = new Date();
		requestedWatch.update();
	}
}
