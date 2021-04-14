package controllers;

import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import fr.hometime.utils.CustomerWatchActions;
import fr.hometime.utils.CustomerWatchHelper;
import fr.hometime.utils.DateHelper;
import fr.hometime.utils.PartnerAndCustomerHelper;
import models.CustomerWatch.CustomerWatchStatus;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.admin.customer_watch;
import views.html.admin.customer_watch_contextual;
import views.html.admin.customer_watch_form;
import views.html.admin.customer_watch_quotation_validation_form;
import views.html.admin.customer_watch_phonecall_form;
import views.html.admin.customer_watches;
import views.html.admin.reports.customer_watch_by_status;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
@With(NoCacheAction.class)
public class CustomerWatch extends Controller {
	
	private static final int NB_OF_DAYS_FOR_SERVICE_DEFAULT = 21;
	
	public static Result LIST_CUSTOMER_WATCHES = redirect(
			routes.CustomerWatch.displayAll(0, "creationDate", "desc", "", "")
			);

	public static Result displayAll(int page, String sortBy, String order, String filter, String status) {
        return ok(customer_watches.render(models.CustomerWatch.page(page, 10, sortBy, order, filter, status), sortBy, order, filter, status));
    }

	public static Result display(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customer_watch.render(existingWatch));
		flash("error", "Unknown watch id");
		return LIST_CUSTOMER_WATCHES;
    }
	
	public static Result displayContextual(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customer_watch_contextual.render(existingWatch));
		flash("error", "Unknown watch id");
		return LIST_CUSTOMER_WATCHES;
    }
	
	public static Result addFromCustomer(Long customerId) {
		models.Customer existingCustomer = models.Customer.findById(customerId);
		if (existingCustomer != null) {
			models.CustomerWatch newWatch = new models.CustomerWatch(existingCustomer);
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(newWatch), true));
		}
		flash("error", "Unknown customer id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result addFromOrder(Long orderId) {
		models.Order existingOrder = models.Order.findById(orderId);
		if (existingOrder != null) {
			models.CustomerWatch newWatch = new models.CustomerWatch(existingOrder);
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(newWatch), true));
		}
		flash("error", "Unknown customer id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result acceptWatchFromCustomer(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (acceptWatch(existingWatch))
			return Customer.display(existingWatch.customer.id);
		flash("error", "Unknown watch id");
		return LIST_CUSTOMER_WATCHES;
	}
	
	protected static boolean acceptWatch(models.CustomerWatch watchToAccept) {
		if (watchToAccept != null) {
			watchToAccept.collectingDate = new Date();
			watchToAccept.status = CustomerWatchStatus.STORED_BY_WATCH_NEXT_OUTSIDE;
			watchToAccept.status = CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER;
			watchToAccept.partner = PartnerAndCustomerHelper.findInternalPartner();
			watchToAccept.update();
			return true;
		}
		return false;
		
	}
	
	public static Result displayByStatusName(String statusName) {
		return ok(customer_watch_by_status.render(CustomerWatchHelper.findUnderOurResponsabilityFilteredByStatusName(statusName)));
	}
	
	public static Result add() {
		return ok(emptyNewWatchForm());		
	}
	
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static Result doAction(String actionName, Long watchId) {
		return doActionAndGo(actionName, watchId, LIST_CUSTOMER_WATCHES);
	}
	
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static Result doActionContextual(String actionName, Long watchId) {
		return doActionAndGo(actionName, watchId, displayContextual(watchId));
	}
	
	private static Result doActionAndGo(String actionName, Long watchId, Result nextPage) {
		CustomerWatchActions.doAction(CustomerWatchActions.CustomerWatchActionList.fromString(actionName), models.CustomerWatch.findById(watchId), session());
		return nextPage;
	}
	
	public static Result edit(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null)
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result duplicate(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null) {
			existingWatch.additionnalModelInfos = "duplicate";
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(existingWatch), true));
		}
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	public static Result duplicateBackToSAV(Long watchId) {
		models.CustomerWatch duplicatedWatch = duplicateBackToSAV(models.CustomerWatch.findById(watchId));
		if (duplicatedWatch != null)
			return ok(customerWatchForm(Form.form(models.CustomerWatch.class).fill(duplicatedWatch), true));
		flash("error", "Unknown watch id");
		return badRequest(emptyNewWatchForm());
	}
	
	protected static models.CustomerWatch duplicateBackToSAV(models.CustomerWatch watchToDuplicate) {
		if (watchToDuplicate != null) {
			models.CustomerWatch newWatch = new models.CustomerWatch();
			newWatch.additionnalModelInfos = "duplicate for SAV";
			newWatch.customer = watchToDuplicate.customer;
			newWatch.brand = watchToDuplicate.brand;
			newWatch.model = watchToDuplicate.model;
			newWatch.reference = watchToDuplicate.reference;
			newWatch.serial = watchToDuplicate.serial;
			newWatch.serial2 = watchToDuplicate.serial2;
			newWatch.movement = watchToDuplicate.movement;
			newWatch.status = models.CustomerWatch.CustomerWatchStatus.STORED_BY_WATCH_NEXT;
			newWatch.type = watchToDuplicate.type;
			newWatch.quotation = watchToDuplicate.quotation;
			return newWatch;
		}
		return null;
	}
	
	public static Result setQuotationSent(Long watchId) {
		return updateWatch(watchId, (watch) -> {watch.finalCustomerQuotationSent = true;});
	}
	
	public static Result setQuotationSentContextual(Long watchId) {
		return updateWatchContextual(watchId, (watch) -> {watch.finalCustomerQuotationSent = true;});
	}
	
	public static Result setQuickServiceContextual(Long watchId) {
		return updateWatchContextual(watchId, (watch) -> {watch.quickWinPossible = true;});
	}
	
	public static Result setAllocatedToContextual(Long watchId, Long watchmakerId) {
		return updateWatchContextual(watchId, (watch) -> {watch.managedBy = User.findById(watchmakerId);});
	}
	
	public static Result setQuotationAccepted(Long watchId) {
		return doAction(CustomerWatchActions.CustomerWatchActionList.MARK_SERVICE_PRICE_ACCEPTED_AND_START_SERVICE.name(), watchId);
	}
	
	public static Result setQuotationAcceptedContextual(Long watchId) {
		return doAction(CustomerWatchActions.CustomerWatchActionList.MARK_SERVICE_PRICE_ACCEPTED_AND_START_SERVICE.name(), watchId);
	}
	
	public static Result setBackToCustomer(Long watchId) {
		SpareParts.markAllRelatedToCustomerWatchClosed(watchId);
		return updateStatus(watchId, CustomerWatchStatus.BACK_TO_CUSTOMER);
	}
	
	public static Result setBackToCustomerContextual(Long watchId) {
		SpareParts.markAllRelatedToCustomerWatchClosed(watchId);
		return updateStatusContextual(watchId, CustomerWatchStatus.BACK_TO_CUSTOMER);
	}
	
	public static Result setStoredByPartner(Long watchId) {
		return updateStatus(watchId, CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER);
	}
	
	public static Result setStoredByUs(Long watchId) {
		return updateStatus(watchId, CustomerWatchStatus.STORED_BY_WATCH_NEXT);
	}
	
	public static Result setStoredByUsOutside(Long watchId) {
		return updateStatus(watchId, CustomerWatchStatus.STORED_BY_WATCH_NEXT_OUTSIDE);
	}
	
	private static Result updateStatus(Long watchId, CustomerWatchStatus newStatus) {
		return updateWatch(watchId, (watch) -> {watch.status = newStatus;});
	}
	
	private static Result updateStatusContextual(Long watchId, CustomerWatchStatus newStatus) {
		return updateWatchContextual(watchId, (watch) -> {watch.status = newStatus;});
	}
	
	private static Result updateWatch(Long watchId, Consumer<models.CustomerWatch> toDo) {
		return updateWatchAndGo(watchId, toDo, Admin.INDEX);
	}
	
	private static Result updateWatchContextual(Long watchId, Consumer<models.CustomerWatch> toDo) {
		return updateWatchAndGo(watchId, toDo, displayContextual(watchId));
	}
	
	private static Result updateWatchAndGo(Long watchId, Consumer<models.CustomerWatch> toDo, Result nextPage) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null) {
			toDo.accept(existingWatch);
			existingWatch.lastStatusUpdate = new Date();
			existingWatch.update();
		}
		return nextPage;
	}
	
	private static void updateWatch(models.CustomerWatch newValues, BiConsumer<models.CustomerWatch, models.CustomerWatch> toDo) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(newValues.id);
		if (existingWatch != null) {
			toDo.accept(existingWatch, newValues);
			existingWatch.lastStatusUpdate = new Date();
			existingWatch.update();
		}
	}
	
	public static Result manage() {
		final Form<models.CustomerWatch> watchForm = Form.form(models.CustomerWatch.class).bindFromRequest();
		String action = Form.form().bindFromRequest().get("action");
		if (watchForm.hasErrors()) {
			if ("save".equals(action))
				return badRequest(customerWatchForm(watchForm, true));
			return badRequest(customerWatchForm(watchForm, false));
		} else {
			models.CustomerWatch watch = watchForm.get();
			if ("save".equals(action)) {
				watch.save();
			} else if ("delete".equals(action)) {
				models.CustomerWatch customerWatchInDB = models.CustomerWatch.findById(watch.id);
				customerWatchInDB.delete();
			} else {
				watch.update();
			}
		}
		return LIST_CUSTOMER_WATCHES;
	}
	
	public static Result prepareQuotationValidation(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null) {
			existingWatch.servicePriceAccepted = true;
			existingWatch.finalCustomerServicePriceAccepted = true;
			existingWatch.finalCustomerServicePriceAcceptedDate = new Date();
			existingWatch.serviceDueDate = DateHelper.todayPlusNDays(NB_OF_DAYS_FOR_SERVICE_DEFAULT);
			return ok(customer_watch_quotation_validation_form.render(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		}
		flash("error", "Unknown watch id");
		return badRequest();
	}
	
	public static Result manageQuotationValidation() {
		return doSpecificUpdate((existingWatch, newWatchValues) -> {
			existingWatch.servicePriceAccepted = newWatchValues.servicePriceAccepted;
			existingWatch.finalCustomerServicePriceAccepted = newWatchValues.finalCustomerServicePriceAccepted;
			existingWatch.finalCustomerServicePriceAcceptedDate = newWatchValues.finalCustomerServicePriceAcceptedDate;
			existingWatch.serviceDueDate = newWatchValues.serviceDueDate;
			});
	}
	
	public static Result preparePhoneCall(Long watchId) {
		models.CustomerWatch existingWatch = models.CustomerWatch.findById(watchId);
		if (existingWatch != null) {
			existingWatch.customerHasCalledForDelay = true;
			existingWatch.lastCustomerCallDate = new Date();
			existingWatch.lastDueDateCommunicated = DateHelper.todayPlusNDays(3);
			existingWatch.lastCustomerCallInformation = existingWatch.lastCustomerCallDate+" -> \n"+"\n"+existingWatch.lastCustomerCallInformation;
			return ok(customer_watch_phonecall_form.render(Form.form(models.CustomerWatch.class).fill(existingWatch), false));
		}
		flash("error", "Unknown watch id");
		return badRequest();
	}
	
	public static Result managePhoneCall() {
		return doSpecificUpdate((existingWatch, newWatchValues) -> {
			existingWatch.customerHasCalledForDelay = newWatchValues.customerHasCalledForDelay;
			existingWatch.lastCustomerCallDate = newWatchValues.lastCustomerCallDate;
			existingWatch.lastDueDateCommunicated = newWatchValues.lastDueDateCommunicated;
			existingWatch.lastCustomerCallInformation = newWatchValues.lastCustomerCallInformation;
			existingWatch.serviceDueDateMustHave = newWatchValues.serviceDueDateMustHave;
			existingWatch.customerHasConstraint = newWatchValues.customerHasConstraint;
			});
	}
	
	private static Result doSpecificUpdate(BiConsumer<models.CustomerWatch, models.CustomerWatch> toDo) {
		final Form<models.CustomerWatch> watchForm = Form.form(models.CustomerWatch.class).bindFromRequest();
		if (watchForm.hasErrors())
			return badRequest(customerWatchForm(watchForm, false));

		models.CustomerWatch newWatchValues = watchForm.get();
		
		updateWatch(newWatchValues, toDo);
		return LIST_CUSTOMER_WATCHES;
	}
	
	protected static Html customerWatchForm(Form<models.CustomerWatch> watchForm, boolean isItANewWatch) {
		return customer_watch_form.render(watchForm, isItANewWatch, models.Customer.findByNameAsc(), models.Partner.findAllByEmailAsc(), models.User.findAllWatchmakers());
	}
	
	protected static Html emptyNewWatchForm() {
		return customerWatchForm(Form.form(models.CustomerWatch.class), true);
	}
}
