package controllers;

import play.mvc.Controller;
import play.mvc.Security;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class News extends Controller {
	public static Crud<models.News, models.News> crud = Crud.of(
			models.News.of(),
			views.html.admin.news.ref(),
			views.html.admin.news_form.ref(),
			views.html.admin.news_list.ref());
}
