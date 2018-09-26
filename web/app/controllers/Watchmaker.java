package controllers;

import fr.hometime.utils.CustomerWatchHelper;
import fr.hometime.utils.PartnerAndCustomerHelper;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.customer_watch_for_watchmaker;
import views.html.admin.customer_watch_for_watchmaker_waiting_quotation_form;
import views.html.admin.customer_watch_for_watchmaker_work_in_progress_form;
import views.html.admin.customer_watches_for_watchmaker;
import views.html.admin.customer_watches_for_watchmaker_waiting_quotation;
import views.html.admin.customer_watches_for_watchmaker_work_in_progress;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER})
@With(NoCacheAction.class)
public class Watchmaker extends Controller {

	public static Result INDEX = Admin.INDEX;
	
	public static Result LIST_WAITING_QUOTATION_WATCHES = redirect(
			routes.Watchmaker.displayWaitingQuotationWatches(0, "id", "desc", "", 0, "")
			);
	
	public static Result LIST_WORK_IN_PROGRESS_WATCHES = redirect(
			routes.Watchmaker.displayWorkInProgressWatches(0, "id", "desc", "", 0, "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter, int size) {
        return displayWatches(page, sortBy, order, filter, size, "");
    }
	
	public static Result displayWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_watchmaker.render(models.CustomerWatch.pageForWatchmaker(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWaitingQuotationWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_watchmaker_waiting_quotation.render(models.CustomerWatch.pageForWatchmakerWaitingQuotation(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWorkInProgressWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_watchmaker_work_in_progress.render(models.CustomerWatch.pageForWatchmakerWorkInProgress(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWatch(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerAndCustomerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
			return ok(customer_watch_for_watchmaker.render(requestedWatch));
        return INDEX;
    }
	
	public static Result displayWatchAlias(Long id) {
		return displayWatch(id);
	}
	
	public static Result displayWatchAlias2(Long id) {
		return displayWatch(id);
	}
	
	public static Result markWatchFinishedUnFinished(Long id) {
		markFinishedUnFinished(models.CustomerWatch.findById(id));
        return LIST_WORK_IN_PROGRESS_WATCHES;
    }
	
	public static Result prepareQuotation(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		flash("error", "Unknown watch id");
		return badRequest();
	}
	
	public static Result prepareWorkInProgress(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customerWatchForWorkInProgressForm(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		flash("error", "Unknown watch id");
		return badRequest();
	}
	
	public static Result manageQuotation() {
		return	manageWatchUpdate(LIST_WORK_IN_PROGRESS_WATCHES);
	}
	

	
	public static Result manageWorkInProgress() {
		return	manageWatchUpdate(LIST_WORK_IN_PROGRESS_WATCHES);
	}
	
	
	private static Result manageWatchUpdate(Result successResult) {
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
				CustomerWatchHelper.updateWatchEnsuringOnlyEditableDataByWatchmasterAreChanged(watch, session());
			}
		}
		return successResult;
	}
	
	private static Html customerWatchForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_for_watchmaker_waiting_quotation_form.render(watchForm, isItANewWatch);
	}
	
	private static Html customerWatchForWorkInProgressForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_for_watchmaker_work_in_progress_form.render(watchForm, isItANewWatch);
	}
	
	private static void markFinishedUnFinished(models.CustomerWatch requestedWatch) {
		requestedWatch.serviceNeeded = ! requestedWatch.serviceNeeded;
		requestedWatch.update();
	}
}
