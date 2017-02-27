package controllers;

import java.util.List;

import models.Partner;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Partners extends Controller {
	public static Crud<Partner, Partner> crud = Crud.of(
			Partner.of(),
			views.html.admin.partner.ref(),
			views.html.admin.partner_form.ref(),
			views.html.admin.partners.ref());
}
