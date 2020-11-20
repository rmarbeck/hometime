package controllers;

import static play.data.Form.form;
import static fr.hometime.utils.SecurityHelper.doesFieldContainSPAM;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.persistence.Column;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.hometime.utils.ActionHelper;
import fr.hometime.utils.AppointmentRequestHelper;
import fr.hometime.utils.GoogleAnalyticsHelper;
import fr.hometime.utils.ListHelper;
import fr.hometime.utils.LiveConfigHelper;
import fr.hometime.utils.PhoneNumberHelper;
import fr.hometime.utils.RandomHelper;
import fr.hometime.utils.SecurityHelper;
import fr.hometime.utils.ServiceTestHelper;
import models.AppointmentRequest;
import models.Authentication;
import models.AutoOrder;
import models.Brand;
import models.BuyRequest;
import models.ContactRequest;
import models.Feedback;
import models.IncomingCall;
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
import play.libs.Json;
import play.libs.ws.*;
import play.mvc.*;
import play.twirl.api.Html;
import play.twirl.api.Template2;
import views.html.*;
import views.html.admin.login;
import views.html.admin.customer_login;
import views.html.admin.quick_login;
import views.html.mails.notify_order;
import views.html.mails.notify_buy_request;
import views.html.mails.notify_call_back;
import views.html.mails.notify_contact;

@With(SessionWatcher.class)
public class Application extends Controller {
	public static String FRONT_END_URL = "https://www.hometime.fr";
	public static String APPOINTMENT_VALIDATION_URL = "/a/v/";

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
	
	public static class AppointmentKeyForm {
		@Constraints.Required
	    public String uniqueKey;
	}
	
	public static class AppointmentRequestForm {
		@Constraints.Required
		public String datetimeAsString;
		
		@Constraints.Required
		public String reason;
		
		@Constraints.Required
		public String customerName;
		
		@Constraints.Required
		public String phoneNumber;

		@Constraints.MaxLength(10000)
		public String optionnalMessage;
		
		@Constraints.MaxLength(10000)
		public String privateRemark;

	    public List<ValidationError> validate() {
	    	List<ValidationError> errors = new ArrayList<ValidationError>();
	        if (!PhoneNumberHelper.isItAFrenchMobilePhoneNumber(phoneNumber)) {
	       		errors.add(new ValidationError("phoneNumber", Messages.get("admin.appointment.request.phonenumber.invalid")));
	        }
	        return errors.isEmpty() ? null : errors;
	    }
	    
	    public AppointmentRequestForm() {
	    	super();
	    }
	    
	    public AppointmentRequest getRequest() {
	    	AppointmentRequest newRequest = new AppointmentRequest();
	    	newRequest.appointmentAsString = datetimeAsString;
	    	newRequest.reason = models.AppointmentRequest.Reason.fromString(reason);
	    	newRequest.customerDetails = customerName;
	    	newRequest.customerPhoneNumber = phoneNumber;
	    	newRequest.customerRemark = optionnalMessage;
	    	newRequest.privateRemark = privateRemark;
	    	return newRequest;
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
		
		public String privateInfos;

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
	    	if (this.watchChosen!= null && !"".equals(this.watchChosen))
	    		request.watchChosen =  Watch.findById(Long.valueOf(this.watchChosen));
	    	request.orderType = OrderRequest.OrderTypes.fromString(this.orderType);
	    	request.method = OrderRequest.MethodTypes.fromString(this.method);
	    	request.city = this.city;
	    	request.email = this.email;
	    	request.model = this.model;
	    	request.nameOfCustomer = this.nameOfCustomer;
	    	request.phoneNumber = this.phoneNumber;
	    	request.remark = this.remark;
	    	request.privateInfos = this.privateInfos;
	    	
	    	return request;
	    }
	}

	public static class AutoOrderForm {
		@Constraints.Required
		public String brand;
		
		public String movementType;
		
