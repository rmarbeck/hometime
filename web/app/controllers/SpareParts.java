package controllers;

import models.SparePart;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR})
public class SpareParts extends Controller {
	public static Crud<SparePart, SparePart> crud = Crud.of(
			SparePart.of(),
			views.html.admin.spare_part.ref(),
			views.html.admin.spare_part_form.ref(),
			views.html.admin.spare_parts.ref());
	
    public static Result pageOfOpenSpareParts(int page, String sortBy, String order, String filter, int pageSize) {
        return ok(views.html.admin.spare_parts.render(SparePart.pageOfOpenSpareParts(page, pageSize, sortBy, order, filter), sortBy, order, filter, pageSize));
    }
}
