package controllers;

import play.mvc.Security;
import play.mvc.Http.Context;
import static fr.hometime.utils.SecurityHelper.retrieveUsername;

import java.util.function.Predicate;

import fr.hometime.utils.SecurityHelper;
import models.User;

import static fr.hometime.utils.SecurityHelper.isAdmin;

public abstract class SecuredForRolesOnly extends Secured {
    @Override
    public String getUsername(Context ctx) {
    	return retrieveUsername(ctx, isEnough());
    }

    public abstract Predicate<User> isEnough();
    
	public static boolean isLoggedInUserAuthorized(String token, Predicate<User> isEnough) {
    	System.out.println(">>>>>>>>>>>>>>>>>><<<<<<<< "+SecurityHelper.isLoggedInUserAuthorized(token, isEnough));
    	return SecurityHelper.isLoggedInUserAuthorized(token, isEnough);
    }
}
