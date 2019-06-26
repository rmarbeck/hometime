package controllers;

import java.nio.charset.StandardCharsets;

import models.CustomerWatch;
import models.SellingWarrant;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class SellingWarrants extends Controller {
	public static Crud<models.SellingWarrant, models.SellingWarrant> crud = Crud.of(
			models.SellingWarrant.of(views.html.admin.accounting.selling_warrant.ref()),
			views.html.admin.accounting.selling_warrant.ref(),
			views.html.admin.accounting.selling_warrant_form.ref(),
			views.html.admin.accounting.selling_warrants.ref());
	
	public static Result displayWarrant(long id) {
		SellingWarrant instance = SellingWarrant.findById(id);
		
		if (instance != null && instance.documentData != null)
			return ok(new String(instance.documentData, StandardCharsets.UTF_8)).as("text/html; charset=utf-8");
		    
		return badRequest("error");
	}
}
