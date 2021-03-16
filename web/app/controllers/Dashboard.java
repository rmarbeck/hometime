package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import actors.DashboardActor;
import actors.DashboardManager;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import models.BuyRequest;
import models.CustomerWatch;
import models.OrderRequest;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.mvc.With;
import scala.concurrent.duration.FiniteDuration;
import views.html.*;
import views.html.admin.dashboard_ws;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR, models.User.Role.PARTNER, models.User.Role.CUSTOMER})
@With(NoCacheAction.class)
public class Dashboard extends Controller {
	public static final ActorRef manager = Akka.system().actorOf(Props.create(DashboardManager.class), "DashBoardManager2");
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static WebSocket<JsonNode> socket() {
		System.err.println("Starting websocket");
		System.err.println("----> "+manager.path());
	    return WebSocket.withActor((out) -> DashboardActor.props(out, manager));
	}
	
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static Result display() {
		return ok(dashboard_ws.render("", null/*Customer.findWithOpenTopic()*/, OrderRequest.findAllUnManaged(), BuyRequest.findAllUnReplied(5), CustomerWatch.findAllEmergencyOrderedByPriority(), models.SparePart.findAllToReceiveByCreationDateDesc(), models.CustomerWatch.findAllByWatchmaker(), models.AppointmentRequest.findCurrentAndInFutureOnly(), models.CustomerWatch.findAllUnderOurResponsabilityWithQuickWinPossibleOrderedByID(), models.InternalMessage.findAllActiveForDash(), 1));
    }
	
    public static Result test_websocket() {
    	return ok(test_websocket.render("toto"));
    }
}

