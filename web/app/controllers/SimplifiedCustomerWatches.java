package controllers;

import models.CustomerWatch;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR})
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
	
	public static Result displayServicingSheet(long id) {
		CustomerWatch instance = CustomerWatch.findById(id);
		
		if (instance != null)
		   return ok(views.html.admin.simplified_customer_watch_servicing_sheet.render(instance));
		    
		return badRequest("error");
	}
	
	
	public static Result displayTestingSheet(long id) {
		CustomerWatch instance = CustomerWatch.findById(id);
		
		if (instance != null)
		   return ok(views.html.admin.simplified_customer_watch_testing_sheet.render(instance));
		    
		return badRequest("error");
	}
	
	public static Result acceptWatch(Long watchId) {
		CustomerWatch instance = CustomerWatch.findById(watchId);
		if (controllers.CustomerWatch.acceptWatch(instance))
			return SimplifiedCustomers.crud.display(instance.customer.id);
		
		return badRequest("error");
	}
}
