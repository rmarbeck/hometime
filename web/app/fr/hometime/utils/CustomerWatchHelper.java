package fr.hometime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import play.mvc.Http.Session;
import models.CustomerWatch;
import models.Customer;
import models.Partner;
import models.User;
import models.CustomerWatch.CustomerWatchStatus;

/**
 * Helper for Customer Watch
 * 
 * @author Raphael
 *
 */

public class CustomerWatchHelper {
	
	public enum CustomerWatchDetailedStatusForCustomer {
	    TO_BE_ACCEPTED ("CUSTOMER_WATCH_STATUS_TO_BE_ACCEPTED"),
	    TO_QUOTE ("CUSTOMER_WATCH_STATUS_TO_QUOTE"),
	    WAITING_FOR_QUOTATION_ACCEPTATION ("CUSTOMER_WATCH_STATUS_WAITING_FOR_QUOTATION_ACCEPTATION"),
	    WORKING ("CUSTOMER_WATCH_STATUS_WORKING"),
	    FINISHED_STORED_BY_US ("CUSTOMER_WATCH_STATUS_FINISHED_STORED_BY_US"),
	    FINISHED_TO_BILL ("CUSTOMER_WATCH_STATUS_FINISHED_TO_BILL"),
	    CLOSE ("CUSTOMER_WATCH_STATUS_CLOSE"),
	    UNCONSISTENT ("CUSTOMER_WATCH_STATUS_UNCONSISTENT");
	    
		private String name = "";
		    
