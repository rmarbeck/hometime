package controllers;

import play.mvc.Controller;
import play.mvc.Security;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class PostServiceCertificates extends Controller {
	public static Crud<models.PostServiceCertificate, models.PostServiceCertificate> crud = Crud.of(
			models.PostServiceCertificate.of(),
			views.html.admin.accounting.post_service_certificate.ref(),
			views.html.admin.accounting.post_service_certificate_form.ref(),
			views.html.admin.accounting.post_service_certificates.ref());
}
