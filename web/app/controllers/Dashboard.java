package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import actors.DashboardActor;
import actors.DashboardManager;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.mvc.With;
import views.html.admin.dashboard_ws;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR, models.User.Role.PARTNER, models.User.Role.CUSTOMER})
@With(NoCacheAction.class)
public class Dashboard extends Controller {
	public static final ActorRef manager = Akka.system().actorOf(Props.create(DashboardManager.class), "DashBoardManager");
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static WebSocket<JsonNode> socket() {
		System.err.println("Starting websocket");
	    return WebSocket.withActor((out) -> DashboardActor.props(out, manager));
	}
	
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static Result defaultDash() {
		return display(1);
    }
	
	@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR})
	public static Result display(Integer page) {
		return ok(dashboard_ws.render("", page));
    }
}

