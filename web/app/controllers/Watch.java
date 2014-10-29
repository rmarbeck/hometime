package controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.watches;
import views.html.admin.watch;
import views.html.admin.watch_form;


@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Watch extends Controller {
	
	public static Result LIST_WATCHES = redirect(
			routes.Watch.displayAll(0, "id", "desc", "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter) {
        return ok(watches.render(models.Watch.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }

	public static Result display(Long watchId) {
		models.Watch existingWatch = models.Watch.findById(watchId);
		if (existingWatch != null)
			return ok(watch.render(existingWatch));
		flash("error", "Unknown watch id");
		return LIST_WATCHES;
    }
	
	public static Result add() {
		return ok(emptyNewWatchForm());		
	}
	
	public static Result edit(Long watchId) {
		models.Watch existingWatch = models.Watch.findById(watchId);
		if (existingWatch != null)
			return ok(watchForm(Form.form(models.Watch.class).fill(existingWatch), false));
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result manage() {
		final Form<models.Watch> watchForm = Form.form(models.Watch.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (watchForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(watchForm(watchForm, true));
			return badRequest(watchForm(watchForm, false));
		} else {
			models.Watch watch = watchForm.get();
			if ("save".equals(action)) {
				watch.save();
			} else if ("delete".equals(action)) {
				models.Watch watchInDB = models.Watch.findById(watch.id);
				watchInDB.delete();
			} else {
				watch.update();
			}
		}
		return LIST_WATCHES;
	}
	
	private static Html watchForm(Form<models.Watch> watchForm, boolean isItANewWatch) {
		return watch_form.render(watchForm, isItANewWatch);
	}
	
	private static Html emptyNewWatchForm() {
		return watchForm(Form.form(models.Watch.class), true);
	}
}
