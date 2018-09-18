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
	
	public enum CustomerWatchDetailedStatus {
	    TO_BE_ACCEPTED ("CUSTOMER_WATCH_STATUS_TO_BE_ACCEPTED"),
	    TO_QUOTE ("CUSTOMER_WATCH_STATUS_TO_QUOTE"),
	    QUOTATION_TO_SEND_TO_CUSTOMER ("QUOTATION_TO_SEND_TO_CUSTOMER"),
	    WAITING_FOR_QUOTATION_ACCEPTATION_FROM_FINAL_CUSTOMER ("WAITING_FOR_QUOTATION_ACCEPTATION_FROM_FINAL_CUSTOMER"),
	    WAITING_FOR_QUOTATION_ACCEPTATION ("CUSTOMER_WATCH_STATUS_WAITING_FOR_QUOTATION_ACCEPTATION"),
	    WORK_TO_START ("CUSTOMER_WATCH_STATUS_WORK_TO_START"),
	    WORKING ("CUSTOMER_WATCH_STATUS_WORKING"),
	    TESTING ("CUSTOMER_WATCH_STATUS_TESTING"),
	    FINISHED_STORED_BY_US_TO_DELIVER ("CUSTOMER_WATCH_STATUS_FINISHED_STORED_BY_US_TO_DELIVER"),
	    FINISHED_TO_BILL_BEFORE_DELIVERY ("CUSTOMER_WATCH_STATUS_FINISHED_TO_BILL_BEFORE_DELIVERY"),
	    FINISHED_WAITING_PAYMENT_BEFORE_DELIVERY ("CUSTOMER_WATCH_STATUS_FINISHED_WAITING_PAYMENT_BEFORE_DELIVERY"),
	    FINISHED_STORED_BY_US_PAID ("CUSTOMER_WATCH_STATUS_FINISHED_STORED_BY_US_PAID"),
	    FINISHED_TO_BILL_AFTER_DELIVERY ("CUSTOMER_WATCH_STATUS_FINISHED_TO_BILL_AFTER_DELIVERY"),
	    FINISHED_WAITING_PAYMENT_AFTER_DELIVERY ("CUSTOMER_WATCH_STATUS_FINISHED_WAITING_PAYMENT_AFTER_DELIVERY"),
	    CLOSE ("CUSTOMER_WATCH_STATUS_CLOSE"),
	    UNCONSISTENT ("CUSTOMER_WATCH_STATUS_UNCONSISTENT");
	    
		private String name = "";
		    
		CustomerWatchDetailedStatus(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static CustomerWatchDetailedStatus fromString(String name) {
	        for (CustomerWatchDetailedStatus status : CustomerWatchDetailedStatus.values()) {
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
    	switch(evaluateStatus(watch)) {
    		case UNCONSISTENT: return CustomerWatchDetailedStatusForCustomer.UNCONSISTENT;
    		case TO_BE_ACCEPTED: return CustomerWatchDetailedStatusForCustomer.TO_BE_ACCEPTED;
    		case TO_QUOTE: return CustomerWatchDetailedStatusForCustomer.TO_QUOTE;
    		case WAITING_FOR_QUOTATION_ACCEPTATION_FROM_FINAL_CUSTOMER: return CustomerWatchDetailedStatusForCustomer.WAITING_FOR_QUOTATION_ACCEPTATION;
    		case WAITING_FOR_QUOTATION_ACCEPTATION:
    		case WORK_TO_START:
    		case WORKING:
    		case TESTING:
    			return CustomerWatchDetailedStatusForCustomer.WORKING;
    		case FINISHED_STORED_BY_US_TO_DELIVER:
    		case FINISHED_TO_BILL_BEFORE_DELIVERY:
    		case FINISHED_WAITING_PAYMENT_BEFORE_DELIVERY:
    			return CustomerWatchDetailedStatusForCustomer.FINISHED_STORED_BY_US;
    		case FINISHED_TO_BILL_AFTER_DELIVERY:
    		case FINISHED_WAITING_PAYMENT_AFTER_DELIVERY:
    			return CustomerWatchDetailedStatusForCustomer.FINISHED_TO_BILL;
    		case CLOSE: return CustomerWatchDetailedStatusForCustomer.CLOSE;
    		default: return CustomerWatchDetailedStatusForCustomer.UNCONSISTENT;
    	}
    }
    
    public static String getStatusName(CustomerWatch watch) {
    	return evaluateStatus(watch).name;
    }
    
    private static CustomerWatchDetailedStatus evaluateStatus(CustomerWatch watch) {
    	if (watch == null)
    		return CustomerWatchDetailedStatus.UNCONSISTENT;
    	
    	if (watch.serviceDefinitivelyRefused)
    		return CustomerWatchDetailedStatus.FINISHED_STORED_BY_US_TO_DELIVER;
    	
    	if (byCustomerOrbackToCustomer(watch) && watch.serviceNeeded)
    		return CustomerWatchDetailedStatus.TO_BE_ACCEPTED;
    	
    	if (watch.serviceNeeded && watch.finalCustomerServicePrice == 0)
    		return CustomerWatchDetailedStatus.TO_QUOTE;

    	if (watch.serviceNeeded && !watch.finalCustomerQuotationSent)
    		return CustomerWatchDetailedStatus.QUOTATION_TO_SEND_TO_CUSTOMER;
    	
    	if (watch.serviceNeeded && watch.finalCustomerQuotationSent && !watch.finalCustomerServicePriceAccepted)
    		return CustomerWatchDetailedStatus.WAITING_FOR_QUOTATION_ACCEPTATION_FROM_FINAL_CUSTOMER;

    	if (watch.serviceNeeded && !watch.servicePriceAccepted)
    		return CustomerWatchDetailedStatus.WAITING_FOR_QUOTATION_ACCEPTATION;
    	
    	if (watch.serviceNeeded && serviceStatusAt0(watch))
    		return CustomerWatchDetailedStatus.WORK_TO_START;

    	if (watch.serviceNeeded && serviceStatusBetween1and99(watch))
    		return CustomerWatchDetailedStatus.WORKING;
    	
    	if (watch.serviceNeeded && serviceStatusAt100(watch))
    		return CustomerWatchDetailedStatus.TESTING;
    	
    	if (watch.customer.isAPro()) {
    		if (!watch.serviceNeeded && serviceStatusAt100(watch) && holdByUs(watch))
    			return CustomerWatchDetailedStatus.FINISHED_STORED_BY_US_TO_DELIVER;
    		
    		if (!watch.serviceNeeded && byCustomerOrbackToCustomer(watch) && !watch.serviceInvoiced)
    			return CustomerWatchDetailedStatus.FINISHED_TO_BILL_AFTER_DELIVERY;
    		
    		if (!watch.serviceNeeded && byCustomerOrbackToCustomer(watch) && !watch.finalCustomerServicePaid)
    			return CustomerWatchDetailedStatus.FINISHED_WAITING_PAYMENT_AFTER_DELIVERY;
    	} else {
    		if (holdByUs(watch) && serviceStatusAt100(watch)) {
    			if (!watch.serviceNeeded && !watch.serviceInvoiced && !watch.finalCustomerServicePaid)
    				return CustomerWatchDetailedStatus.FINISHED_TO_BILL_BEFORE_DELIVERY;
    			
    			if (!watch.serviceNeeded && watch.serviceInvoiced && !watch.finalCustomerServicePaid)
    				return CustomerWatchDetailedStatus.FINISHED_WAITING_PAYMENT_BEFORE_DELIVERY;
    			
    			if (!watch.serviceNeeded && watch.serviceInvoiced && watch.finalCustomerServicePaid)
    				return CustomerWatchDetailedStatus.FINISHED_STORED_BY_US_TO_DELIVER;
    		}
    	}

    	if (!watch.serviceNeeded && byCustomerOrbackToCustomer(watch) && watch.finalCustomerServicePaid) {
    		return CustomerWatchDetailedStatus.CLOSE;
    	} else {
    		return CustomerWatchDetailedStatus.UNCONSISTENT;
    	}
    }
    
    private static boolean serviceStatusAt100(CustomerWatch watch) {
    	return watch.serviceStatus == 100;
    }
    
    private static boolean serviceStatusAt0(CustomerWatch watch) {
    	return watch.serviceStatus == 0;
    }
    
    private static boolean serviceStatusBetween1and99(CustomerWatch watch) {
    	return !(serviceStatusAt100(watch) || serviceStatusAt0(watch));
    }
    
    private static boolean byCustomerOrbackToCustomer(CustomerWatch watch) {
    	return watch.status.equals(CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER);
    }
    
    private static boolean holdByUs(CustomerWatch watch) {
    	return !byCustomerOrbackToCustomer(watch);
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
    
    public static Float getStatusAsFloat(CustomerWatch watch) {
    	switch(evaluateStatus(watch)) {
    		case TO_BE_ACCEPTED: return 1F;
    		case TO_QUOTE: return 2F;
    		case QUOTATION_TO_SEND_TO_CUSTOMER: return 2.5F;
    		case WAITING_FOR_QUOTATION_ACCEPTATION_FROM_FINAL_CUSTOMER: return 3F;
    		case WAITING_FOR_QUOTATION_ACCEPTATION: return 4F;
    		case WORK_TO_START: return 5F;
    		case WORKING: return 6F;
    		case TESTING: return 7F;
    		case FINISHED_STORED_BY_US_TO_DELIVER: return 8F;
    		case FINISHED_TO_BILL_BEFORE_DELIVERY: return 9F;
    		case FINISHED_WAITING_PAYMENT_BEFORE_DELIVERY: return 10F;
    		case FINISHED_STORED_BY_US_PAID: return 11F;
    		case FINISHED_TO_BILL_AFTER_DELIVERY: return 12F;
    		case FINISHED_WAITING_PAYMENT_AFTER_DELIVERY: return 13F;
    		case CLOSE: return 14F;
    		default: return 0F;
    	}
    }
    
   private static boolean is(Long watchId, CustomerWatchDetailedStatusForCustomer toTest) {
	   if (watchId != null)
		   return evaluateStatusForCustomer(CustomerWatch.findById(watchId)).equals(toTest);
	   return false;
   }
    
   public static boolean isEditableByCustomer(Long watchId) {
	   return is(watchId, CustomerWatchDetailedStatusForCustomer.TO_BE_ACCEPTED);
   }
   
   public static boolean isWaitingQuotationAcceptation(Long watchId) {
	   return is(watchId, CustomerWatchDetailedStatusForCustomer.WAITING_FOR_QUOTATION_ACCEPTATION);
   }
   
   public static boolean isWorkDone(Long watchId) {
	   return is(watchId, CustomerWatchDetailedStatusForCustomer.FINISHED_STORED_BY_US);
   }
   
   public static boolean isWaitingQuotation(Long watchId) {
	   return is(watchId, CustomerWatchDetailedStatusForCustomer.TO_QUOTE);
   }
   
   public static boolean isToBeAccepted(Long watchId) {
	   return is(watchId, CustomerWatchDetailedStatusForCustomer.TO_BE_ACCEPTED);
   }

   public static boolean isWorking(Long watchId) {
	   return is(watchId, CustomerWatchDetailedStatusForCustomer.WORKING);
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
		//watch.servicePriceAccepted = currentWatchInDB.servicePriceAccepted;
		watch.serviceStatus = currentWatchInDB.serviceStatus;
		watch.status = currentWatchInDB.status;
		watch.serviceOnHold = currentWatchInDB.serviceOnHold;
		
		watch.update();
	}
	
	public static void updateWatchEnsuringOnlyEditableDataByPartnerAreChanged(models.CustomerWatch watch, Session session) {
		models.CustomerWatch currentWatchInDB = CustomerWatch.findById(watch.id);
		watch.emergencyLevel = currentWatchInDB.emergencyLevel;
		if (PartnerAndCustomerHelper.isLoggedInUserInternalPartner(session)) {
			if (watch.servicePrice != 0)
				watch.finalCustomerServicePrice = watch.servicePrice;
			watch.finalCustomerServiceStatus = watch.serviceStatus;
			watch.finalCustomerToInfos = watch.partnerFromInfos;
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
		watch.serviceOnHold = currentWatchInDB.serviceOnHold;
		
		watch.update();
	}
	
    public static Optional<List<User>> findByCustomer(Customer customer) {
    	List<User> users = User.find.where().eq("customer.id", customer.id).orderBy("id ASC").findList();
    	if (users != null && !users.isEmpty())
    		return Optional.of(users);
    	return Optional.empty();
    }
    
    public static Optional<CustomerWatch> tryTofindByPattern(String supposedId) {
    	if (supposedId != null) {
    		try {
    			 Long foundId = Long.decode(supposedId);
    			 if (foundId != null) {
    				 CustomerWatch foundWatch = CustomerWatch.findById(Long.decode(supposedId));
    				 if (foundWatch != null)
    					 return Optional.of(foundWatch);
    			 }
    		} catch (NumberFormatException nfe) {
    			
    		}
    	}
    	return Optional.empty();
    }
    
    public static CustomerWatch findByPattern(String supposedId) {
    	Optional<CustomerWatch> foundWatch = tryTofindByPattern(supposedId);
    	if (foundWatch.isPresent())
    		return foundWatch.get();
    	return null;
    }
    
}