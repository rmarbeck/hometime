package controllers;

import java.nio.charset.StandardCharsets;

import models.Authentication;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class Authentications extends Controller {
	public static Crud<models.Authentication, models.Authentication> crud = Crud.of(
			models.Authentication.of(views.html.admin.accounting.authentication.ref()),
			views.html.admin.accounting.authentication.ref(),
			views.html.admin.accounting.authentication_form.ref(),
			views.html.admin.accounting.authentications.ref());
	
	public static Result displayAuthentication(long id) {
		Authentication instance = Authentication.findById(id);
		
		if (instance != null && instance.documentData != null)
			return ok(new String(instance.documentData, StandardCharsets.UTF_8)).as("text/html; charset=utf-8");
		    
		return badRequest("error");
	}
}
