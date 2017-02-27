package controllers;

import java.util.List;

import models.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Users extends Controller {
	public static Crud<User, User> crud = Crud.of(
			User.of(),
			views.html.admin.user.ref(),
			views.html.admin.user_form.ref(),
			views.html.admin.users.ref());
}
