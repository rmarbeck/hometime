package controllers;

import fr.hometime.utils.LiveConfigConstants;
import fr.hometime.utils.SecurityHelper;
import models.LiveConfig;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR})
@With(NoCacheAction.class)
public class PDF extends Controller {
	private static final String REQUEST_HEADER_FORWARDED_USER_EMAIL = "token"; 
	public static Promise<Result> testPDF(String path) {
		WSRequestHolder holder = WS.url("https://fathomless-fjord-51125.herokuapp.com/"+path);
		WSRequestHolder complexHolder = holder.setHeader(SecurityHelper.REQUEST_HEADER_TRUSTED_KEY, LiveConfig.getString(LiveConfigConstants.SECRET_KEY_FOR_PDF))
				.setHeader(REQUEST_HEADER_FORWARDED_USER_EMAIL, SecurityHelper.getLoggedInUserEmail(session()))
		        .setTimeout(10000);
		Promise<WSResponse> responsePromise = complexHolder.get();
		return responsePromise.map(response -> {
			response().setContentType("application/pdf");
			response().setHeader("content-disposition", response.getHeader("content-disposition"));
			return ok(response.asByteArray());
		});
	}
}
