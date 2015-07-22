package controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.usefull_link;
import views.html.admin.usefull_link_form;
import views.html.admin.usefull_links;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class UsefullLink extends Controller {
	
	public static Result LIST_LINKS = redirect(
			routes.UsefullLink.displayAll(0, "creation_date", "desc", "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter) {
        return ok(usefull_links.render(models.UsefullLink.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }

	public static Result display(Long linkId) {
		models.UsefullLink existingLink = models.UsefullLink.findById(linkId);
		if (existingLink != null)
			return ok(usefull_link.render(existingLink));
		flash("error", "Unknown usefulllink id");
		return LIST_LINKS;
    }
	
	public static Result add() {
		return ok(emptyNewUsefullLinkForm());		
	}
	
	public static Result edit(Long linkId) {
		models.UsefullLink existingLink = models.UsefullLink.findById(linkId);
		if (existingLink != null)
			return ok(usefullLinkForm(Form.form(models.UsefullLink.class).fill(existingLink), false));
		flash("error", "Unknown usefulllink id");
		return badRequest(emptyNewUsefullLinkForm());
	}
	
	public static Result manage() {
		final Form<models.UsefullLink> usefullLinkForm = Form.form(models.UsefullLink.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (usefullLinkForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(usefullLinkForm(usefullLinkForm, true));
			return badRequest(usefullLinkForm(usefullLinkForm, false));
		} else {
			models.UsefullLink usefullLink = usefullLinkForm.get();
			if ("save".equals(action)) {
				usefullLink.save();
			} else if ("delete".equals(action)) {
				models.UsefullLink usefullLinkInDB = models.UsefullLink.findById(usefullLink.id);
				usefullLinkInDB.delete();
			} else {
				usefullLink.update();
			}
		}
		return LIST_LINKS;
	}
	
	private static Html usefullLinkForm(Form<models.UsefullLink> usefullLinkForm, boolean isItANewLink) {
		return usefull_link_form.render(usefullLinkForm, isItANewLink);
	}
	
	private static Html emptyNewUsefullLinkForm() {
		return usefullLinkForm(Form.form(models.UsefullLink.class), true);
	}
}
