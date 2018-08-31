package controllers;

import models.AccountingLineAnalyticPresetItem;
import models.CustomerWatch;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(SecuredAdminOnly.class)
public class AccountingLineAnalyticPresetItems extends Controller {
	public static Crud<AccountingLineAnalyticPresetItem, AccountingLineAnalyticPresetItem> crud = Crud.of(
			AccountingLineAnalyticPresetItem.of(),
			views.html.admin.accounting.analytic_preset_item.ref(),
			views.html.admin.accounting.analytic_preset_item_form.ref(),
			views.html.admin.accounting.analytic_preset_items.ref());
	
	public static Result createItemForPreset(Long presetId) {
		AccountingLineAnalyticPresetItem instance = new AccountingLineAnalyticPresetItem();
		models.AccountingLineAnalyticPreset preset = models.AccountingLineAnalyticPreset.findById(presetId);
		
		if (preset != null) {
			instance.accountingLineAnalyticPreset = preset;
		}
		return crud.create(Form.form(AccountingLineAnalyticPresetItem.class).fill(instance));
	}
}
