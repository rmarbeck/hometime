package controllers;

import fr.hometime.utils.ActionHelper;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security.Authenticator;

public class Secured extends Authenticator {
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("email");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
    	ActionHelper.setOriginOfCall(ctx);
        return redirect(routes.Application.quickAdminLogin(ActionHelper.getOriginOfCall(ctx)));
    }
}
