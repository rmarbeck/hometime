package controllers;

import static fr.hometime.utils.SecurityHelper.retrieveUsername;

import java.util.function.Predicate;

import fr.hometime.utils.SecurityHelper;
import models.User;
import play.mvc.Http.Context;

public abstract class SecuredForRolesOnly extends Secured {
    @Override
    public String getUsername(Context ctx) {
    	return retrieveUsername(ctx, isEnough());
    }

    public abstract Predicate<User> isEnough();
    
	public static boolean isLoggedInUserAuthorized(String token, Predicate<User> isEnough) {
    	return SecurityHelper.isLoggedInUserAuthorized(token, isEnough);
    }
}
