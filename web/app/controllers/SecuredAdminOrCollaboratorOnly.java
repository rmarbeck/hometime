package controllers;

import play.mvc.Http.Context;
import static fr.hometime.utils.SecurityHelper.retrieveUsername;

import java.util.function.Predicate;

import fr.hometime.utils.SecurityHelper;
import models.User;

import static fr.hometime.utils.SecurityHelper.isAdmin;
import static fr.hometime.utils.SecurityHelper.isCollaborator;

public class SecuredAdminOrCollaboratorOnly extends SecuredForRolesOnly {
	public static Predicate<User> isEnough = (user) -> isAdmin.test(user) || isCollaborator.test(user);

    public static boolean isLoggedInUserAuthorized(String token) {
    	return isLoggedInUserAuthorized(token, isEnough);
    }
    
    public Predicate<User> isEnough() {
    	return isEnough;
    };
}
