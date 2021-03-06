package controllers;

import models.ExternalDocument;
import play.mvc.Controller;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
public class ExternalDocuments extends Controller {
	public static Crud<ExternalDocument, ExternalDocument> crud = Crud.of(
			ExternalDocument.of(),
			views.html.admin.external_document.ref(),
			views.html.admin.external_document_form.ref(),
			views.html.admin.external_documents.ref());
}
