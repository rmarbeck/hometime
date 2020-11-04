package controllers;

import java.util.Date;

import models.Customer.CustomerCivility;
import models.CustomerWatch.CustomerWatchStatus;
import models.QuickServiceWatch;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR})
public class QuickServiceWatches extends Controller {
	public static Crud<models.QuickServiceWatch, models.QuickServiceWatch> crud = Crud.of(
			models.QuickServiceWatch.of(),
			views.html.admin.quick_service_watch.ref(),
			views.html.admin.quick_service_watch_form.ref(),
			views.html.admin.quick_service_watches.ref());
	
	
	public static Result saveWatchAndCustomer(long id) {
		QuickServiceWatch foundQSWatch = models.QuickServiceWatch.findById(id);
		
		if (foundQSWatch != null && checkIfCanTransform(foundQSWatch))
				if (saveInDBCustomerAndWatch(foundQSWatch) != null) {
					removeQSWatchFromDB(foundQSWatch);
					return Admin.INDEX;
				}
		return Admin.badRequest();
    }
	
	private static boolean checkIfCanTransform(QuickServiceWatch qsWatch) {
		if (doesCustomerAlreadyExist(qsWatch) || doWeKnowEnoughToCreateCustomer(qsWatch))
			if (doWeKnowEnoughToCreateWatch(qsWatch))
				return true;
		return false;
	}
	
	private static boolean doesCustomerAlreadyExist(QuickServiceWatch qsWatch) {
		if (qsWatch.customerEmail != null && models.Customer.findByEmail(qsWatch.customerEmail) != null)
			return true;
		return false;
	}
	
	private static boolean doWeKnowEnoughToCreateCustomer(QuickServiceWatch qsWatch) {
		return isNotEmpty(qsWatch.customerEmail)
				&& isNotEmpty(qsWatch.customerFirstName)
				&& isNotEmpty(qsWatch.customerName)
				&& isNotEmpty(qsWatch.customerAddress)
				&& isNotEmpty(qsWatch.customerPhoneNumber);
	}
	
	private static boolean doWeKnowEnoughToCreateWatch(QuickServiceWatch qsWatch) {
		return isNotEmpty(qsWatch.brand);
	}
	
	private static models.CustomerWatch saveInDBCustomerAndWatch(QuickServiceWatch qsWatch) {
		models.CustomerWatch newWatch = new models.CustomerWatch(getOrCreateCustomer(qsWatch));
		newWatch.brand = qsWatch.brand;
		newWatch.model = qsWatch.model;
		newWatch.serial = qsWatch.serial;
		newWatch.reference = qsWatch.reference;
		newWatch.collectingDate = new Date();
		newWatch.status = CustomerWatchStatus.STORED_BY_WATCH_NEXT;
		newWatch.save();
		return newWatch;
	}
	
	private static models.Customer getOrCreateCustomer(QuickServiceWatch qsWatch) {
		models.Customer currentCustomer = models.Customer.findByEmail(qsWatch.customerEmail);
		if (currentCustomer == null) {
			models.Customer newCustomer = new models.Customer();
			newCustomer.email = qsWatch.customerEmail;
			newCustomer.civility = CustomerCivility.fromString(qsWatch.civility.name());
			newCustomer.firstname = qsWatch.customerFirstName;
			newCustomer.name = qsWatch.customerName;
			newCustomer.billingAddress = qsWatch.customerAddress;
			newCustomer.phoneNumber = qsWatch.customerPhoneNumber;
			newCustomer.sharedInfos = qsWatch.customerInfo;
			newCustomer.save();
			currentCustomer = newCustomer;
		} else {
			currentCustomer.privateInfos = currentCustomer.privateInfos+"\n\nfrom QuickServiceWatch :\n"
											+ qsWatch.customerAddress + "\n"
											+ qsWatch.customerPhoneNumber + "\n"
											+ qsWatch.customerInfo + "\n";
			currentCustomer.update();
		}
		return currentCustomer;
	}
	
	private static boolean isNotEmpty(String value) {
		return value != null && !value.equals("");
	}
	
	private static boolean removeQSWatchFromDB(QuickServiceWatch qsWatch) {
		qsWatch.delete();
		return true;
	}
}
