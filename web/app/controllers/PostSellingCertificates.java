package controllers;

import play.mvc.Controller;
import play.mvc.Security;

@Security.Authenticated(SecuredAdminOnly.class)
public class PostSellingCertificates extends Controller {
	public static Crud<models.PostSellingCertificate, models.PostSellingCertificate> crud = Crud.of(
			models.PostSellingCertificate.of(),
			views.html.admin.accounting.post_selling_certificate.ref(),
			views.html.admin.accounting.post_selling_certificate_form.ref(),
			views.html.admin.accounting.post_selling_certificates.ref());
}
