package controllers;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

import models.Customer;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.simplified_customer_form_for_customer;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR})
public class SimplifiedCustomers extends Controller {
	public static Crud<Customer, Customer> crud = Crud.of(
			Customer.of(),
			views.html.admin.simplified_customer.ref(),
			views.html.admin.simplified_customer_form.ref(),
			views.html.admin.simplified_customers.ref());
	
    public static Result pageOfSpecialCustomers(int page, String sortBy, String order, String filter, int pageSize) {
        return ok(views.html.admin.simplified_customers.render(Customer.pageOfSpecialCustomers(page, pageSize, sortBy, order, filter), sortBy, order, filter, pageSize));
    }
    
	public static Result createNewCustomerByCustomer() {
		return ok(simplified_customer_form_for_customer.render(crud.getInstance().fillForm(Form.form(Customer.class), false), true));
    }
	
    public static Result manageNewCustomerByCustomer() {
    	Form<Customer> currentForm = Form.form(Customer.class).bindFromRequest();
    	Logger.debug(currentForm.toString());
		String action = Form.form().bindFromRequest().get("action");
		if (currentForm.hasErrors()) {
			Logger.error("error in form ["+currentForm+"] "+" - "+ currentForm.errors());
			if ("save".equals(action))
				return badRequest(simplified_customer_form_for_customer.render(currentForm, true));
			return badRequest(simplified_customer_form_for_customer.render(currentForm, false));
		} else {
			Customer currentInstance = crud.getInstance().getInstanceFromForm(currentForm);
			if ("save".equals(action)) {
				currentInstance.save();
			}
		}
		
		flash("success", "Merci, vos coordonnées ont été enregistrées avec succès !");
		return createNewCustomerByCustomer();
    }
}
