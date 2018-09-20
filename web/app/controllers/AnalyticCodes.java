package controllers;

import models.AnalyticCode;
import play.mvc.Controller;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class AnalyticCodes extends Controller {
	public static Crud<AnalyticCode, AnalyticCode> crud = Crud.of(
			AnalyticCode.of(),
			views.html.admin.accounting.analytic_code.ref(),
			views.html.admin.accounting.analytic_code_form.ref(),
			views.html.admin.accounting.analytic_codes.ref());
}
