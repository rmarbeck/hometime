package controllers;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.redirect;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import fr.hometime.utils.SecurityHelper;
import models.SparePart;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR})
public class SpareParts extends Controller {
	public static Crud<SparePart, SparePart> crud = Crud.of(
			SparePart.of(),
			views.html.admin.spare_part.ref(),
			views.html.admin.spare_part_form.ref(),
			views.html.admin.spare_parts.ref());
	
    public static Result pageOfOpenSpareParts(int page, String sortBy, String order, String filter, int pageSize) {
        return ok(views.html.admin.spare_parts.render(SparePart.pageOfOpenSpareParts(page, pageSize, sortBy, order, filter), sortBy, order, filter, pageSize));
    }
    
    public static Result displayOverview() {
    	return ok(views.html.admin.spare_parts_overview.render("", models.SparePart.findAllJustCreatedByCreationDateDesc(), models.SparePart.findAllToOrderByCreationDateDesc(), models.SparePart.findAllToReceiveByCreationDateDesc(), models.SparePart.findAllToCheckByCreationDateDesc(), models.SparePart.findAllToCloseByCreationDateDesc()));
    }
    
    public static Result prepareOrdering(long id) {
    	SparePart existingSparePart = SparePart.findById(id);
    	if (existingSparePart != null) {
    		existingSparePart.ordered = true;
    		existingSparePart.realInPrice = existingSparePart.expectedInPrice;
    		return ok(views.html.admin.spare_part_ordering_form.render(Form.form(models.SparePart.class).fill(existingSparePart), false));
    	}
    	flash("error", "Unknown spare id");
		return badRequest();
    	
    }

	public static Result manageOrdering() {
		final Form<models.SparePart> sparePartForm = Form.form(models.SparePart.class).bindFromRequest();
		if (sparePartForm.hasErrors()) {
			return badRequest(views.html.admin.spare_part_ordering_form.render(sparePartForm, false));
		} else {
			models.SparePart sparePart = sparePartForm.get();
			sparePart.orderDate = new Date();
			sparePart.update();
		}
		return displayOverview();
	}
	
	public static Result manageWorkflow() {
		final Form<models.SparePart> sparePartForm = Form.form(models.SparePart.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		
		if ("delete".equals(action)) {
			models.SparePart sparePart = sparePartForm.get();
			sparePart.delete();
			return redirect(
					routes.CrudHelper.displayAll("SpareParts", 20)
					);
		} else {
			if (sparePartForm.hasErrors()) {
				if ("save".equals(action))
					return badRequest(views.html.admin.spare_part_form.render(sparePartForm, true));
				return badRequest(views.html.admin.spare_part_form.render(sparePartForm, false));
			} else {
				models.SparePart sparePart = sparePartForm.get();
				if ("save".equals(action)) {
					sparePart.save();
				} else if ("show".equals(action)) {
					return crud.display(sparePart.id);
				} else {
					sparePart.update();
				}
				return displayWorkflowNextStep(sparePart.watch.id);
			}
		}
	}
	
	
    public static Result displayWorkflowNextStep(long watchId) {
    	models.CustomerWatch currentWatch = models.CustomerWatch.findById(watchId);
    	if (SecurityHelper.isLoggedInUserAuthorized(session(), SecurityHelper.isWatchmaker)) {
    		if (currentWatch.servicePriceAccepted)
    			return Watchmaker.prepareWorkInProgress(currentWatch.id); 
    		return Watchmaker.prepareQuotation(currentWatch.id);
    	}
    	return redirect(
				routes.CrudHelper.displayAll("SpareParts", 20)
				);
    }
    
	public static Result createForCustomerWatch(long id) {
		return crud.create(Form.form(SparePart.class).fill(createForCustomerWatch(id, false)));
    }
	
	public static Result createForCustomerWatchOrderNow(long id) {
		return crud.create(Form.form(SparePart.class).fill(createForCustomerWatch(id, true)));
    }
	
	private static SparePart createForCustomerWatch(long id, boolean toOrderNow) {
		SparePart instance = new SparePart();
		models.CustomerWatch watch = models.CustomerWatch.findById(id);
		
		if (watch != null)
			instance.watch = watch;
		
		if (toOrderNow) {
			instance.confirmed = true;
			instance.toOrder = true;
		}
		return instance;
	}
	
	public static Result moveToNextStep(long id) {
		retrieve(id).ifPresent(instance -> {
			if (instance.checkedOK) {
				markClosed(id);
			} else if (instance.ordered) {
				markReceived(id);
			} else if (instance.toOrder) {
				markToOrder(id);
			} else {
				markOrdered(id, instance.expectedInPrice);
			}
		});
		
		return displayOverview(); 
	}
	
	public static Result markConfirmed(long id) {
		return doIt(id, instance -> {
			instance.confirmed = true;
		});
	}
	
	
	public static Result markToOrder(long id) {
		return doIt(id, instance -> {
			instance.confirmed = true;
			instance.toOrder = true;
		});
	}
	
	public static Result markOrdered(long id, long price) {
		return doIt(id, instance -> {
			instance.ordered = true;
			instance.realInPrice = price;
			instance.orderDate = new Date();
		});
	}
	
	public static Result markReceived(long id) {
		return doIt(id, instance -> {
			instance.got = true;
			instance.receptionDate = new Date();
		});
	}
	
	public static Result markCheckedOK(long id) {
		return doIt(id, instance -> {
			instance.checkedOK = true;
			instance.checkedKO = false;
		});
	}
	
	public static Result markCheckedKO(long id) {
		return doIt(id, instance -> {
			instance.checkedOK = false;
			instance.checkedKO = true;
		});
	}
	
	public static Result markClosed(long id) {
		return doIt(id, instance -> {
			instance.closed = true;
		});
	}
	
	public static void markAllRelatedToCustomerWatchClosed(long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null) {
			List<SparePart> stillOpen = SparePart.findAllOpenByCustomerWatch(existingWatch);
			if (stillOpen != null)
				stillOpen.forEach(spare -> {
					spare.closed = true;
					spare.update();
				});
		}
	}
	
	private static Result doIt(long id, Consumer<SparePart> action) {
		retrieve(id).ifPresent(instance -> {
			action.accept(instance);
			instance.lastStatusUpdate = new Date();
			instance.update();
		});
		
		return displayOverview(); 
	}
	
	private static Optional<SparePart> retrieve(long id) {
		SparePart instance = SparePart.findById(id);

		return Optional.ofNullable(instance); 
	}
	
}
