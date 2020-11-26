package controllers;

import play.mvc.Controller;
import play.mvc.Security;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class InternalMessages extends Controller {
	public static Crud<models.InternalMessage, models.InternalMessage> crud = Crud.of(
			models.InternalMessage.of(),
			views.html.admin.internal_message.ref(),
			views.html.admin.internal_messages_form.ref(),
			views.html.admin.internal_messages.ref());
}
