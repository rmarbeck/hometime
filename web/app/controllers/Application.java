package controllers;

import static play.data.Form.form;
import static fr.hometime.utils.SecurityHelper.doesFieldContainSPAM;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;

import fr.hometime.utils.ActionHelper;
import fr.hometime.utils.GoogleAnalyticsHelper;
import fr.hometime.utils.ListHelper;
import fr.hometime.utils.RandomHelper;
import fr.hometime.utils.SecurityHelper;
import fr.hometime.utils.ServiceTestHelper;
import models.Brand;
import models.BuyRequest;
import models.ContactRequest;
import models.Feedback;
import models.OrderRequest;
import models.Picture;
import models.ServiceTest;
import models.UsefullLink;
import models.User;
import models.Watch;
import play.Logger;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.libs.ws.*;
import play.mvc.*;
import views.html.*;
import views.html.admin.login;
import views.html.admin.quick_login;
import views.html.mails.notify_order;
import views.html.mails.notify_buy_request;

@With(SessionWatcher.class)
public class Application extends Controller {

	public static class LoginForm {
		@Constraints.Email
		@Constraints.Required
	    public String email;
		@Constraints.Required
	    public String password;
	    
		public String origin;
		
	    public String validate() {
	    	Logger.debug("Validating credentials : [{}]:[{}]", email, password);
	        if (User.authenticate(email, password) == null) {
	        	Logger.debug("Authentication failed");
	        	return "Invalid user or password";
	        }
	        return null;
	    }
	}
	
	public static class QuickLoginForm {
		@Constraints.Required
	    public String password;
		
		public String origin;
	    
	    public String validate() {
	    	Logger.debug("Validating quick login for admin : [{}]:[{}]", password);
	        if (User.quickAuthenticateAdmin(password) == null) {
	        	Logger.debug("Authentication failed");
	        	return "Invalid user or password";
	        }
	        return null;
	    }
	}
	
	public static class OrderForm {
		@Constraints.Required
		public String orderType;
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

	    public List<ValidationError> validate() {
	    	List<ValidationError> errors = new ArrayList<ValidationError>();
	    	if (doesFieldContainSPAM(model))
	    		errors.add(new ValidationError("model", Messages.get("order.validation.error.model.spam.detected")));
	    	if (doesFieldContainSPAM(remark))
	    		errors.add(new ValidationError("remark", Messages.get("order.validation.error.remark.spam.detected")));
	    	return errors.isEmpty() ? null : errors;
	    }
	    
	    public OrderForm() {
	    	super();
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
		public String movementComplexity;
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
		public String doingSport = "";
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
	        if (doesFieldContainSPAM(model))
	    		errors.add(new ValidationError("model", Messages.get("service.test.validation.error.model.spam.detected")));
	        return errors.isEmpty() ? null : errors;
	    }
	    
	    public ServiceTestForm() {
	    	super();
	    }
	    
	    public ServiceTest getRequest() {
	    	ServiceTest request = new ServiceTest();
	    	request.movementType = ServiceTest.MovementTypes.fromString(this.movementType);
	    	request.movementComplexity = ServiceTest.MovementComplexity.fromString(this.movementComplexity);
	    	request.buildPeriod = ServiceTest.BuildPeriod.fromString(this.buildPeriod);
	    	request.lastServiceYear = ServiceTest.LastServiceYear.fromString(this.lastServiceYear);
	    	request.performanceIssue = ("0".equals(this.performanceIssue))?false:true;
	    	request.powerReserveIssue = ("0".equals(this.powerReserveIssue))?false:true;
	    	request.waterIssue = ("0".equals(this.waterIssue))?false:true;
	    	request.doingSport = ("0".equals(this.doingSport))?false:true;
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
		@Constraints.Required
		@Constraints.Email
		@Constraints.MaxLength(60)
		public String email;
		
	    public List<ValidationError> validate() {
	    	List<ValidationError> errors = new ArrayList<ValidationError>();
	        if (doesFieldContainSPAM(message))
	    		errors.add(new ValidationError("message", Messages.get("contact.validation.error.message.spam.detected")));
	        return errors.isEmpty() ? null : errors;
	    }
	    
	    public ContactForm() {
	    	super();
	    }
	    
	    public ContactForm(String title) {
	    	this();
	    	this.title = title;
	    }
	    
	    public ContactRequest getRequest() {
	    	ContactRequest request = new ContactRequest();
	    	request.title = this.title;
	    	request.message = this.message;
	    	request.name = this.name;
	    	if (this.email != null && !"".equals(this.email))
	    		request.email = this.email;
	    	
	    	return request;
	    }
	}
	
