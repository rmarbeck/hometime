package controllers;

import fr.hometime.utils.CustomerWatchHelper;
import fr.hometime.utils.PartnerAndCustomerHelper;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.customer_watches_for_collaborator;
import views.html.admin.customer_watch_for_collaborator;
import views.html.admin.customer_watch_for_collaborator_form;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR})
@With(NoCacheAction.class)
public class CollaboratorWatchmaker extends Controller {

	public static Result INDEX = Admin.INDEX;
	
	public static Result LIST_WATCHES = redirect(
			routes.CollaboratorWatchmaker.displayWatches(0, "id", "desc", "", 0, "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter, int size) {
        return displayWatches(page, sortBy, order, filter, size, "");
    }
	
	public static Result displayWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_collaborator.render(models.CustomerWatch.pageForCollaboratorWatchmaker(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
		
	public static Result displayWatch(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerAndCustomerHelper.isWatchOneOfLoggedInCollaborator(requestedWatch, session()))
			return ok(customer_watch_for_collaborator.render(requestedWatch));
        return INDEX;
    }
	
	public static Result prepareWatchUpdate(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerAndCustomerHelper.isWatchOneOfLoggedInCollaborator(requestedWatch, session()))
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(requestedWatch), false));
        return badRequest();
	}
	
	public static Result manageWatchUpdate() {
		return	manageWatchUpdate(LIST_WATCHES);
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
				CustomerWatchHelper.updateWatchEnsuringOnlyEditableDataByCollaboratorWatchmasterAreChanged(watch, session());
			}
		}
		return successResult;
	}
	
	private static Html customerWatchForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_for_collaborator_form.render(watchForm, isItANewWatch);
	}
}
