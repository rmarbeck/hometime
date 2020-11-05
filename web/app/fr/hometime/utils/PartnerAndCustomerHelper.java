package fr.hometime.utils;

import java.util.Optional;
import java.util.function.Predicate;

import models.Customer;
import models.CustomerWatch;
import models.Partner;
import models.User;
import play.Logger;
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
		return getAndCheckLoggedInUser(session, (user -> user.partner != null)).map(user -> user.partner);
	}
	
	public static Optional<Customer> getLoggedInCustomer(Session session)  {
		return getAndCheckLoggedInUser(session, (user -> user.customer != null)).map(user -> user.customer);
	}
	
	public static Optional<User> getLoggedInCollaborator(Session session)  {
		return getAndCheckLoggedInUser(session, (user -> user.role.equals(User.Role.COLLABORATOR)));
	}
	
	private static Optional<User> getAndCheckLoggedInUser(Session session, Predicate<User> toCheck) { 
		Optional<User> loggedInUser = SecurityHelper.getLoggedInUser(session);
		return loggedInUser.filter(toCheck);
	}
	
	public static Long getLoggedInPartnerID(Session session)  {
		if (isLoggedInUserAPartner(session))
			return getLoggedInPartner(session).get().id;
		return null;
	}
	
	public static String getLoggedInCollaboratorName(Session session)  {
		return getLoggedInCollaborator(session).map(user -> user.name).orElse("-unknown-");
	}
	
	public static String getLoggedInCollaboratorFirstname(Session session)  {
		return getLoggedInCollaborator(session).map(user -> user.firstname).orElse("-unknown-");
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
	
	public static boolean isWatchOneOfLoggedInCollaborator(CustomerWatch watch, Session session) {
		Optional<User> collaborator = getLoggedInCollaborator(session);
		return collaborator.filter(collab -> (isWatchAllocatedToCollaborator(watch, collab))).isPresent();
	}
	
	
	public static Partner findInternalPartner() {
		return (Partner) CachingHelper.getDataFromCache("simon", (key) -> Optional.of(models.Partner.findByInternalName(key)), "partner_").get();
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
	
	private static boolean isWatchAllocatedToCollaborator(CustomerWatch watch, User user) {
		if (watch != null && watch.managedBy != null && user != null)
			return watch.managedBy.firstname.equals(user.firstname);
		return false;
	}
}