		public String movementComplexity;
		
		public String workingCondition;
		
		public String emergencyLevel;
		
		public String proposal;

	    public List<ValidationError> validate() {
	    	List<ValidationError> errors = new ArrayList<ValidationError>();
	        return errors.isEmpty() ? null : errors;
	    }
	    
	    public AutoOrderForm() {
	    	super();
	    }
	    
	    public AutoOrder getRequest() {
	    	AutoOrder request = new AutoOrder();
	    	request.brand = Brand.findBySeoName(this.brand);
	    	request.movementType = AutoOrder.MovementTypes.fromString(this.movementType);
	    	request.movementComplexity = AutoOrder.MovementComplexity.fromString(this.movementComplexity);
	    	request.workingCondition = AutoOrder.WorkingCondition.fromString(this.workingCondition);
	    	request.emergencyLevel = AutoOrder.EmergencyLevel.fromString(this.emergencyLevel);

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
	    
	    public CallForm(String reason) {
	    	super();
	    	if (reason != null && reason != "")
	    		this.reason = reason;
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
	
	public static class AcceptForm {

		@Constraints.Required
		@Formats.NonEmpty
		public Long orderId;
		@Constraints.Required
		@Formats.NonEmpty
		public String price;
		@Constraints.Required
		@Formats.NonEmpty
		public String delay;
		@Constraints.Required
		@Formats.NonEmpty
		public String address;
		
	    public List<ValidationError> validate() {
	    	List<ValidationError> errors = new ArrayList<ValidationError>();
	        if (doesFieldContainSPAM(address))
	    		errors.add(new ValidationError("address", Messages.get("accept.quotation.validation.error.reason.spam.detected")));
	        return errors.isEmpty() ? null : errors;
	    }
	    
	    public AcceptForm() {
	    	super();
	    }
	    
	    public AcceptForm(Long orderId, String price, String delay) {
	    	this.orderId = orderId;
	    	this.price = price;
	    	this.delay = delay;
	    }
	    
	    public String toString() {
	    	StringBuilder content = new StringBuilder();
	    	content.append(this.getClass().getSimpleName() + " : [");
	    	content.append(" Order ID is : " + this.orderId);
	    	content.append(", Price is : " + this.price);
	    	content.append(", Delay is : " + this.delay);
	    	content.append(", address is : " + this.address);
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
    	return logger(login.ref(), Form.form(LoginForm.class), originInQueryString);
    }
    
    public static Result customerLogin(String originInQueryString) {
        return logger(customer_login.ref(), Form.form(LoginForm.class), originInQueryString);
    }
    
    public static Result quickAdminLogin(String originInQueryString) {
    	return logger(quick_login.ref(), Form.form(QuickLoginForm.class), originInQueryString);
    }
    
    private static <A> Result logger(Template2<Form<A>, String, Html> loginview, Form<A> form, String originInQueryString) {
    	return ok(loginview.render(form, originToUse(originInQueryString)));
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
    
    
    public static Result customerLogout() {
        session().clear();
        flash("success", "Déconnexion réussie");
        return redirect(
            routes.Application.customerLogin("")
        );
    }
    
    public static Result authenticate() {
    	return authenticator(routes.Admin.index(), login.ref());
    }
    
    public static Result customerAuthenticate() {
    	return authenticator(routes.CustomerAdmin.index(), customer_login.ref());
    }
    
    private static Result authenticator(Call successCall, Template2<Form<LoginForm>, String , Html> loginPage) {
    	Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
        	Logger.info("Mauvais login/mot de passe !");
            return badRequest(loginPage.render(loginForm, ActionHelper.getOriginOfCall(ctx())));
        } else {
        	String origin = loginForm.get().origin;
            session().clear();
            session("token", loginForm.get().email);
            if (origin != null && !"".equals(origin))
            	return redirect(origin);
            return redirect(
            		successCall
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
    
    public static Result watchmakers() {
        return ok(watchmakers.render(""));
    }
    
    public static Result news() {
        return ok(news.render(""));
    }
    
    public static Result content_rolex() {
        return ok(content_rolex_repairing.render(""));
    }
    
    public static Result content_omega() {
        return ok(content_omega_repairing.render(""));
    }
    
    public static Result content_paris() {
        return ok(content_paris_repairing.render(""));
    }
    
    public static Result content_authentication() {
        return ok(content_authentication.render(""));
    }

    public static Result more() {
        return ok(more.render(""));
    }

    public static Result prices() {
        return ok(prices.render(""));
    }
    
    public static Result quotation_choice() {
        return ok(quotation_choice.render(""));
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
    

    public static Result orderAuto(String brandName) {
    	try {
    		
	        return ok(auto_order.render("", fillAutoFormWithQueryParams(brandName), getSupportedBrands(), getDisplayableWatches(), SessionWatcher.isItFirstPageOfSession(session())));
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
    
    public static Result quartz_en(String brandName) {
    	return switchInEnglish(Application::quartz, brandName);
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
    
    public static Result appointment() {
    	try {
	        return ok(appointment.render("", SessionWatcher.isItFirstPageOfSession(session())));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result appointment_en() {
	    return switchInEnglish(Application::appointment);
    }
    
    public static Result callRequest(String reason) {
    	try {
	        return ok(call.render("", Form.form(CallForm.class).fill(new CallForm(reason))));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result callRequest_en(String reason) {
	    return switchInEnglish(Application::callRequest, reason);
    }
    
    private static Result switchInEnglish(Supplier<Result> supplier) {
    	ctx().changeLang("en");
   		Result result = supplier.get();
    	ctx().clearLang();
	    return result;
    }
    
    private static Result switchInEnglish(Function<String, Result> function, String value) {
    	ctx().changeLang("en");
   		Result result = function.apply(value);
    	ctx().clearLang();
	    return result;
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
			OrderRequest orderRequest = manageOrderForm(orderForm);
			
			flash("success", "OK");
			
			flash("facebook_demande_devis", "-");
			
			GoogleAnalyticsHelper.pushEvent("order", "sent", ctx());
			
			return redirect(
					routes.Application.order(orderRequest.brand.seo_name)
					);
		}
	}
	
	public static Result manageOrderFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
			Form<OrderForm> orderForm = Form.form(OrderForm.class).bindFromRequest();
			if(orderForm.hasErrors()) {
				logFormErrors(orderForm);
				return badRequest();
			} else {
				manageOrderForm(orderForm);
				return ok();
			}
		});
	}
	
	private static Result doIfComesFromFriendlyLocation(Supplier<Result> toDo) {
		if (Webservices.isAuthorized()) {
			return toDo.get();
		} else {
			return unauthorized();
		}
	}
	
	private static OrderRequest manageOrderForm(Form<OrderForm> orderForm) {
		OrderRequest orderRequest = orderForm.get().getRequest();
		orderRequest.save();
		
		ActionHelper.asyncTryToSendHtmlEmail("["+orderRequest.id+"] Nouvelle demande de devis", notify_order.render(orderRequest).body().toString());
		return orderRequest;
	}
	
	public static Result manageAutoOrder() {
		Form<AutoOrderForm> autoOrderForm = Form.form(AutoOrderForm.class).bindFromRequest();
		
		String brandId = autoOrderForm.get().brand;
		
		return order(Brand.findById(Long.valueOf(brandId)).seo_name);

	}
	
	public static Result manageBuyRequest() {
		Form<BuyRequest> requestForm = Form.form(BuyRequest.class).bindFromRequest();
		if(requestForm.hasErrors()) {
			logFormErrors(requestForm);
			return badRequest(buy_request.render("", requestForm, getAllBrands(), SessionWatcher.isItFirstPageOfSession(session())));
		} else {
			manageBuyRequestForm(requestForm);
			
			GoogleAnalyticsHelper.pushEvent("buy_request", "sent", ctx());
			
			return redirect(
					routes.Application.buyRequest()
					);
		}
	}
	
	public static Result manageBuyRequestFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
			Form<BuyRequest> requestForm = Form.form(BuyRequest.class).bindFromRequest();
			if(requestForm.hasErrors()) {
				logFormErrors(requestForm);
				return badRequest();
			} else {
				manageBuyRequestForm(requestForm);
				return ok();
			}
		});
	}
	
	private static BuyRequest manageBuyRequestForm(Form<BuyRequest> requestForm) {
		BuyRequest request = requestForm.get();
		
		request.save();
		
		ActionHelper.asyncTryToSendHtmlEmail("["+request.id+"] Nouvelle recherche de montre", notify_buy_request.render(BuyRequest.findById(request.id)).body().toString());
		
		return request;
	}
	
	
	
	public static Result manageContact() {
		Form<ContactForm> contactForm = Form.form(ContactForm.class).bindFromRequest();
		if(contactForm.hasErrors()) {
			return badRequest(contact.render("", contactForm));
		} else {
			manageContactForm(contactForm);
			
			flash("success", "OK");
			
			GoogleAnalyticsHelper.pushEvent("contact", "sent", ctx());
			
			return contact("");
		}
	}
	
	public static Result manageContactFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
			Form<ContactForm> contactForm = Form.form(ContactForm.class).bindFromRequest();
			if(contactForm.hasErrors()) {
				return badRequest();
			} else {
				manageContactForm(contactForm);
				return ok();
			}
		});
	}
	
	private static ContactRequest manageContactForm(Form<ContactForm> contactForm) {
		ContactRequest contactRequest = contactForm.get().getRequest();
		contactRequest.save();

		ActionHelper.asyncTryToNotifyTeamByEmail("Prise de contact : "+contactRequest.title, notify_contact.render(contactForm).body().toString());
		return contactRequest;
	}
		
	
	public static Result manageCallRequest() {
		Form<CallForm> callForm = Form.form(CallForm.class).bindFromRequest();
		if(callForm.hasErrors()) {
			return badRequest(call.render("", callForm));
		} else {
			ActionHelper.asyncTryToNotifyTeamByEmail("Demande de rappel", getCallRequestMessage(callForm));
			
			flash("success", "OK");
			
			GoogleAnalyticsHelper.pushEvent("contact", "sent", ctx());
			
			return callRequest("");
		}
	}
	
	public static Result manageCallRequestFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
			Form<CallForm> callForm = Form.form(CallForm.class).bindFromRequest();
			if(callForm.hasErrors()) {
				return badRequest();
			} else {
				ActionHelper.asyncTryToNotifyTeamByEmail("Demande de rappel", notify_call_back.render(callForm).body().toString());
			
				return ok();
			}
		});
	}
	
		
	private static String getCallRequestMessage(Form<CallForm> callForm) {
		StringBuilder message = new StringBuilder();
		message.append("\n");
		message.append("Numéro de téléphone : ");
		message.append(callForm.get().number);
		message.append("\n\n");
		message.append("Motif de la demande : ");
		message.append(callForm.get().reason);
		return message.toString();
	}
	
	
	public static Result manageAcceptQuotation() {
		Form<AcceptForm> acceptForm = Form.form(AcceptForm.class).bindFromRequest();
		if(acceptForm.hasErrors()) {
			return badRequest(accept_quotation.render("", acceptForm));
		} else {
			manageAcceptQuotationForm(acceptForm);
			
			flash("success", "OK");
			
			GoogleAnalyticsHelper.pushEvent("contact", "sent", ctx());
			
			return callRequest("");
		}
	}
	
	public static Result manageAcceptQuotationRequestFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
		Form<AcceptForm> acceptForm = Form.form(AcceptForm.class).bindFromRequest();
			if(acceptForm.hasErrors()) {
				return badRequest();
			} else {
				manageAcceptQuotationForm(acceptForm);
			
				return ok();
			}
		});
	}
	
	private static void manageAcceptQuotationForm(Form<AcceptForm> acceptForm) {
		ActionHelper.asyncTryToNotifyTeamByEmail("Devis accepté", acceptForm.toString());
	}
	
	
	public static Result manageAppointmentRequestFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
		Form<AppointmentRequestForm> appointmentForm = Form.form(AppointmentRequestForm.class).bindFromRequest();
			if(appointmentForm.hasErrors()) {
				return badRequest();
			} else {
				AppointmentRequest newRequest = appointmentForm.get().getRequest();
				newRequest.save();
				SMS.sendSMS(newRequest.customerPhoneNumber, Messages.get("sms.appointment.to.validate", FRONT_END_URL+APPOINTMENT_VALIDATION_URL, newRequest.uniqueKey));
				return ok(getAppointmentAsJsonNode(newRequest));
			}
		});
	}
	
	public static Result validateAppointment(String uniqueKey) {
		return manageAppointment(uniqueKey, Application::validateAppointmentAndSendSMS);
	}
	
	public static Result cancelAppointment(String uniqueKey) {
		return manageAppointment(uniqueKey, Application::cancelAppointmentAndSendSMS);
	}
	
	public static Result validateAppointmentFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
			Form<AppointmentKeyForm> appointmentForm = Form.form(AppointmentKeyForm.class).bindFromRequest();
				if(appointmentForm.hasErrors()) {
					return badRequest();
				} else {
					return manageAppointment(appointmentForm.get().uniqueKey, Application::validateAppointmentAndSendSMS);
				}
			});
	}
	
	public static Result cancelAppointmentFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
			Form<AppointmentKeyForm> appointmentForm = Form.form(AppointmentKeyForm.class).bindFromRequest();
				if(appointmentForm.hasErrors()) {
					return badRequest();
				} else {
					return manageAppointment(appointmentForm.get().uniqueKey, Application::cancelAppointmentAndSendSMS);
				}
			});
	}
	
	private static Optional<ObjectNode> validateAppointmentAndSendSMS(String uniqueKey) {
		boolean alreadyValid = isItValidated(uniqueKey);
		Optional<AppointmentRequest> appointment = AppointmentRequestHelper.validate(uniqueKey);
		if (isItValidated(appointment)) {
			if (!alreadyValid) {
				SMS.sendSMS(appointment.get().customerPhoneNumber, Messages.get("sms.appointment.just.validated", appointment.get().getNiceDisplayableDatetime()));
				ActionHelper.asyncTryToNotifyTeamByEmail("Rendez-vous confirmé", getAppointementValidationMessage(appointment.get()));
			}
			return Optional.of(getAppointmentAsJsonNode(appointment.get()));
		}
		return Optional.empty();
	}
	
	private static String getAppointementValidationMessage(AppointmentRequest appointment) {
		StringBuilder message = new StringBuilder();
		message.append("\n");
		message.append("Créneau : ");
		message.append(appointment.getNiceDisplayableDatetime());
		message.append("\n\n");
		message.append("Client : ");
		message.append(appointment.customerDetails);
		message.append("\n\n");
		message.append("Motif de la demande : ");
		message.append(appointment.reason);
		message.append("\n\n");
		message.append("Remarque : ");
		message.append(appointment.customerRemark);
		return message.toString();
	}
	
	private static boolean isItValidated(String uniqueKey) {
		return isItValidated(AppointmentRequestHelper.findByKey(uniqueKey));
	}
	
	private static boolean isItValidated(Optional<AppointmentRequest> appointment) {
		return appointment.filter(AppointmentRequest::isValidated).map(x -> true).orElse(false);
	}
	
	private static Optional<ObjectNode> cancelAppointmentAndSendSMS(String uniqueKey) {
		boolean alreadyCanceled = isItCanceled(uniqueKey);
		Optional<AppointmentRequest> appointment = AppointmentRequestHelper.cancel(uniqueKey);
		if (appointment.isPresent() && appointment.get().isCanceled()) {
			if (!alreadyCanceled) {
				SMS.sendSMS(appointment.get().customerPhoneNumber, Messages.get("sms.appointment.just.canceled"));
				ActionHelper.asyncTryToNotifyTeamByEmail("Rendez-vous annulé", getAppointementValidationMessage(appointment.get()));
			}
			return Optional.of(getAppointmentAsJsonNode(appointment.get()));
		}
		return Optional.empty();
	}
	
	private static boolean isItCanceled(String uniqueKey) {
		Optional<AppointmentRequest> appointment = AppointmentRequestHelper.findByKey(uniqueKey);
		return appointment.isPresent() && appointment.get().isCanceled();
	}
	
	private static Result manageAppointment(String uniqueKey, Function<String, Optional<ObjectNode>> toDo) {
		Optional<ObjectNode> result = toDo.apply(uniqueKey);
		if (result.isPresent())
			return ok(result.get());
		return badRequest();
	}
	
	private static ObjectNode getAppointmentAsJsonNode(AppointmentRequest appointment) {
		ObjectNode resultAsJson = Json.newObject();
		resultAsJson.put("appointment", appointment.appointmentAsString);
		resultAsJson.put("customerDetails", appointment.customerDetails);
		resultAsJson.put("customerPhoneNumber", appointment.customerPhoneNumber);
		resultAsJson.put("uniqueKey", appointment.uniqueKey);
		resultAsJson.put("status", appointment.status.name());
		resultAsJson.put("reason", appointment.reason.name());
		resultAsJson.put("appointmentNiceToDisplay", appointment.getNiceDisplayableDatetime());
		return resultAsJson;
	}
	
	/*
	
	private static void manageAppointmentForm(AppointmentRequestForm appointmentForm) {
		
	}

	private static boolean validateAppointmentForm(String appointmentKey) {
		
	}

	private static boolean cancelAppointmentForm(String appointmentKey) {
	
	}
	*/
		
	
	
	public static Result manageServiceTest() {
		Form<ServiceTestForm> serviceTestForm = Form.form(ServiceTestForm.class).bindFromRequest();
		if(serviceTestForm.hasErrors()) {
			return badRequest(service_test.render("", serviceTestForm));
		} else {
			ServiceTest serviceTest = manageServiceTestForm(serviceTestForm);
			
			GoogleAnalyticsHelper.pushEvent("serviceTest", "sent", ctx());
			
			return ok(service_test_result.render("", ServiceTestHelper.whenDoServiceIsRecommended(serviceTest), isCustomizationAsked(serviceTest), serviceTest.email));
		}
	}
	
	public static Result manageServiceTestRequestFromFriendlyLocation() {
		return doIfComesFromFriendlyLocation(() -> {
			Form<ServiceTestForm> serviceTestForm = Form.form(ServiceTestForm.class).bindFromRequest();
			if(serviceTestForm.hasErrors()) {
				return badRequest();
			} else {
				ServiceTest serviceTest = manageServiceTestForm(serviceTestForm);
				
				ObjectNode resultAsJson = Json.newObject();
				resultAsJson.put("ServiceTestResult", ServiceTestHelper.whenDoServiceIsRecommended(serviceTest).toString());
				resultAsJson.put("IsCustomizationAsked", isCustomizationAsked(serviceTest)?"1":"0");
				resultAsJson.put("CustomerEmail", serviceTest.email);
				
				return ok(resultAsJson);
			}
		});
	}
	
	private static boolean isCustomizationAsked(ServiceTest serviceTest) {
		return serviceTest.email != null && !"".equals(serviceTest.email);
	}
	
	private static ServiceTest manageServiceTestForm(Form<ServiceTestForm> serviceTestForm) {
		ServiceTest serviceTest = serviceTestForm.get().getRequest();
		boolean isCustomisedTestAsked = isCustomizationAsked(serviceTest); 
		if (isCustomisedTestAsked) {
			serviceTest.save();
			ActionHelper.tryToNotifyTeamByEmail("Evaluation personnalisée", serviceTest.toString());
		}
		return serviceTest;
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
    
    public static Result newIncomingCall(String secret, String phoneNumber, boolean missed) {
    	if (LiveConfigHelper.isNotifyingAuthorized(secret)) {
	    	IncomingCall call = new IncomingCall(phoneNumber, missed);
	    	call.save();
	    	return ok();
    	}
    	return unauthorized();
    }

    public static Result sitemap() {
    	return ok(views.xml.sitemap.render(getDisplayableWatches(), getSupportedBrands()));
    }
    
    public static Result siteplan() {
    	return ok(site_plan.render(getDisplayableWatches(), getSupportedBrands()));
    }
    
    public static Result checkAuthentication(Long id1, Long id2, Long id3) {
    	Authentication authentication = Authentication.findEagerById(id1);
    	if (authentication != null) {
    		if (authentication.watch.retrieveId().equals(id2) && authentication.watch.customer.retrieveId().equals(id3))
    			return ok(check_auth_result.render(authentication));
    	}
    	return ok("Certificat inconnu");
    }
    
    public static Result checkAuthenticationFromFriendlyLocation(Long id1, Long id2, Long id3) {
    	return doIfComesFromFriendlyLocation(() -> {
	    	Authentication authentication = Authentication.findEagerById(id1);
	    	if (authentication != null) {
	    		if (authentication.watch.retrieveId().equals(id2) && authentication.watch.customer.retrieveId().equals(id3)) {
	    			ObjectNode resultAsJson = Json.newObject();
	    			resultAsJson.put("result", authentication.watch.isAuthentic);
					resultAsJson.put("brand", authentication.watch.brand);
					resultAsJson.put("model", authentication.watch.model);
					resultAsJson.put("reference", authentication.watch.reference);
					resultAsJson.put("serial", authentication.watch.serial);
					resultAsJson.put("date", authentication.documentDate.getTime());
					
					return ok(resultAsJson);
	    		}
	    	}
	    	return notFound("Certificat inconnu");
    	});
    }
    
    public static Result acceptQuotation(Long orderId, String price, String delay) {
    	try {
	        return ok(accept_quotation.render("", Form.form(AcceptForm.class).fill(new AcceptForm(orderId, price, delay))));
    	} catch (Exception e) {
    		return internalServerError();
    	}
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
    	if (isPrivateInfosParamFoundAndValid())
    		orderForm.privateInfos = form().bindFromRequest().get("privateInfos");
    	return Form.form(OrderForm.class).fill(orderForm);
    }
    
    private static Form<AutoOrderForm> fillAutoFormWithQueryParams(String brandName) {
    	AutoOrderForm orderForm = new AutoOrderForm();
    	if (isBrandParamValid(brandName))
    		orderForm.brand = Brand.findBySeoName(brandName).id.toString();
    	return Form.form(AutoOrderForm.class).fill(orderForm);
    }
    
    private static boolean isWatchParamFoundAndValid() {
    	String valueFound = form().bindFromRequest().get("watch");
    	if (valueFound!= null)
    		return isWatchParamValid(valueFound);
    	return false;
    }
    
    private static boolean isPrivateInfosParamFoundAndValid() {
    	String valueFound = form().bindFromRequest().get("privateInfos");
    	if (valueFound!= null)
    		return true;
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
    
    public static Result test_new() {
    	return ok(test_new.render("toto"));
    }
    
    public static Result test_canvas() {
    	return ok(test_canvas.render("toto"));
    }
}
