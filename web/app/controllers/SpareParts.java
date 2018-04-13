package controllers;

import java.util.List;

import models.Customer;
import models.Partner;
import models.SparePart;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(SecuredAdminOrCollaboratorOrPartnerOnly.class)
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
