package controllers;

import java.util.List;

import models.WatchToSell;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(SecuredAdminOnly.class)
public class WatchesToSell extends Controller {
	public static Crud<WatchToSell, WatchToSell> crud = Crud.of(
			WatchToSell.of(),
			views.html.admin.watch_to_sell.ref(),
			views.html.admin.watch_to_sell_form.ref(),
			views.html.admin.watches_to_sell.ref());
	
	public static Result watchesAsJSon() {
		List<WatchToSell> watches = WatchToSell.findAllByPurchaseDateAsc();
		return ok(Json.toJson(watches));
    }
	
	public static Result duplicateWatch(Long id) {
		WatchToSell duplicated = WatchToSell.duplicate(id);
		return crud.create(Form.form(WatchToSell.class).fill(duplicated));
	}
}
