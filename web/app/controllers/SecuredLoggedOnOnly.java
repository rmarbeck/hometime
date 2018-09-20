package controllers;

import static fr.hometime.utils.SecurityHelper.isAdmin;
import static fr.hometime.utils.SecurityHelper.isCollaboratorOrPartner;
import static fr.hometime.utils.SecurityHelper.isCustomer;

import java.util.function.Predicate;

import models.User;

public class SecuredLoggedOnOnly extends SecuredForRolesOnly {
	public static Predicate<User> isEnough = (user) -> isAdmin.test(user) || isCollaboratorOrPartner.test(user) || isCustomer.test(user);
	
    public static boolean isLoggedInUserAuthorized(String token) {
    	return isLoggedInUserAuthorized(token, isEnough);
    }
    
    public Predicate<User> isEnough() {
    	return isEnough;
    };
}
