package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.Logger;
import play.libs.Json;
import fr.hometime.utils.AppointmentOptionHelper;
import fr.hometime.utils.LiveConfigHelper;
import fr.hometime.utils.StringHelper;

import static fr.hometime.utils.LiveConfigConstants.*;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import models.Brand;
import models.Feedback;
import models.LiveConfig;
import models.Price;
import models.NewsAdapter;

public class Webservices extends Controller {
	public static String SECRET_KEY_FOR_WS_HEADER = "SECRET_KEY";
	
	public static Result getBrands() {
		return sendIfAuthorized(Json.toJson(Brand.findAll()));
	}
	
	public static Result getFeedbacks() {
		return sendIfAuthorized(Json.toJson(Feedback.findAll()));
	}
	
	public static Result getPrices() {
		return sendIfAuthorized(Json.toJson(Price.findAll()));
	}
	
	public static Result getNews() {
		return sendIfAuthorized(Json.toJson(NewsAdapter.findAll()));
	}
	
	public static Result getLiveConfig() {
		return ok(Json.toJson(LiveConfig.findAll()));
	}
	
	public static Result getAppointmentOptions() {
		return sendIfAuthorized(Json.toJson(AppointmentOptionHelper.getAvailableAppointmentOptions()));
	}
	
	public static Result getAppointmentOptionsFree() {
		return ok(Json.toJson(AppointmentOptionHelper.getAvailableAppointmentOptions()));
	}
	
	private static Result sendIfAuthorized(JsonNode result) {
		if (!isAuthorized())
			return unauthorized();
		return ok(result);
	}
	
	public static boolean isAuthorized() {
		Optional<String> waitedValue = LiveConfigHelper.tryToGetString(SECRET_KEY_FOR_FRIENDLY_LOCATION);
		String receivedValue = request().getHeader(SECRET_KEY_FOR_WS_HEADER);
		
		boolean checkResult = false;
		if (waitedValue.isPresent())
			checkResult = waitedValue.get().equals(receivedValue);
		
		if (checkResult == false)
			Logger.error("Validating secretKey, value received is : [{}], should be [{}] => {}", receivedValue, StringHelper.maskPartially(waitedValue.orElse("unset")), checkResult);
		return checkResult;
	}
}
