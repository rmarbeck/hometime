package controllers;

import static fr.hometime.utils.SecurityHelper.retrieveUsername;

import fr.hometime.utils.ActionHelper;
import play.mvc.Http.Context;
import play.mvc.Result;

public class SecuredEnhanced extends SecurityEnhanced.Authenticator {
    @Override
    public String getUsername(Context ctx) {
    	return retrieveUsername(ctx);
    }

    @Override
    public Result onUnauthorized(Context ctx) {
    	ActionHelper.setOriginOfCall(ctx);
        return redirect(routes.Application.quickAdminLogin(ActionHelper.getOriginOfCall(ctx)));
    }
}
