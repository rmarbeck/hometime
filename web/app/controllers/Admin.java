package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Brand;
import models.BuyRequest;
import models.Customer;
import models.ExternalDocument;
import models.Order;
import models.OrderRequest;
import models.OrderRequest.OrderTypes;
import models.PresetQuotationForBrand;
import models.Quotation;
import models.ServiceTest;
import models.ServiceTest.TestResult;
import models.Watch;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.admin.order_request;
import views.html.admin.order_requests;
import views.html.admin.buy_request;
import views.html.admin.buy_requests;
import views.html.admin.orders;
import views.html.admin.index;
import views.html.admin.quotation;
import views.html.admin.quotation_form;
import views.html.admin.service_test;
import views.html.admin.service_tests;
import views.html.mails.notify_order;
import views.html.mails.notify_buy_request;
import fr.hometime.utils.MailjetAdapter;
import fr.hometime.utils.ServiceTestHelper;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Admin extends Controller {
	
	private final static String AUTRE_MARQUE_SEO_NAME = "autre-marque-de-montres";
	private final static int CLOSE_ORDER_REQUEST = 1;
	private final static int SET_REPLIED_ORDER_REQUEST = 2;
	
	public static class QuotationForm {
		@Constraints.Required
		public String serviceType;
		@Constraints.Required
		public String typeOfNetwork;
		@Constraints.Required
		public String watch;
		@Constraints.Required
		public String brand;

		public String customerEmail = null;
		public String customerCity = null;
		public String watchChosen = null;
		@Constraints.Required
		public String delay;
		
		public String delayBrand1 = null;
		
		public String delayBrand2 = null;
		
		public String delayReturn = null;
		@Constraints.Required
		public String price;
		
		public String priceService = null;
		
		public String priceLoan = null;
		@Constraints.Required		
		public String warantyTime;
		@Constraints.Required
		public String priceIsNotFinal = "";
		@Constraints.Required
		public String delayIsNotSure = "";
		@Constraints.Required
		public String outsideZone = "";
		@Constraints.Required
		public String delayCanBeReduced = "1";
		
		public String infosGivenByCustomer1 = null;
		public String infosGivenByCustomer2 = null;
		public String infosGivenByCustomer3 = null;
		
		public String remark1 = null;
		public String remark2 = null;
		public String remark3 = null;
		
		public String partnerAsset = null;
		
		public String hypothesis1 = null;
		public String hypothesis2 = null;
		public String hypothesis3 = null;
		
		public String action = null;

	    public List<ValidationError> validate() {
	    	List<ValidationError> errors = new ArrayList<ValidationError>();
	        if (watchChosen != null && watchChosen.length() > 0) {
	        	if (priceLoan == null || "".equals(priceLoan))
	        		errors.add(new ValidationError("priceLoan", Messages.get("admin.test.validation.error.price.loan.mandatory")));
	        	if (priceService == null || "".equals(priceService))
	        		errors.add(new ValidationError("priceService", Messages.get("admin.test.validation.error.price.service.mandatory")));
	        }
	        return errors.isEmpty() ? null : errors;
	    }
	    
	    public QuotationForm() {
	    	super();
			hypothesis1 = Messages.get("admin.quotation.default.hypothesis.1");
			hypothesis2 = Messages.get("admin.quotation.default.hypothesis.2");
	    }
	    
	    public QuotationForm(OrderRequest order, long presetId, boolean inNetworkIfPossible) {
	    	this();
	    	this.serviceType = order.orderType.toString();
	    	this.brand = order.brand.display_name;
	    	if (!order.model.toLowerCase().contains(order.brand.display_name.toLowerCase())) {
	    		this.watch = order.brand.display_name + " " + order.model;
	    	} else {
	    		this.watch = order.model;
	    	}
	    	if (order.watchChosen != null) {
	    		this.watchChosen = order.watchChosen.id.toString();
	    		this.priceLoan = Messages.get("admin.order.price.loan", order.watchChosen.price.intValue());
	    	}
	    	this.customerEmail = order.email;
	    	this.customerCity = order.city;
	    	this.infosGivenByCustomer1 = order.remark;
	    	switch(order.method) {
	    		case BRAND:
	    			this.typeOfNetwork = Quotation.TypesOfNetwork.IN_ONLY.toString();
	    			break;
				case OUTLET:
					this.typeOfNetwork = Quotation.TypesOfNetwork.OUT_ONLY.toString();
					break;
				default:
					if (inNetworkIfPossible) {
						this.typeOfNetwork = Quotation.TypesOfNetwork.IN_BOTH.toString();
					} else {
						this.typeOfNetwork = Quotation.TypesOfNetwork.OUT_BOTH.toString();
					}
					break;
	    	}
	    	if (this.typeOfNetwork.equals(Quotation.TypesOfNetwork.OUT_BOTH.toString()) || this.typeOfNetwork.equals(Quotation.TypesOfNetwork.OUT_ONLY.toString()))
	    		if (this.brand != null)
	    			this.partnerAsset = Messages.get("admin.quotation.default.partner.asset", this.brand);
	    	this.fillWithPresetFields(presetId, order);
	    }
	    
	    public Quotation getQuotation() {
	    	Quotation quotation = new Quotation();
	    	quotation.serviceType = OrderTypes.fromString(this.serviceType);
	    	quotation.typeOfNetwork = Quotation.TypesOfNetwork.fromString(this.typeOfNetwork);
	    	quotation.customerEmail = this.customerEmail;
	    	quotation.customerCity = this.customerCity;
	    	quotation.brand = this.brand;
    		quotation.watch = this.watch;
	    	
	    	if (!"".equals(this.watchChosen)) {
	    		quotation.watchChosen = Watch.findById(Long.valueOf(this.watchChosen)).brand + " " + Watch.findById(Long.valueOf(this.watchChosen)).full_name;
	    	} else {
	    		quotation.watchChosen = null;
	    	}
	    	quotation.delay = this.delay;
	    	quotation.delayBrand1 = this.delayBrand1;
	    	quotation.delayBrand2 = this.delayBrand2;
	    	quotation.delayReturn = this.delayReturn;
	    	quotation.price = this.price;
	    	quotation.priceService = this.priceService;
	    	quotation.priceLoan = this.priceLoan;
	    	quotation.warantyTime = this.warantyTime;
	    	quotation.priceIsNotFinal = ("0".equals(this.priceIsNotFinal))?false:true;
	    	quotation.delayIsNotSure = ("0".equals(this.delayIsNotSure))?false:true;
	    	quotation.outsideZone = ("0".equals(this.outsideZone))?false:true;
	    	quotation.delayCanBeReduced = ("0".equals(this.delayCanBeReduced))?false:true;
	    	quotation.infosGivenByCustomer1 = getStringValue(this.infosGivenByCustomer1);
	    	quotation.infosGivenByCustomer2 = getStringValue(this.infosGivenByCustomer2);
	    	quotation.infosGivenByCustomer3 = getStringValue(this.infosGivenByCustomer3);
	    	quotation.remark1 = getStringValue(this.remark1);
	    	quotation.remark2 = getStringValue(this.remark2);
	    	quotation.remark3 = getStringValue(this.remark3);
	    	quotation.partnerAsset = getStringValue(this.partnerAsset);
	    	quotation.hypothesis1 = getStringValue(this.hypothesis1);
	    	quotation.hypothesis2 = getStringValue(this.hypothesis2);
	    	quotation.hypothesis3 = getStringValue(this.hypothesis3);
	    	return quotation;
	    }
	    
	    private void fillWithPresetFields(long presetId, OrderRequest order) {
	    	if (presetExists(presetId)) {
	    		PresetQuotationForBrand presetFound = PresetQuotationForBrand.findById(presetId);
	    		this.delay = presetFound.delay;
	    		this.delayBrand1 = presetFound.delayBrand1;
	    		this.delayBrand2 = presetFound.delayBrand2;
	    		this.delayReturn = presetFound.delayReturn;
	    		int watchChosenPrice = 0;
    			if (order.watchChosen != null)
    				watchChosenPrice = order.watchChosen.price.intValue();

	    		if (presetFound.priceServiceHighBound > presetFound.priceServiceLowBound) {
	    			this.priceService = Messages.get("admin.order.priceService.unfixed", presetFound.priceServiceLowBound, presetFound.priceServiceHighBound);
	    			this.price = Messages.get("admin.order.price.unfixed", presetFound.priceServiceLowBound + watchChosenPrice, presetFound.priceServiceHighBound + watchChosenPrice);
	    		} else {
	    			this.priceService = Messages.get("admin.order.priceService.fixed", presetFound.priceServiceLowBound);
    				this.price = Messages.get("admin.order.price.fixed", presetFound.priceServiceLowBound + watchChosenPrice);
	    		}
	    		this.priceIsNotFinal = presetFound.priceIsNotFinal?"1":"0";
	    		this.delayCanBeReduced = presetFound.delayCanBeReduced?"1":"0";
	    		this.delayIsNotSure = presetFound.delayIsNotSure?"1":"0";
	    		this.warantyTime = presetFound.warantyTime;
	    		this.hypothesis1 = presetFound.hypothesis1;
	    		this.hypothesis2 = presetFound.hypothesis2;
	    		this.hypothesis3 = presetFound.hypothesis3;
	    		this.remark1 = presetFound.remark1;
	    		this.remark2 = presetFound.remark2;
	    		this.remark3 = presetFound.remark3;
	    	}
	    }
	}
	
	public static Result index() {
		return ok(index.render("", Customer.findWithOpenTopic(), OrderRequest.findAllUnReplied(), BuyRequest.findAllUnReplied()));
    }
	
	public static Result INDEX = redirect(
			routes.Admin.index()
			);
	
	public static Result LIST_ORDERS = redirect(
			routes.Admin.displayOrderRequests(0, "requestDate", "desc", "")
			);
	
	public static Result displayOrderRequests(int page, String sortBy, String order, String filter) {
        return ok(order_requests.render(OrderRequest.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result displayOrderRequest(long id) {
		if (orderIsValid(id))
			return ok(order_request.render(	OrderRequest.findById(id),
									PresetQuotationForBrand.findByBrand(OrderRequest.findById(id).brand),
									PresetQuotationForBrand.findByBrand(Brand.findBySeoName(AUTRE_MARQUE_SEO_NAME))
								  ));
		flash("error", "Unknown id");
		return LIST_ORDERS;
    }
	
	public static Result closeOrderRequest(long id) {
		return updateOrderRequest(id, CLOSE_ORDER_REQUEST);
    }
	
	public static Result setRepliedOrderRequest(long id) {
		return updateOrderRequest(id, SET_REPLIED_ORDER_REQUEST);
    }
	
	private static Result updateOrderRequest(long id, int action) {
		if (orderIsValid(id)) {
			switch(action) {
			case CLOSE_ORDER_REQUEST:
				OrderRequest.findById(id).close().update();
				break;
			case SET_REPLIED_ORDER_REQUEST:
				OrderRequest.findById(id).replied().update();
				break;
			}
			return INDEX;
		}
		flash("error", "Unknown id");
		return INDEX;
	}
	
	public static Result displayMail(long id) {
		if (orderIsValid(id))
			return ok(notify_order.render(OrderRequest.findById(id)));
		flash("error", "Unknown id");
		return LIST_ORDERS;
    }
	
	public static Result LIST_SERVICE_TESTS = redirect(
			routes.Admin.displayServiceTests(0, "requestDate", "desc", "")
			);
	
	public static Result displayServiceTests(int page, String sortBy, String order, String filter) {
        return ok(service_tests.render(ServiceTest.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result displayServiceTest(long id) {
		if (serviceTestIsValid(id)) {
			ServiceTest serviceTestFound = ServiceTest.findById(id);
			TestResult result = ServiceTestHelper.whenDoServiceIsRecommended(serviceTestFound);
			
			return ok(service_test.render(serviceTestFound, result));
		}
		flash("error", "Unknown id");
		return LIST_SERVICE_TESTS;
    }
	
    public static Result prepareQuotation() {
    	try {
	        return ok(quotation_form.render(Form.form(QuotationForm.class).fill(new QuotationForm()), getAvailableWatches()));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result prepareQuotationFromOrder(long orderId, boolean inNetworkIfPossible) {
    	return prepareQuotationFromOrderWithPreset(orderId, (long) -1, inNetworkIfPossible);
    }
    
    public static Result prepareQuotationFromOrderWithPreset(long orderId, long presetId, boolean inNetworkIfPossible) {
    	if (orderIsValid(orderId))
    		return ok(quotation_form.render(Form.form(QuotationForm.class).fill(new QuotationForm(OrderRequest.findById(orderId), presetId, inNetworkIfPossible)), getAvailableWatches()));
    	flash("error", "Unknown id");
    	return prepareQuotation();
    }

	public static Promise<Result> manageQuotation() {
		final Form<QuotationForm> quotationForm = Form.form(QuotationForm.class).bindFromRequest();
		Logger.debug("Managing Quotation");
		if(quotationForm.hasErrors()) {
			Logger.debug("Error in form : {}", quotationForm.errors());
			return Promise.pure((Result) badRequest(quotation_form.render(quotationForm, getAvailableWatches())));
		} else {
			if ("preview".equals(quotationForm.get().action))
				return displayQuotation(quotationForm.get().getQuotation());
			if ("edit".equals(quotationForm.get().action))
				return editQuotation(quotationForm.get().getQuotation());
			return sendQuotation(quotationForm);
		}
	}
	
	
	private static Promise<Result> displayQuotation(Quotation quotationFilled) {
		Logger.debug("Displaying Quotation");
		return Promise.pure(ok(quotation.render(quotationFilled)));
    }
    
	private static Promise<Result> editQuotation(Quotation quotationFilled) {
		Logger.debug("Editing Quotation");
		return Promise.pure(ok(quotation.render(quotationFilled)));
    }

	private static Promise<Result> sendQuotation(final Form<QuotationForm> quotationForm) {
		Logger.debug("Sending Quotation");
		try {
			final Quotation quotationFilled = quotationForm.get().getQuotation();
			String subject = getSubjectFromQuotation(quotationFilled);
			String title = getTitleFromQuotation(quotationFilled);
			String email = quotationFilled.customerEmail;
			String html = quotation.render(quotationFilled).body();
			String textVersion = Messages.get("admin.quotation.order.email.text.version");

			Promise<Result> resultPromise = MailjetAdapter.wsCreateACampaignWithHtmlContent(subject, title, email, html, textVersion)
					.map(url -> {
						flash("success", Messages.get("admin.quotation.success", url));
						return INDEX;
						});
							
			Promise<Result> recoverPromise = resultPromise.recoverWith(throwable -> {
				Logger.debug("4");
				Logger.error(throwable.getMessage());
				flash("error", throwable.getMessage());
	        	return Promise.pure((Result) badRequest(quotation_form.render(quotationForm, getAvailableWatches())));
			});
			
			return recoverPromise;
		} catch (Exception e) {
			Logger.debug("5");
			flash("error", e.getMessage());
			return Promise.pure((Result) badRequest(quotation_form.render(quotationForm, getAvailableWatches())));	
		}
    }
	
	private static boolean orderIsValid(long id) {
		OrderRequest orderFound = OrderRequest.findById(id);
		return orderFound != null;
	}
	
	private static boolean buyRequestIsValid(long id) {
		BuyRequest requestFound = BuyRequest.findById(id);
		return requestFound != null;
	}
	
	private static boolean presetExists(long id) {
		PresetQuotationForBrand presetFound = PresetQuotationForBrand.findById(id);
		return presetFound != null;
	}
	
	private static boolean serviceTestIsValid(long id) {
		ServiceTest serviceTestFound = ServiceTest.findById(id);
		return serviceTestFound != null;
	}
	
    private static List<Watch> getAvailableWatches() {
    	return Watch.findAvailable();
    }
    
    private static String getSubjectFromQuotation(Quotation quotation) {
    	switch (quotation.typeOfNetwork) {
    		case IN_BOTH: case IN_ONLY :
    			return Messages.get("admin.quotation.order.email.subject.for.in.brand", quotation.brand);	
    		case OUT_BOTH:
    			return Messages.get("admin.quotation.order.email.subject.for.out.not.only");
    		case OUT_ONLY:
    			return Messages.get("admin.quotation.order.email.subject.for.out.only");
    	}
		return null;
    }
    
    private static String getTitleFromQuotation(Quotation quotation) {
    	switch (quotation.typeOfNetwork) {
		case IN_BOTH: case IN_ONLY :
			return Messages.get("admin.quotation.order.email.title.for.in.brand", quotation.customerEmail);	
		case OUT_BOTH: case OUT_ONLY:
			return Messages.get("admin.quotation.order.email.title.for.out", quotation.customerEmail);
    	}
    	return null;
    }
    
    private static String getStringValue(String value) {
    	if (value != null && value.length() == 0) {
    		return null;
    	} else {
    		return value;
    	}
    }
    
    public static Result goToExternalDocumentUrl(long id) {
 	   return redirect(ExternalDocument.findById(id).url);
    }
}

