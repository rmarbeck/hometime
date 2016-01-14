package controllers;

import models.WatchToSell;
import play.mvc.Controller;

public class WatchesToSell extends Controller {
	public static Crud<WatchToSell, WatchToSell> crud = Crud.of(
			WatchToSell.of(),
			views.html.admin.watch_to_sell.ref(),
			views.html.admin.watch_to_sell_form.ref(),
			views.html.admin.watches_to_sell.ref());
}
