package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import play.libs.Json;

import models.Brand;
import models.Feedback;

public class Webservices extends Controller {
	public Result getBrands() {
		return ok(Json.toJson(Brand.findAll()));
	}
	
	public Result getFeedbacks() {
		return ok(Json.toJson(Feedback.findAll()));
	}
}
