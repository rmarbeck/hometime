package controllers;

import models.User;
import models.User.Role;
import play.Logger;
import play.mvc.Http.Context;

public class SecuredAdminOnly extends Secured {
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
}
