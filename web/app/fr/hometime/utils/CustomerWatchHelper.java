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

/**
 * Helper for Customer Watch
 * 
 * @author Raphael
 *
 */

public class CustomerWatchHelper {
	public static List<CustomerWatch> findForLoggedInCustomer(Session session) {
    	return genericFindForLoggedInCustomer(session, (customer) -> CustomerWatch.find.where().conjunction().eq("customer.id", customer.id).orderBy("creationDate DESC").findList());
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWorkingOnIt(Session session) {
   		return genericFindForLoggedInCustomer(session, CustomerWatch::findByCustomerOut);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWaitingToBeCollected(Session session) {
    	return genericFindForLoggedInCustomer(session, CustomerWatch::findByCustomerWaitingToBeCollected);
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
    
}