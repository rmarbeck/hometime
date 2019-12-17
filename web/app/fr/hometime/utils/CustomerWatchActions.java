package fr.hometime.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import fr.hometime.utils.CustomerWatchHelper.CustomerWatchDetailedStatusForCustomer;
import play.mvc.Http.Session;
import models.CustomerWatch;
import models.Customer;
import models.Customer.CustomerStatus;
import models.Partner;
import models.User;
import models.CustomerWatch.CustomerWatchStatus;

/**
 * Helper for Customer Watch
 * 
 * @author Raphael
 *
 */

public class CustomerWatchActions {
	public enum CustomerWatchActionList {
		MARK_IN_HOUSE_SERVICING ("MARK_IN_HOUSE_SERVICING"),
		MARK_SERVICE_PRICE_TO_EVALUATE ("MARK_SERVICE_PRICE_TO_EVALUATE"),
	    MARK_SERVICE_PRICE_ACCEPTED ("MARK_SERVICE_PRICE_ACCEPTED"),
	    MARK_SERVICE_PRICE_ACCEPTED_AND_START_SERVICE ("MARK_SERVICE_PRICE_ACCEPTED_AND_START_SERVICE"),
	    MARK_SERVICE_PRICE_DECLINED ("MARK_SERVICE_PRICE_DECLINED"),
	    MARK_SERVICE_PRICE_DEFINITIVELY_REFUSED ("MARK_SERVICE_PRICE_DEFINITIVELY_REFUSED"),
	    MARK_SERVICING_ON_HOLD ("MARK_SERVICING_ON_HOLD"),
	    MARK_SERVICE_FINISHED_TESTING ("MARK_SERVICE_FINISHED_TESTING"),
	    MARK_SERVICE_TESTING_FAILED ("MARK_SERVICE_TESTING_FAILED"),
	    MARK_SERVICE_TESTING_FINISHED ("MARK_SERVICE_TESTING_FINISHED"),
	    MARK_BACK_TO_CUSTOMER_AND_PAID ("MARK_BACK_TO_CUSTOMER_AND_PAID"),
		MARK_BACK_TO_CUSTOMER_AND_WAITING_PAYMENT ("MARK_BACK_TO_CUSTOMER_AND_WAITING_PAYMENT"),
		MARK_BACK_TO_CUSTOMER_AND_CANCELED ("MARK_BACK_TO_CUSTOMER_AND_CANCELED");
	    
		private String name = "";
		    
