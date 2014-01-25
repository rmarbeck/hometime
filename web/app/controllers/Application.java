package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import fr.hometime.utils.ActionHelper;
import fr.hometime.utils.ServiceTestHelper;
import models.Brand;
import models.ContactRequest;
import models.OrderRequest;
import models.Picture;
import models.ServiceTest;
import models.Watch;
import models.ServiceTest.BuildPeriod;
import models.ServiceTest.LastServiceYear;
import models.ServiceTest.MovementTypes;
import models.ServiceTest.UsageFrequency;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	public static class OrderForm {

		@Constraints.Required
		public String orderType = OrderRequest.OrderTypes.SERVICE.toString();
		@Constraints.Required
		public String brand;
		@Constraints.Required
		public String model;
		@Constraints.Required
		public String method;
		@Column(length = 1000)
		public String remark;
		public String watchChosen;
		@Constraints.Required
		@Formats.NonEmpty
	    @Constraints.MaxLength(60)
		public String nameOfCustomer;
		@Constraints.Required
		@Constraints.Email
		@Constraints.MaxLength(60)
		public String email;
		public String phoneNumber;
		@Constraints.Required
		public String city;

	    public String validate() {
	        return null;
	    }
	    
	    public OrderForm() {
	    	super();
	    	orderType = OrderRequest.OrderTypes.SERVICE.toString();
	    }
	    
	    public OrderForm(String watchChosenId) {
	    	this();
	    	watchChosen = watchChosenId;
	    }
	    
	    public OrderRequest getRequest() {
	    	OrderRequest request = new OrderRequest();
	    	request.brand = Brand.findById(Long.valueOf(this.brand));
	    	if (!"".equals(this.watchChosen))
	    		request.watchChosen =  Watch.findById(Long.valueOf(this.watchChosen));
	    	request.orderType = OrderRequest.OrderTypes.fromString(this.orderType);
	    	request.method = OrderRequest.MethodTypes.fromString(this.method);
	    	request.city = this.city;
	    	request.email = this.email;
	    	request.model = this.model;
	    	request.nameOfCustomer = this.nameOfCustomer;
	    	request.phoneNumber = this.phoneNumber;
	    	request.remark = this.remark;
	    	
	    	return request;
	    }
	}
	
	public static class ServiceTestForm {
		@Constraints.Required
		public String movementType;
		@Constraints.Required
		public String buildPeriod;
		@Constraints.Required
		public String lastServiceYear;
		@Constraints.Required
		public String performanceIssue = "";
		@Constraints.Required		
		public String powerReserveIssue = "";
		@Constraints.Required
		public String waterIssue = "";
		@Constraints.Required
		public String usageFrequency;
		@Column(length = 1000)
		public String model;
		public String nameOfCustomer;
		@Constraints.Email
		public String email;

	    public List<ValidationError> validate() {
	    	List<ValidationError> errors = new ArrayList<ValidationError>();
	        if (email != null && !"".equals(email)) {
	        	if (nameOfCustomer == null || "".equals(nameOfCustomer))
	        		errors.add(new ValidationError("nameOfCustomer", Messages.get("service.test.validation.error.name.mandatory")));
	        	if (model == null || "".equals(model))
	        		errors.add(new ValidationError("model", Messages.get("service.test.validation.error.model.mandatory")));
	        } else {
	        	if ((nameOfCustomer != null && !"".equals(nameOfCustomer)) ||
	        			(model != null && !"".equals(model)))
	        		errors.add(new ValidationError("email", Messages.get("service.test.validation.error.email.mandatory")));
	        }
	        return errors.isEmpty() ? null : errors;
	    }
	    
	    public ServiceTestForm() {
	    	super();
	    }
	    
	    public ServiceTest getRequest() {
	    	ServiceTest request = new ServiceTest();
	    	request.movementType = ServiceTest.MovementTypes.fromString(this.movementType);
	    	request.buildPeriod = ServiceTest.BuildPeriod.fromString(this.buildPeriod);
	    	request.lastServiceYear = ServiceTest.LastServiceYear.fromString(this.lastServiceYear);
	    	request.performanceIssue = ("0".equals(this.performanceIssue))?false:true;
	    	request.powerReserveIssue = ("0".equals(this.powerReserveIssue))?false:true;
	    	request.waterIssue = ("0".equals(this.waterIssue))?false:true;
	    	request.usageFrequency = ServiceTest.UsageFrequency.fromString(this.usageFrequency);
	    	request.model = this.model;
	    	request.nameOfCustomer = this.nameOfCustomer;
	    	request.email = this.email;
	    	
	    	return request;
	    }
	}
	
	public static class ContactForm {

		@Constraints.Required
		@Formats.NonEmpty
		public String title;
		@Constraints.Required
		@Column(length = 1000)
		@Formats.NonEmpty
		public String message;
		@Constraints.Required
		@Formats.NonEmpty
	    @Constraints.MaxLength(60)
		public String name;
		@Constraints.Email
		@Constraints.MaxLength(60)
		public String email;
		
	    public String validate() {
	        return null;
	    }
	    
	    public ContactForm() {
	    	super();
	    }
	    
	    public ContactRequest getRequest() {
	    	ContactRequest request = new ContactRequest();
	    	request.title = this.title;
	    	request.message = this.message;
	    	request.name = this.name;
	    	if (this.email != null && "".equals(this.email))
	    		request.email = this.email;
	    	
	    	return request;
	    }
	}
	

    public static Result index() {
        return ok(index.render("", getSupportedBrands()));
    }

    public static Result service() {
        return ok(service.render(""));
    }

    public static Result watch_collection() {
        return ok(watch_collection.render("", getDisplayableWatches()));
    }
    
    public static Result offer() {
        return ok(offer.render(""));
    }

    public static Result more() {
        return ok(more.render(""));
    }

    public static Result prices() {
        return ok(prices.render(""));
    }

    public static Result order(String brandName) {
    	try {
    		
	        return ok(order.render("", fillFormWithQueryParams(brandName), getSupportedBrands(), getDisplayableWatches()));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result contact() {
    	try {
	        return ok(contact.render("", Form.form(ContactForm.class).fill(new ContactForm())));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result service_test() {
    	try {
	        return ok(service_test.render("", Form.form(ServiceTestForm.class).fill(new ServiceTestForm())));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result checkServiceTestResult(Integer id, boolean custom, String email) {
    	try {
    		return ok(service_test_result.render("", ServiceTest.TestResult.fromString(""+id), custom, email));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }

    public static Result about() {
        return ok(about.render());
    }
    
    public static Result privacy() {
        return ok(privacy.render());
    }
    
    public static Result cgv() {
        return ok(cgv.render());
    }

	public static Result manageOrder() {
		Form<OrderForm> orderForm = Form.form(OrderForm.class).bindFromRequest();
		if(orderForm.hasErrors()) {
			return badRequest(order.render("", orderForm, getSupportedBrands(), getDisplayableWatches()));
		} else {
			OrderRequest orderRequest = orderForm.get().getRequest();
			orderRequest.save();
			
			ActionHelper.tryToNotifyTeamByEmail("Nouvelle demande de devis", orderRequest.toString());
			
			flash("success", "OK");
			
			return redirect(
					routes.Application.order(orderRequest.brand.seo_name)
					);
		}
	}
	
	public static Result manageContact() {
		Form<ContactForm> contactForm = Form.form(ContactForm.class).bindFromRequest();
		if(contactForm.hasErrors()) {
			return badRequest(contact.render("", contactForm));
		} else {
			ContactRequest contactRequest = contactForm.get().getRequest();
			contactRequest.save();

			ActionHelper.tryToNotifyTeamByEmail("Prise de contact : "+contactRequest.title, contactRequest.toString());
			
			flash("success", "OK");
			
			return contact();
		}
	}
	
	public static Result manageServiceTest() {
		Form<ServiceTestForm> serviceTestForm = Form.form(ServiceTestForm.class).bindFromRequest();
		if(serviceTestForm.hasErrors()) {
			return badRequest(service_test.render("", serviceTestForm));
		} else {
			ServiceTest serviceTest = serviceTestForm.get().getRequest();
			boolean isCustomisedTestAsked = false; 
			if (serviceTest.email != null && !"".equals(serviceTest.email)) {
				isCustomisedTestAsked = true;
				serviceTest.save();
				ActionHelper.tryToNotifyTeamByEmail("Evaluation personnalisée", serviceTest.toString());
			}
			
			return ok(service_test_result.render("", ServiceTestHelper.whenDoServiceIsRecommended(serviceTest), isCustomisedTestAsked, serviceTest.email));
		}
	}
    
    public static Result watch_detail(Long id) {
    	try {
	    	Watch currentWatch = Watch.findById(id);
	        return ok(watch_detail.render(currentWatch.short_name, currentWatch, Picture.findPicturesForWatch(currentWatch), getDisplayableWatchesExceptOne(currentWatch)));
    	} catch (Exception e) {
    		return badRequest();
    	}
    }

    public static Result sitemap() {
    	return ok(views.xml.sitemap.render(getDisplayableWatches(), getSupportedBrands()));
    }
    
    public static Result siteplan() {
    	return ok(site_plan.render(getDisplayableWatches(), getSupportedBrands()));
    }

    private static List<Watch> getDisplayableWatches() {
    	return Watch.findDisplayable();
    }

    private static List<Watch> getAvailableWatches() {
    	return Watch.findAvailable();
    }

    private static List<Brand> getSupportedBrands() {
    	return Brand.findAllByAscId();
    }

    private static List<Watch> getDisplayableWatchesExceptOne(Watch currentWatch) {
    	List<Watch> displayableWatches = getDisplayableWatches();
    	if (displayableWatches != null && !displayableWatches.isEmpty())
    		displayableWatches.remove(currentWatch);
    	return displayableWatches;
    }
    
    private static Form<OrderForm> fillFormWithQueryParams(String brandName) {
    	OrderForm orderForm = new OrderForm();
    	if (isBrandParamValid(brandName))
    		orderForm.brand = Brand.findBySeoName(brandName).id.toString();
    	if (isWatchParamFoundAndValid())
    		orderForm.watchChosen = form().bindFromRequest().get("watch");
    	return Form.form(OrderForm.class).fill(orderForm);
    }
    
    private static boolean isWatchParamFoundAndValid() {
    	String valueFound = form().bindFromRequest().get("watch");
    	if (valueFound!= null)
    		return isWatchParamValid(valueFound);
    	return false;
    }
    
    private static boolean isWatchParamValid(String value) {
    	try {
	    	if (Watch.findById(Long.valueOf(value)) != null)
	    		return true;
	    	return false;
    	} catch (NumberFormatException e) {
    		return false;
    	}
    }
    
    private static boolean isBrandParamValid(String brandName) {
    	try {
	    	if (Brand.findBySeoName(brandName) != null)
	    		return true;
	    	return false;
    	} catch (NumberFormatException e) {
    		return false;
    	}
    }
}
