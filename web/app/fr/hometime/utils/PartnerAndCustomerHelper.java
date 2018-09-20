package fr.hometime.utils;

import java.util.Optional;

import models.Customer;
import models.CustomerWatch;
import models.Partner;
import models.User;
import play.mvc.Http.Session;

/**
 * Security class containing functions for managing Partners 
 * 
 * @author Raphael
 *
 */

public class PartnerAndCustomerHelper {
	public static boolean isLoggedInUserAPartner(Session session) {
		return getLoggedInPartner(session).isPresent();
	}
	
	public static boolean isLoggedInUserACustomer(Session session) {
		return getLoggedInCustomer(session).isPresent();
	}

	public static Optional<Partner> getLoggedInPartner(Session session)  {
		Optional<User> loggedInUser = SecurityHelper.getLoggedInUser(session);
		if (loggedInUser.isPresent())
			if (loggedInUser.get().partner != null)
				return Optional.of(loggedInUser.get().partner);
		return Optional.empty();
	}
	
	public static Optional<Customer> getLoggedInCustomer(Session session)  {
		Optional<User> loggedInUser = SecurityHelper.getLoggedInUser(session);
		if (loggedInUser.isPresent())
			if (loggedInUser.get().customer != null)
				return Optional.of(loggedInUser.get().customer);
		return Optional.empty();
	}
	
	public static Long getLoggedInPartnerID(Session session)  {
		if (isLoggedInUserAPartner(session))
			return getLoggedInPartner(session).get().id;
		return null;
	}
	
	public static Long getLoggedInCustomerID(Session session)  {
		if (isLoggedInUserACustomer(session))
			return getLoggedInCustomer(session).get().id;
		return null;
	}
	
	public static boolean isWatchAllocatedToLoggedInPartner(CustomerWatch watch, Session session) {
		Optional<Partner> partner = getLoggedInPartner(session);
		if (partner.isPresent() && watch != null && watch.partner != null)
			return watch.partner.equals(partner.get());
		return false;
	}
	
	public static boolean isWatchOneOfLoggedInCustomer(CustomerWatch watch, Session session) {
		Optional<Customer> customer = getLoggedInCustomer(session);
		if (customer.isPresent() && watch != null && watch.customer != null)
			return watch.customer.equals(customer.get());
		return false;
	}
	
	
	public static Partner findInternalPartner() {
		return models.Partner.findByInternalName("simon");
	}
	
	public static boolean isItInternalPartner(Partner partner) {
		if (partner != null)
			return isItInternalPartner(partner.id);
		return false;
	}
	
	public static boolean isItInternalPartner(Long partnerId) {
		return partnerId == findInternalPartner().id;
	}
	
	public static boolean isLoggedInUserInternalPartner(Session session) {
		if (isLoggedInUserAPartner(session))
			if (isItInternalPartner(getLoggedInPartner(session).get()))
				return true;
		return false;
	}
	
	public static boolean isWatchAllocatedToInternalPartner(CustomerWatch watch) {
		return isItInternalPartner(watch.partner);
		
	}
}
