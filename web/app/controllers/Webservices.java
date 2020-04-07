package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.Logger;
import play.libs.Json;

import models.Brand;
import models.Feedback;

public class Webservices extends Controller {
	public static Result getBrands() {
		Logger.error("Validating secretKey, value received is : [{}]", request().getHeader("secretKey"));
		return ok(Json.toJson(Brand.findAll()));
	}
	
	public static Result getFeedbacks() {
		Logger.error("Validating secretKey, value received is : [{}]", request().getHeader("secretKey"));
		return ok(Json.toJson(Feedback.findAll()));
	}
}