		CustomerWatchDetailedStatusForCustomer(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static CustomerWatchDetailedStatusForCustomer fromString(String name) {
	        for (CustomerWatchDetailedStatusForCustomer status : CustomerWatchDetailedStatusForCustomer.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	
	public static List<CustomerWatch> findForLoggedInCustomer(Session session) {
    	return genericFindForLoggedInCustomer(session, (customer) -> CustomerWatch.find.where().conjunction().eq("customer.id", customer.id).orderBy("creationDate DESC").findList());
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWorkingOnIt(Session session) {
   		return genericFindForLoggedInCustomer(session, CustomerWatch::findByCustomerWorkingOnIt);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWorkDone(Session session) {
   		return genericFindForLoggedInCustomer(session, CustomerWatch::findByCustomerWorkDone);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWaitingForPayment(Session session) {
   		return genericFindForLoggedInCustomer(session, CustomerWatch::findByCustomerWaitingForPayment);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWaitingToBeCollected(Session session) {
    	return genericFindForLoggedInCustomer(session, CustomerWatch::findByCustomerWaitingToBeCollected);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWaitingForQuotation(Session session) {
    	return genericFindForLoggedInCustomer(session, CustomerWatch::findByCustomerWaitingForQuotation);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWaitingForQuotationApproval(Session session) {
    	return genericFindForLoggedInCustomer(session, CustomerWatch::findByCustomerWaitingForQuotationApproval);
    }
    
    
    
    private static List<CustomerWatch> genericFindForLoggedInCustomer(Session session, Function<Customer, List<CustomerWatch>> finder) {
    	return genericFind(session, (user) -> finder.apply(user.customer));
    }
    
	public static List<CustomerWatch> findForLoggedInPartner(Session session) {
    	return genericFindForLoggedInPartner(session, (partner) -> CustomerWatch.find.where().conjunction().eq("partner.id", partner.id).orderBy("firstEntryInPartnerWorkshopDate DESC").findList());
    }
	
	public static List<CustomerWatch> findForLoggedInPartnerInWorkshop(Session session) {
    	return genericFindForLoggedInPartner(session, (partner) -> CustomerWatch.find.where().conjunction().eq("partner.id", partner.id).eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER).orderBy("firstEntryInPartnerWorkshopDate DESC").findList());
    }
    
    private static List<CustomerWatch> genericFindForLoggedInPartner(Session session, Function<Partner, List<CustomerWatch>> finder) {
    	return genericFind(session, (user) -> finder.apply(user.partner));
    }
    
    private static List<CustomerWatch> genericFind(Session session, Function<User, List<CustomerWatch>> finder) {
    	Optional<User> loggedInUser = SecurityHelper.getLoggedInUser(session);
    	if (loggedInUser.isPresent()) {
    		return finder.apply(loggedInUser.get());
    	}
    	return new ArrayList<CustomerWatch>();
    }
    
    public static String getStatusForCustomer(CustomerWatch watch) {
    	return evaluateStatusForCustomer(watch).name();
    }
    
    public static String getNextStepForCustomer(CustomerWatch watch) {
    	return evaluateNextStepForCustomer(watch).name();
    }
    
    private static CustomerWatchDetailedStatusForCustomer evaluateStatusForCustomer(CustomerWatch watch) {
    	if (watch == null)
    		return CustomerWatchDetailedStatusForCustomer.UNCONSISTENT;
    	if (watch.status.equals(CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER) && watch.serviceNeeded) {
    		return CustomerWatchDetailedStatusForCustomer.TO_BE_ACCEPTED;
    	} else if (watch.serviceNeeded && watch.finalCustomerServicePrice == 0) {
    		return CustomerWatchDetailedStatusForCustomer.TO_QUOTE;
    	} else if (watch.serviceNeeded && !watch.finalCustomerServicePriceAccepted) {
    		return CustomerWatchDetailedStatusForCustomer.WAITING_FOR_QUOTATION_ACCEPTATION;
    	} else if (watch.serviceNeeded) {
    		return CustomerWatchDetailedStatusForCustomer.WORKING;
    	} else if (!watch.serviceNeeded && watch.serviceStatus == 100 && !watch.status.equals(CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)) {
    		return CustomerWatchDetailedStatusForCustomer.FINISHED_STORED_BY_US;
    	} else if (!watch.serviceNeeded && watch.status.equals(CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER) && !watch.finalCustomerServicePaid) {
    		return CustomerWatchDetailedStatusForCustomer.FINISHED_TO_BILL;
    	} else if (!watch.serviceNeeded && watch.status.equals(CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER) && watch.finalCustomerServicePaid) {
    		return CustomerWatchDetailedStatusForCustomer.CLOSE;
    	} else {
    		return CustomerWatchDetailedStatusForCustomer.UNCONSISTENT;
    	}
    }
    
    public static Long getStatusForCustomerAsLong(CustomerWatch watch) {
    	switch(evaluateStatusForCustomer(watch)) {
    		case TO_BE_ACCEPTED: return 1L;
    		case TO_QUOTE: return 2L;
    		case WAITING_FOR_QUOTATION_ACCEPTATION: return 3L;
    		case WORKING: return 4L;
    		case FINISHED_STORED_BY_US: return 5L;
    		case FINISHED_TO_BILL: return 6L;
    		case CLOSE: return 7L;
    		default: return 0L;
    	}
    }
    
    private static CustomerWatchDetailedStatusForCustomer evaluateNextStepForCustomer(CustomerWatch watch) {
    	switch(evaluateStatusForCustomer(watch)) {
    		case TO_BE_ACCEPTED : return CustomerWatchDetailedStatusForCustomer.TO_QUOTE;
    		case TO_QUOTE : return CustomerWatchDetailedStatusForCustomer.WAITING_FOR_QUOTATION_ACCEPTATION;
    		case WAITING_FOR_QUOTATION_ACCEPTATION : return CustomerWatchDetailedStatusForCustomer.WORKING;
    		case WORKING : return CustomerWatchDetailedStatusForCustomer.FINISHED_STORED_BY_US;
    		case FINISHED_STORED_BY_US : return CustomerWatchDetailedStatusForCustomer.FINISHED_TO_BILL;
    		case FINISHED_TO_BILL : return CustomerWatchDetailedStatusForCustomer.CLOSE;
    		case CLOSE : return CustomerWatchDetailedStatusForCustomer.CLOSE;
    		case UNCONSISTENT : return CustomerWatchDetailedStatusForCustomer.UNCONSISTENT;
    		default: return CustomerWatchDetailedStatusForCustomer.UNCONSISTENT;
    	}
   }
    
   public static boolean isEditableByCustomer(Long watchId) {
	   if (watchId != null)
		   return evaluateStatusForCustomer(CustomerWatch.findById(watchId)).equals(CustomerWatchDetailedStatusForCustomer.TO_BE_ACCEPTED);
	   return false;
   }
   
   public static boolean isWaitingQuotationAcceptation(Long watchId) {
	   if (watchId != null)
		   return evaluateStatusForCustomer(CustomerWatch.findById(watchId)).equals(CustomerWatchDetailedStatusForCustomer.WAITING_FOR_QUOTATION_ACCEPTATION);
	   return false;
   }
   
   public static boolean isWorkDone(Long watchId) {
	   if (watchId != null)
		   return evaluateStatusForCustomer(CustomerWatch.findById(watchId)).equals(CustomerWatchDetailedStatusForCustomer.FINISHED_STORED_BY_US);
	   return false;
   }
   
   public static boolean isWaitingQuotation(Long watchId) {
	   if (watchId != null)
		   return evaluateStatusForCustomer(CustomerWatch.findById(watchId)).equals(CustomerWatchDetailedStatusForCustomer.TO_QUOTE);
	   return false;
   }
   
   public static boolean isToBeAccepted(Long watchId) {
	   if (watchId != null)
		   return evaluateStatusForCustomer(CustomerWatch.findById(watchId)).equals(CustomerWatchDetailedStatusForCustomer.TO_BE_ACCEPTED);
	   return false;
   }
   
	public static void updateWatchEnsuringOnlyEditableDataByCustomerAreChanged(models.CustomerWatch watch) {
		models.CustomerWatch currentWatchInDB = CustomerWatch.findById(watch.id);
		watch.emergencyLevel = currentWatchInDB.emergencyLevel;
		watch.finalCustomerServicePrice = currentWatchInDB.finalCustomerServicePrice;
		watch.finalCustomerServiceStatus = currentWatchInDB.finalCustomerServiceStatus;
		watch.finalCustomerServicePaid = currentWatchInDB.finalCustomerServicePaid;
		watch.newServicePriceNeeded = currentWatchInDB.newServicePriceNeeded;
		watch.picturesDoneOnCollect = currentWatchInDB.picturesDoneOnCollect;
		watch.serviceNeeded = currentWatchInDB.serviceNeeded;
		watch.servicePaid = currentWatchInDB.servicePaid;
		watch.servicePrice = currentWatchInDB.servicePrice;
		watch.servicePriceAccepted = currentWatchInDB.servicePriceAccepted;
		watch.serviceStatus = currentWatchInDB.serviceStatus;
		watch.status = currentWatchInDB.status;
		
		watch.update();
	}
	
	public static void updateWatchEnsuringOnlyEditableDataByPartnerAreChanged(models.CustomerWatch watch, Session session) {
		models.CustomerWatch currentWatchInDB = CustomerWatch.findById(watch.id);
		watch.emergencyLevel = currentWatchInDB.emergencyLevel;
		if (PartnerAndCustomerHelper.isLoggedInUserInternalPartner(session)) {
			watch.finalCustomerServicePrice = watch.servicePrice;
			watch.finalCustomerServiceStatus = watch.serviceStatus;
		} else {
			watch.finalCustomerServicePrice = currentWatchInDB.finalCustomerServicePrice;
			watch.finalCustomerServiceStatus = currentWatchInDB.finalCustomerServiceStatus;
		}
		
		watch.finalCustomerServicePaid = currentWatchInDB.finalCustomerServicePaid;
		watch.newServicePriceNeeded = currentWatchInDB.newServicePriceNeeded;
		watch.picturesDoneOnCollect = currentWatchInDB.picturesDoneOnCollect;
		watch.serviceNeeded = currentWatchInDB.serviceNeeded;
		watch.servicePaid = currentWatchInDB.servicePaid;
		watch.servicePriceAccepted = currentWatchInDB.servicePriceAccepted;
		watch.finalCustomerEmergencyLevel = currentWatchInDB.finalCustomerEmergencyLevel;
		watch.status = currentWatchInDB.status;
		watch.quotation = currentWatchInDB.quotation;
		
		watch.update();
	}
}