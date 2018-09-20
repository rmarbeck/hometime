package controllers;

import models.Partner;
import play.mvc.Controller;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class Partners extends Controller {
	public static Crud<Partner, Partner> crud = Crud.of(
			Partner.of(),
			views.html.admin.partner.ref(),
			views.html.admin.partner_form.ref(),
			views.html.admin.partners.ref());
}