	public static class CallForm {

		@Constraints.Required
		@Formats.NonEmpty
		public String number;
		@Constraints.Required
		@Column(length = 1000)
		@Formats.NonEmpty
		public String reason;
		
	    public List<ValidationError> validate() {
	    	List<ValidationError> errors = new ArrayList<ValidationError>();
	        if (doesFieldContainSPAM(reason))
	    		errors.add(new ValidationError("reason", Messages.get("call.request.validation.error.reason.spam.detected")));
	        return errors.isEmpty() ? null : errors;
	    }
	    
	    public CallForm() {
	    	super();
	    }
	    
	    public String toString() {
	    	StringBuilder content = new StringBuilder();
	    	content.append(this.getClass().getSimpleName() + " : [");
	    	content.append(" Number is : " + this.number);
	    	content.append(", Message is : " + this.reason);
	    	content.append("]");
	    	return content.toString();
	    }
	}
	

    public static Result index() {
        return ok(index.render("", getSupportedBrands(), "", getOneEmphasizableFeedbackRandomly()));
    }

    public static Result indexAlternate() {
        return ok(index.render("", getSupportedBrands(), "alt", getOneEmphasizableFeedbackRandomly()));
    }
    
    public static Result login(String originInQueryString) {
        return ok(login.render(Form.form(LoginForm.class), originToUse(originInQueryString)));
    }
    
    public static Result quickAdminLogin(String originInQueryString) {
        return ok(quick_login.render(Form.form(QuickLoginForm.class), originToUse(originInQueryString)));
    }
    
