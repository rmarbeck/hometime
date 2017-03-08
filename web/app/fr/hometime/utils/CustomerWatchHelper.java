package fr.hometime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import play.mvc.Http.Session;
import models.CustomerWatch;
import models.Customer;
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
    	Optional<User> loggedInUser = SecurityHelper.getLoggedInUser(session);
    	if (loggedInUser.isPresent()) {
    		return finder.apply(loggedInUser.get().customer);
    	}
    	return new ArrayList<CustomerWatch>();
    }
}