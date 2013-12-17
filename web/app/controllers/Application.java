package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render(""));
    }

    public static Result service() {
        return ok(service.render(""));
    }

    public static Result watch_collection() {
        return ok(watch_collection.render(""));
    }

}
