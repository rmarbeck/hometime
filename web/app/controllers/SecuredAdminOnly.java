package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Security.Authenticator;
import play.mvc.Http.*;
import models.*;
import models.User.Role;

public class SecuredAdminOnly extends Authenticator {
    @Override
    public String getUsername(Context ctx) {
    	String currentUserToken = ctx.session().get("token");
    	if (currentUserToken != null) {
    		User loggedInUser = User.findByEmail(currentUserToken);
    		Logger.debug("There seems to be a user in session : {}", loggedInUser);
    		if (loggedInUser!= null && loggedInUser.role == Role.ADMIN)
    			return loggedInUser.email;
    	}
    		
        return null;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }
}
