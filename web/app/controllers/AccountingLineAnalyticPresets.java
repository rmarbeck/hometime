package controllers;

import models.AccountingLineAnalyticPreset;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class AccountingLineAnalyticPresets extends Controller {
	public static Crud<AccountingLineAnalyticPreset, AccountingLineAnalyticPreset> crud = Crud.of(
			AccountingLineAnalyticPreset.of(),
			views.html.admin.accounting.analytic_preset.ref(),
			views.html.admin.accounting.analytic_preset_form.ref(),
			views.html.admin.accounting.analytic_presets.ref());
	
	public static Result createItem(Long id) {
		return ok("toto");
	}
}
