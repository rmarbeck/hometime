package controllers;

import play.mvc.Http.Context;
import static fr.hometime.utils.SecurityHelper.retrieveUsername;
import static fr.hometime.utils.SecurityHelper.isAdmin;

public class SecuredAdminOnly extends Secured {
    @Override
    public String getUsername(Context ctx) {
    	return retrieveUsername(ctx, isAdmin);
    }
}
