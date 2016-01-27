package controllers;

import java.util.List;

import models.MarginVatReport;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.admin.reports.index;
import views.html.admin.reports.address_tab;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Reporting extends Controller {
	public static Result index() {
		return ok(index.render(MarginVatReport.generateReport()));
    }
	
	public static Result addresses() {
		return ok(address_tab.render(getCustomers()));
    }
	
	private static List<models.Customer> getCustomers() {
		/*List<models.Customer> customers = new ArrayList<models.Customer>();
		for(int index = 0; index <10; index++) {
			customers.addAll(models.Customer.findPrintableByDescId());
		}*/
		return models.Customer.findPrintableByDescId();
	}
}
