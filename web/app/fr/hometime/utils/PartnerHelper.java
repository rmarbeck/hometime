package fr.hometime.utils;

import java.util.Optional;

import models.CustomerWatch;
import models.Partner;
import models.User;
import play.mvc.Http.Session;

/**
 * Security class containing functions for manging Partners 
 * 
 * @author Raphael
 *
 */

public class PartnerHelper {
	public static boolean isLoggedInUserAPartner(Session session) {
		return getLoggedInPartner(session).isPresent();
	}

	public static Optional<Partner> getLoggedInPartner(Session session)  {
		Optional<User> loggedInUser = SecurityHelper.getLoggedInUser(session);
		if (loggedInUser.isPresent())
			if (loggedInUser.get().partner != null)
				return Optional.of(loggedInUser.get().partner);
		return Optional.empty();
	}
	
	public static Long getLoggedInPartnerID(Session session)  {
		if (isLoggedInUserAPartner(session))
			return getLoggedInPartner(session).get().id;
		return null;
	}
	
	public static boolean isWatchAllocatedToLoggedInPartner(CustomerWatch watch, Session session) {
		Optional<Partner> partner = getLoggedInPartner(session);
		if (partner.isPresent() && watch != null && watch.partner != null)
			return watch.partner.equals(partner.get());
		return false;
	}
}
