package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import play.libs.Json;

import models.Brand;
import models.Feedback;

public class Webservices extends Controller {
	public static Result getBrands() {
		return ok(Json.toJson(Brand.findAll()));
	}
	
	public static Result getFeedbacks() {
		return ok(Json.toJson(Feedback.findAll()));
	}
}
