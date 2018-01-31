package controllers;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.redirect;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import models.ExternalDocument;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(SecuredAdminOnly.class)
public class ExternalDocuments extends Controller {
	public static Crud<ExternalDocument, ExternalDocument> crud = Crud.of(
			ExternalDocument.of(),
			views.html.admin.external_document.ref(),
			views.html.admin.external_document_form.ref(),
			views.html.admin.external_documents.ref());
}
