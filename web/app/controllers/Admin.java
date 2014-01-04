package controllers;

import static play.data.Form.form;

import java.util.List;

import javax.persistence.Column;

import fr.hometime.utils.ActionHelper;
import models.Brand;
import models.OrderRequest;
import models.Picture;
import models.Watch;

import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.mvc.*;
import views.html.admin.*;

public class Admin extends Controller {

	public static Result index() {
        return ok(index.render("", OrderRequest.findAll()));
    }
}
