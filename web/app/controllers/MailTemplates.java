package controllers;

import java.util.List;

import models.MailTemplate;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class MailTemplates extends Controller {
	public static Crud<MailTemplate, MailTemplate> crud = Crud.of(
			MailTemplate.of(),
			views.html.admin.mail_template.ref(),
			views.html.admin.mail_template_form.ref(),
			views.html.admin.mail_templates.ref());
	
	public static Result duplicateMailTemplate(Long id) {
		MailTemplate duplicated = MailTemplate.duplicate(id);
		return crud.create(Form.form(MailTemplate.class).fill(duplicated));
	}
}
