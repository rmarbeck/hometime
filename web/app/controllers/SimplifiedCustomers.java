package controllers;

import models.Customer;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

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
		return crud.create(Form.form(Customer.class));
    }
}