    private static String originToUse(String originInQueryString) {
    	String originToUse = originInQueryString;
    	if (originToUse == null)
    		originToUse = ActionHelper.getOriginOfCall(ctx());
    	return originToUse;
    }
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
            routes.Application.quickAdminLogin("")
        );
    }
    
    public static Result authenticate() {
        Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
        	Logger.info("Login failed");
            return badRequest(login.render(loginForm, ActionHelper.getOriginOfCall(ctx())));
        } else {
        	String origin = loginForm.get().origin;
            session().clear();
            session("token", loginForm.get().email);
            if (origin != null && !"".equals(origin))
            	return redirect(origin);
            return redirect(
                routes.Admin.index()
            );
        }
    }
    
    public static Result quickAdminAuthenticate() {
        Form<QuickLoginForm> loginForm = Form.form(QuickLoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
        	Logger.info("Login failed");
            return badRequest(quick_login.render(loginForm, ActionHelper.getOriginOfCall(ctx())));
        } else {
        	String origin = loginForm.get().origin;
            session().clear();
            session("token", SecurityHelper.findByQuickPassword(loginForm.get().password).map(user -> user.email).orElse(null));
            if (origin != null && !"".equals(origin))
            	return redirect(origin);
            return redirect(
                routes.Admin.index()
            );
        }
    }
    
    public static Result service() {
        return ok(service.render(""));
    }
    
    public static Result customization() {
        return ok(customization.render(""));
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

    public static Result water() {
        return ok(water.render(""));
    }
    
    public static Result broking() {
        return ok(broking.render(""));
    }
    
    public static Result sell() {
        return ok(sell.render(""));
    }

    public static Result order(String brandName) {
    	try {
    		
	        return ok(order.render("", fillFormWithQueryParams(brandName), getSupportedBrands(), getDisplayableWatches(), SessionWatcher.isItFirstPageOfSession(session())));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result quartz(String brandName) {
    	try {
    		
	        return ok(quartz.render("", Brand.findByInternalName(brandName), SessionWatcher.isItFirstPageOfSession(session())));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result buyRequest() {
    	try {
    		
	        return ok(buy_request.render("", Form.form(BuyRequest.class), getAllBrands(), SessionWatcher.isItFirstPageOfSession(session())));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result contact(String title) {
    	try {
	        return ok(contact.render("", Form.form(ContactForm.class).fill(new ContactForm(title))));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result callRequest() {
    	try {
	        return ok(call.render("", Form.form(CallForm.class).fill(new CallForm())));
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
    
    public static Result feedback() {
        return ok(feedback.render(models.Feedback.findDisplayable()));
    }
    
    public static Result faq() {
        return ok(faq.render());
    }

    public static Result legal() {
        return ok(legal.render());
    }
    
    public static Result privacy() {
        return ok(privacy.render());
    }
    
    public static Result cgv() {
        return ok(cgv.render());
    }
    
    public static Result around() {
        return ok(around.render());
    }

	public static Result manageOrder() {
		Form<OrderForm> orderForm = Form.form(OrderForm.class).bindFromRequest();
		if(orderForm.hasErrors()) {
			logFormErrors(orderForm);
			return badRequest(order.render("", orderForm, getSupportedBrands(), getDisplayableWatches(), SessionWatcher.isItFirstPageOfSession(session())));
		} else {
			OrderRequest orderRequest = orderForm.get().getRequest();
			orderRequest.save();
			
			ActionHelper.tryToSendHtmlEmail("["+orderRequest.id+"] Nouvelle demande de devis", notify_order.render(orderRequest).body().toString());
			
			flash("success", "OK");
			
			flash("facebook_demande_devis", "-");
			
			GoogleAnalyticsHelper.pushEvent("order", "sent", ctx());
			
			return redirect(
					routes.Application.order(orderRequest.brand.seo_name)
					);
		}
	}
	
	public static Result manageBuyRequest() {
		Form<BuyRequest> requestForm = Form.form(BuyRequest.class).bindFromRequest();
		if(requestForm.hasErrors()) {
			logFormErrors(requestForm);
			return badRequest(buy_request.render("", requestForm, getAllBrands(), SessionWatcher.isItFirstPageOfSession(session())));
		} else {
			BuyRequest request = requestForm.get();
			
			request.save();
			
			ActionHelper.tryToSendHtmlEmail("["+request.id+"] Nouvelle recherche de montre", notify_buy_request.render(BuyRequest.findById(request.id)).body().toString());
			
			flash("success", "OK");
			
			GoogleAnalyticsHelper.pushEvent("buy_request", "sent", ctx());
			
			return redirect(
					routes.Application.buyRequest()
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
			
			GoogleAnalyticsHelper.pushEvent("contact", "sent", ctx());
			
			return contact("");
		}
	}
	
	
	public static Result manageCallRequest() {
		Form<CallForm> callForm = Form.form(CallForm.class).bindFromRequest();
		if(callForm.hasErrors()) {
			return badRequest(call.render("", callForm));
		} else {
			ActionHelper.tryToNotifyTeamByEmail("Demande de rappel", callForm.toString());
			
			flash("success", "OK");
			
			GoogleAnalyticsHelper.pushEvent("contact", "sent", ctx());
			
			return callRequest();
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
			
			GoogleAnalyticsHelper.pushEvent("serviceTest", "sent", ctx());
			
			return ok(service_test_result.render("", ServiceTestHelper.whenDoServiceIsRecommended(serviceTest), isCustomisedTestAsked, serviceTest.email));
		}
	}
    
    public static Result watch_detail_byId(Long id) {
    	try {
	    	Watch currentWatch = Watch.findById(id);
	        return ok(watch_detail.render(currentWatch.short_name, currentWatch, Picture.findPicturesForWatch(currentWatch), getDisplayableWatchesExceptOne(currentWatch)));
    	} catch (Exception e) {
    		return badRequest(views.html.error.notfound.render(request().uri(), null));
    	}
    }
    
    public static Result watch_detail(String seoName) {
    	try {
	    	Watch currentWatch = Watch.findBySeoName(seoName);
	    	return watch_detail_byId(currentWatch.id);
    	} catch (Exception e) {
    		return badRequest(views.html.error.notfound.render(request().uri(), null));
    	}
    }

    public static Result sitemap() {
    	return ok(views.xml.sitemap.render(getDisplayableWatches(), getSupportedBrands()));
    }
    
    public static Result siteplan() {
    	return ok(site_plan.render(getDisplayableWatches(), getSupportedBrands()));
    }
    
    public static Result usefull_links() {
    	return ok(usefull_links.render(getDisplayableUsefullLinks()));
    }

    private static List<Watch> getDisplayableWatches() {
    	return Watch.findDisplayable();
    }

    private static List<Watch> getAvailableWatches() {
    	return Watch.findAvailable();
    }

    private static List<Brand> getSupportedBrands() {
    	return Brand.findAllSupportedByAscName();
    }
    
    private static List<Brand> getAllBrands() {
    	return Brand.findAllByAscName();
    }

    private static List<Watch> getDisplayableWatchesExceptOne(Watch currentWatch) {
    	List<Watch> displayableWatches = getDisplayableWatches();
    	if (displayableWatches != null && !displayableWatches.isEmpty())
    		displayableWatches.remove(currentWatch);
    	return displayableWatches;
    }
    
    private static List<UsefullLink> getDisplayableUsefullLinks() {
    	return UsefullLink.findDisplayable();
    }
    
    private static Form<OrderForm> fillFormWithQueryParams(String brandName) {
    	OrderForm orderForm = new OrderForm();
    	if (isBrandParamValid(brandName))
    		orderForm.brand = Brand.findBySeoName(brandName).id.toString();
    	if (isWatchParamFoundAndValid())
    		orderForm.watchChosen = form().bindFromRequest().get("watch");
    	if (isOrderTypeParamFoundAndValid())
    		orderForm.orderType = form().bindFromRequest().get("type");
    	return Form.form(OrderForm.class).fill(orderForm);
    }
    
    private static boolean isWatchParamFoundAndValid() {
    	String valueFound = form().bindFromRequest().get("watch");
    	if (valueFound!= null)
    		return isWatchParamValid(valueFound);
    	return false;
    }
    
    private static boolean isOrderTypeParamFoundAndValid() {
    	String valueFound = form().bindFromRequest().get("type");
    	if (valueFound!= null)
    		return isOrderTypeParamValid(valueFound);
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
    
    private static boolean isOrderTypeParamValid(String value) {
    	try {
	    	if (OrderRequest.OrderTypes.fromString(value) != null)
	    		return true;
	    	return false;
    	} catch (IllegalArgumentException e) {
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
    
    public static Result autoHead(String originalPath) throws IllegalAccessException {
    	Logger.debug("In autoHead with {}", originalPath);
        WSRequestHolder forwardedRequest = WS.url("http://" + request().host() + request().path());
        // this header will allow you to make additional choice i.e. avoid tracking the request or something else
        // see condition in index() action
        forwardedRequest.setHeader("X_FORWARD_FROM_HEAD", "true");

        // Forward original headers
        for (String header : request().headers().keySet()) {
            forwardedRequest.setHeader(header, request().getHeader(header));
        }

        // Forward original queryString
        for (String key : request().queryString().keySet()) {
            for (String val : request().queryString().get(key)) {
                forwardedRequest.setQueryParameter(key, val);
            }
        }

        // Call the same path but with GET
        WSResponse wsResponse = forwardedRequest.get().get(1000L, TimeUnit.MILLISECONDS);

        // Set returned headers to the response
        for (java.lang.reflect.Field f : Http.HeaderNames.class.getFields()) {
            String headerName = f.get(null).toString();
            if (wsResponse.getHeader(headerName) != null) {
                response().setHeader(headerName, wsResponse.getHeader(headerName));
            }
        }

        return status(wsResponse.getStatus());
    }
    
    private static <T> void logFormErrors(Form<T> form) {
		String errorMsg = "";
        java.util.Map<String, List<play.data.validation.ValidationError>> errorsAll = form.errors();
        for (String field : errorsAll.keySet()) {
            errorMsg += field + " ";
            for (ValidationError error : errorsAll.get(field)) {
            	
                errorMsg += error.message() + "["+form.field(field).value()+"] , ";
            }
        }
		Logger.warn("Error in form : "+errorMsg);
    }

    public static boolean forwardedFromHead() {
        return (request().getHeader("X_FORWARD_FROM_HEAD") != null && "true".equals(request().getHeader("X_FORWARD_FROM_HEAD")));
    }
    
    private static Feedback getOneEmphasizableFeedbackRandomly() {
    	return new ListHelper<Feedback>(Feedback.findEmphasizable()).getAnElementRandomly();
    }
}
