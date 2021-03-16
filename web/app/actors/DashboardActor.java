package actors;

import java.time.Instant;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.AppointmentRequest;
import models.OrderRequest;
import play.libs.Akka;
import play.libs.Json;

public class DashboardActor extends UntypedActor {
	
    public static Props props(ActorRef out, ActorRef manager) {
        return Props.create(DashboardActor.class, out, manager);
    }
    
    private final ActorRef out;
    private final ActorRef manager;

    public DashboardActor(ActorRef out, ActorRef manager) {
    	System.err.println("Starting actor {"+this.self().path().name()+"}");
        this.out = out;
        this.manager = manager;
        /*System.err.println("?????> "+Akka.system().actorSelection("/application/user/DashBoardManager2").path());
        System.err.println("?????> "+Akka.system().actorSelection("DashBoardManager2").path());
        System.err.println("?????> "+Akka.system().actorSelection("/user/DashBoardManager2").anchor().path());
        System.err.println("?????> "+Akka.system().actorSelection("user/DashBoardManager2").anchor().path());
        
        getContext().actorSelection("akka://application/user/DashBoardManager2").anchor().tell("test", this.self());
        getContext().actorSelection("/application/user/DashBoardManager2").anchor().tell("test", this.self());
        getContext().actorSelection("application/user/DashBoardManager2").anchor().tell("test", this.self());
        getContext().actorSelection("/user/DashBoardManager2").anchor().tell("test", this.self());
        getContext().actorSelection("user/DashBoardManager2").anchor().tell("test", this.self());
        getContext().actorSelection("/DashBoardManager2").anchor().tell("test", this.self());
        getContext().actorSelection("DashBoardManager2").anchor().tell("test", this.self());
        getContext().actorSelection("DashBoardManager2").tell("test", this.self());
        getContext().actorSelection("user/DashBoardManager2").tell("test", this.self());*/
        
        this.self().tell("tick", null);
    }
    
    @Override
    public void preStart() {
    	manager.tell(new JoinMessage(), getSelf());
    }
	
	@Override
	public void onReceive(Object message) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		System.err.println("Receiving message");
		out.tell(Json.newObject().put("type" , "unreplied").put("message", "I received your message: " + message + Instant.now()).put("unmanaged", mapper.writeValueAsString(OrderRequest.findAllUnManaged().stream().map(OrderRequest::toJson).collect(Collectors.toList()))), self());
		out.tell(Json.newObject().put("type" , "appointments").put("appointments", mapper.writeValueAsString(models.AppointmentRequest.findCurrentAndInFutureOnly().stream().map(AppointmentRequest::toJson).collect(Collectors.toList()))), self());
	}

	@Override
	public void postStop() throws Exception {
		System.err.println("Actor Stopped {"+this.self().path().name()+"}");
		
		super.postStop();
	}

}