		CustomerWatchActionList(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static CustomerWatchActionList fromString(String name) {
	        for (CustomerWatchActionList action : CustomerWatchActionList.values()) {
	            if (action.name.equals(name)) {
	                return action;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public static void doAction(String actionName, CustomerWatch watch, Session session) {
		doAction(CustomerWatchActionList.fromString(actionName), watch, session);
	}
	
	public static void doAction(CustomerWatchActionList action, CustomerWatch watch, Session session) {
		if (canDoAction(action, watch)) {
			getAction(action).accept(watch, session);
			watch.update();
		} 
	}
	
	public static boolean canDoAction(CustomerWatchActionList action, CustomerWatch watch) {
		switch(action) {
			case MARK_IN_HOUSE_SERVICING: return canMarkInHouseServicing(watch);
			case MARK_SERVICE_PRICE_TO_EVALUATE: return true;
			case MARK_SERVICE_PRICE_ACCEPTED: return canMarkServicePriceAccepted(watch);
			case MARK_SERVICE_PRICE_ACCEPTED_AND_START_SERVICE: return canMarkServicePriceAcceptedAndStartService(watch);
			case MARK_SERVICE_PRICE_DECLINED: return canMarkServicePriceDeclined(watch);
			case MARK_SERVICE_PRICE_DEFINITIVELY_REFUSED: return canMarkServicePriceDefinitivelyRefused(watch);
			case MARK_SERVICING_ON_HOLD: return canMarkServicingOnHold(watch);
			case MARK_SERVICE_FINISHED_TESTING: return canMarkServiceFinishedTesting(watch);
			case MARK_SERVICE_TESTING_FAILED: return canMarkTestingFailed(watch);
			case MARK_SERVICE_TESTING_FINISHED: return canMarkTestingFinished(watch);
			case MARK_BACK_TO_CUSTOMER_AND_PAID: return canMarkBackToCustomerAndPaid(watch);
			case MARK_BACK_TO_CUSTOMER_AND_WAITING_PAYMENT: return canMarkBackToCustomerAndWaitingPayment(watch);
			case MARK_BACK_TO_CUSTOMER_AND_CANCELED: return canMarkBackToCustomerAndCanceled(watch);
			default: return true;
		}
	}
	
	private static BiConsumer<CustomerWatch, Session> getAction(CustomerWatchActionList action)  {
		switch(action) {
			case MARK_IN_HOUSE_SERVICING: return CustomerWatchActions::markInHouseServicing;
			case MARK_SERVICE_PRICE_TO_EVALUATE: return CustomerWatchActions::doNothing;
			case MARK_SERVICE_PRICE_ACCEPTED: return CustomerWatchActions::markServicePriceAccepted;
			case MARK_SERVICE_PRICE_ACCEPTED_AND_START_SERVICE: return CustomerWatchActions::markServicePriceAcceptedAndStartService;
			case MARK_SERVICE_PRICE_DECLINED: return CustomerWatchActions::markServicePriceDeclined;
			case MARK_SERVICE_PRICE_DEFINITIVELY_REFUSED: return CustomerWatchActions::markServicePriceDefinitivelyRefused;
			case MARK_SERVICING_ON_HOLD: return CustomerWatchActions::markServicingOnHold;
			case MARK_SERVICE_FINISHED_TESTING: return CustomerWatchActions::markServiceFinishedTesting;
			case MARK_SERVICE_TESTING_FAILED: return CustomerWatchActions::markTestingFailed;
			case MARK_SERVICE_TESTING_FINISHED: return CustomerWatchActions::markTestingFinished;
			case MARK_BACK_TO_CUSTOMER_AND_PAID: return CustomerWatchActions::markBackToCustomerAndPaid;
			case MARK_BACK_TO_CUSTOMER_AND_WAITING_PAYMENT: return CustomerWatchActions::markBackToCustomerAndWaitingPayment;
			case MARK_BACK_TO_CUSTOMER_AND_CANCELED: return CustomerWatchActions::markBackToCustomerAndCanceled;
			default: return CustomerWatchActions::doNothing;
		}
	}
		
	private static void doNothing(CustomerWatch watch, Session session) {
		return;
	}
	
	private static boolean canMarkInHouseServicing(CustomerWatch watch)  {
		return !PartnerAndCustomerHelper.findInternalPartner().equals(watch.partner);
	}
	
	private static void markInHouseServicing(CustomerWatch watch, Session session) {
		watch.status = CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER;
		watch.partner = PartnerAndCustomerHelper.findInternalPartner();
	}
	

	private static boolean canMarkServicePriceAccepted(CustomerWatch watch)  {
		return watch.finalCustomerServicePrice != 0 && watch.finalCustomerServicePriceAccepted != true;
	}
	
	private static void markServicePriceAccepted(CustomerWatch watch, Session session) {
		watch.finalCustomerServicePriceAccepted = true;
		watch.finalCustomerServicePriceAcceptedDate = new Date();
	}
	
	private static boolean canMarkServicePriceAcceptedAndStartService(CustomerWatch watch)  {
		return watch.finalCustomerServicePrice != 0 && watch.servicePriceAccepted != true;
	}
	
	private static void markServicePriceAcceptedAndStartService(CustomerWatch watch, Session session) {
		markServicePriceAccepted(watch, session);
		watch.newServicePriceNeeded = false;
		watch.servicePriceAccepted = true;
	}
	
	private static boolean canMarkServicePriceDeclined(CustomerWatch watch)  {
		return watch.servicePriceAccepted == false;
	}
	
	private static void markServicePriceDeclined(CustomerWatch watch, Session session)  {
		watch.newServicePriceNeeded = true;
	}
	
	private static boolean canMarkServicePriceDefinitivelyRefused(CustomerWatch watch)  {
		return CustomerWatchHelper.isWaitingQuotationAcceptation(watch);
	}
	
	private static void markServicePriceDefinitivelyRefused(CustomerWatch watch, Session session)  {
		watch.serviceDefinitivelyRefused = true;
	}
	
	private static boolean canMarkServicingOnHold(CustomerWatch watch)  {
		return watch.serviceStatus < 100L && watch.serviceOnHold == false;
	}
	
	private static void markServicingOnHold(CustomerWatch watch, Session session)  {
		watch.serviceOnHold = true;
	}
	
	
	private static boolean canMarkServiceFinishedTesting(CustomerWatch watch)  {
		return watch.serviceStatus <= 100L && watch.status == CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER;
	}
	
	private static void markServiceFinishedTesting(CustomerWatch watch, Session session)  {
		watch.serviceOnHold = false;
		watch.serviceStatus = 100L;
		markOutFromPartner(watch);
	}
	
	private static boolean canMarkTestingFailed(CustomerWatch watch)  {
		return watch.serviceStatus == 100L && watch.status == CustomerWatchStatus.STORED_BY_WATCH_NEXT;
	}
	
	private static void markTestingFailed(CustomerWatch watch, Session session)  {
		watch.serviceStatus = 90L;
		watch.status = CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER;
	}
	
	private static boolean canMarkTestingFinished(CustomerWatch watch)  {
		return CustomerWatchHelper.isWorking(watch) && watch.status == CustomerWatchStatus.STORED_BY_WATCH_NEXT;
	}
	
	private static void markTestingFinished(CustomerWatch watch, Session session)  {
		watch.serviceNeeded = false;
		watch.testerEmail = SecurityHelper.getLoggedInUserEmail(session);
	}
	
	private static boolean canMarkBackToCustomerAndPaid(CustomerWatch watch)  {
		return CustomerWatchHelper.isWorkDone(watch);
	}
	
	private static void markBackToCustomerAndPaid(CustomerWatch watch, Session session)  {
		markBackToCustomer(watch);
		watch.serviceInvoiced = true;
		watch.finalCustomerServicePaid = true;
		watch.serviceNeeded = false;
	}
	
	private static boolean canMarkBackToCustomerAndWaitingPayment(CustomerWatch watch)  {
		return CustomerWatchHelper.isWorkDone(watch);
	}
	
	private static void markBackToCustomerAndWaitingPayment(CustomerWatch watch, Session session)  {
		markBackToCustomer(watch);
		watch.serviceInvoiced = true;
		watch.finalCustomerServicePaid = false;
		watch.serviceNeeded = false;
	}
	
	private static boolean canMarkBackToCustomerAndCanceled(CustomerWatch watch)  {
		return !CustomerWatchHelper.isWorking(watch) && !CustomerWatchHelper.isWorkDone(watch);
	}
	
	private static void markBackToCustomerAndCanceled(CustomerWatch watch, Session session)  {
		markBackToCustomer(watch);
		watch.serviceDefinitivelyRefused = true;
		watch.serviceInvoiced = false;
		watch.finalCustomerServicePaid = false;
		watch.serviceNeeded = false;
	}
	
	private static void markBackToCustomer(CustomerWatch watch)  {
		watch.status = CustomerWatchStatus.BACK_TO_CUSTOMER;
	}
	
	private static void markOutFromPartner(CustomerWatch watch)  {
		watch.status = CustomerWatchStatus.STORED_BY_WATCH_NEXT;
	}
}