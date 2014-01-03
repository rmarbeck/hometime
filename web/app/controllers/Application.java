package controllers;

import static play.data.Form.form;

import java.util.List;

import javax.persistence.Column;

import fr.hometime.utils.ActionHelper;
import models.Brand;
import models.OrderRequest;
import models.Picture;
import models.Watch;
import models.OrderRequest.MethodTypes;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	public static class OrderForm {

		@Constraints.Required
		public String type = OrderRequest.OrderTypes.SERVICE.toString();
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
	    	type = OrderRequest.OrderTypes.SERVICE.toString();
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
	    	request.type = OrderRequest.OrderTypes.fromString(this.type);
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

    public static Result order() {
    	try {
	        return ok(order.render("", fillFormWithQueryParams(), getSupportedBrands(), getDisplayableWatches()));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }

    public static Result about() {
        return ok(about.render(""));
    }
    
    public static Result privacy() {
        return ok(privacy.render(""));
    }
    
    public static Result cgv() {
        return ok(cgv.render(""));
    }

	public static Result manageOrder() {
		Form<OrderForm> orderForm = Form.form(OrderForm.class).bindFromRequest();
		if(orderForm.hasErrors()) {
			return badRequest(order.render("", orderForm, getSupportedBrands(), getDisplayableWatches()));
		} else {
			OrderRequest orderRequest = orderForm.get().getRequest();
			orderRequest.save();
			
			ActionHelper.tryToNotifyTeamByEmail("Nouvelle demande de devis", orderRequest.toString());
			
			/*Logger.debug(orderRequest.brand.display_name);
			Logger.debug(orderRequest.model);
			Logger.debug(orderRequest.remark);
			if (orderRequest.watchChosen != null)
				Logger.debug(orderRequest.watchChosen.full_name);
			Logger.debug(orderRequest.nameOfCustomer);
			Logger.debug(orderRequest.email);
			Logger.debug(orderRequest.phoneNumber);
			Logger.debug(orderRequest.city);*/
			
			return redirect(
					routes.Application.index()
					);
		}
	}
    
    public static Result watch_detail(Long id) {
    	try {
	    	Watch currentWatch = Watch.findById(id);
	        return ok(watch_detail.render("Submariner", currentWatch, Picture.findPicturesForWatch(currentWatch), getDisplayableWatchesExceptOne(currentWatch)));
    	} catch (Exception e) {
    		return badRequest();
    	}
    }

    
    private static List<Watch> getDisplayableWatches() {
    	return Watch.findDisplayable();
    }

    private static List<Watch> getAvailableWatches() {
    	return Watch.findAvailable();
    }

    private static List<Brand> getSupportedBrands() {
    	return Brand.findAll();
    }

    private static List<Watch> getDisplayableWatchesExceptOne(Watch currentWatch) {
    	List<Watch> displayableWatches = getDisplayableWatches();
    	if (displayableWatches != null && !displayableWatches.isEmpty())
    		displayableWatches.remove(currentWatch);
    	return displayableWatches;
    }
    
    private static Form<OrderForm> fillFormWithQueryParams() {
    	if (isWatchParamFoundAndValid())
    		return Form.form(OrderForm.class).fill(new OrderForm(form().bindFromRequest().get("watch")));
    	return Form.form(OrderForm.class).fill(new OrderForm());
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
}
