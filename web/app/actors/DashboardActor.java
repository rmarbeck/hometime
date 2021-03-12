package actors;

import java.time.Instant;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.OrderRequest;
import play.libs.Akka;
import play.libs.Json;
import scala.concurrent.duration.FiniteDuration;

public class DashboardActor extends UntypedActor {
    public static Props props(ActorRef out) {
        return Props.create(DashboardActor.class, out);
    }
    
    private final ActorRef out;

    public DashboardActor(ActorRef out) {
    	System.err.println("Starting actor");
        this.out = out;
        Akka.system().scheduler().schedule(
        		FiniteDuration.fromNanos(2000000000),
        		FiniteDuration.fromNanos(2000000000),
                this.getSelf(),
                "tick : ",
                Akka.system().dispatcher(),
                null);
    }
	
	@Override
	public void onReceive(Object message) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		if (message instanceof String) {
            out.tell(Json.newObject().put("type" , "unreplied").put("message", "I received your message: " + message + Instant.now()).put("unmanaged", mapper.writeValueAsString(OrderRequest.findAllUnManaged().stream().map(OrderRequest::toJson).limit(Double.valueOf((Math.random()*10)%3).longValue()).collect(Collectors.toList()))), self());
        }
		if (message instanceof JsonNode) {
			out.tell(Json.newObject().put("type" , "unreplied").put("message", "I received your message: " + message + Instant.now()).put("unmanaged", mapper.writeValueAsString(OrderRequest.findAllUnManaged().stream().map(OrderRequest::toJson).limit(Double.valueOf((Math.random()*10)%3).longValue()).collect(Collectors.toList()))), self());
        }
	}

	@Override
	public void postStop() throws Exception {
		System.err.println("Actor Stopped");
		
		super.postStop();
	}

}
