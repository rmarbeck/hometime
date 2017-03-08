package controllers;

import play.mvc.Http.Context;
import static fr.hometime.utils.SecurityHelper.retrieveUsername;
import static fr.hometime.utils.SecurityHelper.isAdmin;
import static fr.hometime.utils.SecurityHelper.isCollaboratorOrPartner;

public class SecuredAdminOrCollaboratorOrPartnerOnly extends Secured {
    @Override
    public String getUsername(Context ctx) {
    	return retrieveUsername(ctx, (user) -> isAdmin.test(user) || isCollaboratorOrPartner.test(user));
    }
}
