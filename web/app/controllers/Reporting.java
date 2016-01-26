package controllers;

import models.MarginVatReport;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.admin.reports.index;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Reporting extends Controller {
	public static Result index() {
		return ok(index.render(MarginVatReport.generateReport()));
    }
}
