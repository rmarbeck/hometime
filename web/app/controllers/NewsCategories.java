package controllers;

import play.mvc.Controller;
import play.mvc.Security;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class NewsCategories extends Controller {
	public static Crud<models.NewsCategory, models.NewsCategory> crud = Crud.of(
			models.NewsCategory.of(),
			views.html.admin.news_category.ref(),
			views.html.admin.news_category_form.ref(),
			views.html.admin.news_categories.ref());
}
