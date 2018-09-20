package controllers;

import models.User;
import play.mvc.Controller;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class Users extends Controller {
	public static Crud<User, User> crud = Crud.of(
			User.of(),
			views.html.admin.user.ref(),
			views.html.admin.user_form.ref(),
			views.html.admin.users.ref());
}
