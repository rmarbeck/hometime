package controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.feedback;
import views.html.admin.feedback_form;
import views.html.admin.feedbacks;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Feedback extends Controller {
	
	public static Result LIST_FEEDBACKS = redirect(
			routes.Feedback.displayAll(0, "display_date", "desc", "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter) {
        return ok(feedbacks.render(models.Feedback.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }

	public static Result display(Long feedbackId) {
		models.Feedback existingFeedback = models.Feedback.findById(feedbackId);
		if (existingFeedback != null)
			return ok(feedback.render(existingFeedback));
		flash("error", "Unknown feedback id");
		return LIST_FEEDBACKS;
    }
	
	public static Result add() {
		return ok(emptyNewFeedbackForm());		
	}
	
	public static Result edit(Long feedbackId) {
		models.Feedback existingFeedback = models.Feedback.findById(feedbackId);
		if (existingFeedback != null)
			return ok(feedbackForm(Form.form(models.Feedback.class).fill(existingFeedback), false));
		flash("error", "Unknown feedback id");
		return badRequest(emptyNewFeedbackForm());
	}
	
	public static Result manage() {
		final Form<models.Feedback> feedbackForm = Form.form(models.Feedback.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (feedbackForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(feedbackForm(feedbackForm, true));
			return badRequest(feedbackForm(feedbackForm, false));
		} else {
			models.Feedback feedback = feedbackForm.get();
			if ("save".equals(action)) {
				feedback.save();
			} else if ("delete".equals(action)) {
				models.Feedback feedbackInDB = models.Feedback.findById(feedback.id);
				feedbackInDB.delete();
			} else {
				feedback.update();
			}
		}
		return LIST_FEEDBACKS;
	}
	
	private static Html feedbackForm(Form<models.Feedback> feedbackForm, boolean isItANewFeedback) {
		return feedback_form.render(feedbackForm, isItANewFeedback);
	}
	
	private static Html emptyNewFeedbackForm() {
		return feedbackForm(Form.form(models.Feedback.class), true);
	}
}
