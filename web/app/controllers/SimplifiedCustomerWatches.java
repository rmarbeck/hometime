package controllers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import fr.hometime.utils.DateHelper;
import models.CustomerWatch;
import models.Invoice;
import models.Payment;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(SecuredAdminOrCollaboratorOnly.class)
public class SimplifiedCustomerWatches extends Controller {
	public static Crud<CustomerWatch, CustomerWatch> crud = Crud.of(
			CustomerWatch.of(),
			views.html.admin.simplified_customer_watch.ref(),
			views.html.admin.simplified_customer_watch_form.ref(),
			views.html.admin.simplified_customer_watches.ref());
	
	public static Result createForCustomer(long id) {
		CustomerWatch instance = new CustomerWatch();
		models.Customer customer = models.Customer.findById(id);
		
		if (customer != null) {
			instance.customer = customer;
		}
		return crud.create(Form.form(CustomerWatch.class).fill(instance));
    }
	
	public static Result displayCollectingForm(long id) {
		CustomerWatch instance = CustomerWatch.findById(id);
		
		if (instance != null)
		   return ok(views.html.admin.simplified_customer_watch_collecting.render(instance));
		    
		return badRequest("error");
    }
}
