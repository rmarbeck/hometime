package controllers;

import models.AccountingLineAnalytic;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class AccountingLineAnalytics extends Controller {
	public static Crud<AccountingLineAnalytic, AccountingLineAnalytic> crud = Crud.of(
			AccountingLineAnalytic.of(),
			views.html.admin.accounting.analytic.ref(),
			views.html.admin.accounting.analytic_form.ref(),
			views.html.admin.accounting.analytics.ref());
	
	public static Result createItem(Long id) {
		return ok("toto");
	}
	
	public static Result createForAccountingLine(long id) {
		AccountingLineAnalytic instance = new AccountingLineAnalytic();
		models.AccountingLine accountingLine = models.AccountingLine.findById(id);
		
		if (accountingLine != null) {
			instance.accountingLine = accountingLine;
		}
		return crud.create(Form.form(AccountingLineAnalytic.class).fill(instance));
    }
}
