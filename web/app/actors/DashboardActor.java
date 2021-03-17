package actors;

import java.time.Instant;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.AppointmentRequest;
import models.OrderRequest;
import play.libs.Json;

import play.Logger;

public class DashboardActor extends UntypedActor {
	
    public static Props props(ActorRef out, ActorRef manager) {
        return Props.create(DashboardActor.class, out, manager);
    }
    
    private final ActorRef out;
    private final ActorRef manager;

    public DashboardActor(ActorRef out, ActorRef manager) {
    	Logger.debug("Starting actor {"+this.self().path()+"}");
        this.out = out;
        this.manager = manager;
       
        this.self().tell(new InitMessage(), null);
    }
    
    @Override
    public void preStart() {
    	manager.tell(new JoinMessage(), getSelf());
    }
	
	@Override
	public void onReceive(Object message) throws Exception {
		Logger.debug("Actor is receiving a message");
		if (message instanceof InitMessage) {
			Logger.debug("Initing {"+this.self().path()+"}");
		} else if (message instanceof ForwardJsonMessage){
			Logger.debug("Forwarding message {"+this.self().path()+"}");
			out.tell(((ForwardJsonMessage) message).getJsonMessage(), self());
		} else {
			ObjectMapper mapper = new ObjectMapper();
			out.tell(Json.newObject().put("type" , "unreplied").put("message", "I received your message: " + message + Instant.now()).put("unmanaged", mapper.writeValueAsString(OrderRequest.findAllUnManaged().stream().map(OrderRequest::toJson).collect(Collectors.toList()))), self());
			out.tell(Json.newObject().put("type" , "appointments").put("appointments", mapper.writeValueAsString(models.AppointmentRequest.findCurrentAndInFutureOnly().stream().map(AppointmentRequest::toJson).collect(Collectors.toList()))), self());	
		}
	}

	@Override
	public void postStop() throws Exception {
		Logger.debug("Actor Stopped {"+this.self().path()+"}");
		super.postStop();
	}

}
