package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import actors.DashboardActor;
import models.BuyRequest;
import models.CustomerWatch;
import models.OrderRequest;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.mvc.With;
import views.html.*;
import views.html.admin.dashboard_ws;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR, models.User.Role.PARTNER, models.User.Role.CUSTOMER})
@With(NoCacheAction.class)
public class Dashboard extends Controller {
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static WebSocket<JsonNode> socket() {
		System.err.println("Starting websocket");
	    return WebSocket.withActor(DashboardActor::props);
	}
	
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static Result display() {
		return ok(dashboard_ws.render("", null/*Customer.findWithOpenTopic()*/, OrderRequest.findAllUnManaged(), BuyRequest.findAllUnReplied(5), CustomerWatch.findAllEmergencyOrderedByPriority(), models.SparePart.findAllToReceiveByCreationDateDesc(), models.CustomerWatch.findAllByWatchmaker(), models.AppointmentRequest.findCurrentAndInFutureOnly(), models.CustomerWatch.findAllUnderOurResponsabilityWithQuickWinPossibleOrderedByID(), models.InternalMessage.findAllActiveForDash(), 1));
    }
	
    public static Result test_websocket() {
    	return ok(test_websocket.render("toto"));
    }
}

