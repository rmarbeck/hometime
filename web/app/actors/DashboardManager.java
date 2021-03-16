package actors;

import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import models.OrderRequest;

public class DashboardManager extends UntypedActor {
	private final Set<ActorRef> dashActors = new HashSet<>();
	
	private DashboardManager() {
		System.err.println("Starting DashboardManager");
    	OrderRequest.getListener().addConsumer((model, action) -> this.self().tell("tick", this.self()));
    	OrderRequest.getListener().addConsumer((model, action) -> System.err.println("notifying action to actor"));
	}
	
    public static Props props() {
        return Props.create(DashboardActor.class);
    }
	
    private void addActor(ActorRef actorRef){
    	System.err.println("DashboardManager is adding an actor, number of actors will be "+(dashActors.size()+1));
    	dashActors.add(actorRef);
    }

    private void removeActor(ActorRef actorRef){
    	System.err.println("DashboardManager is removing an actor, number of actors will be "+(dashActors.size()-1));
    	dashActors.remove(actorRef);
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
    	System.err.println("--------------------------------------> DashboardManager is receiving a message");
        if (message instanceof JoinMessage) {
        	System.err.println("DashboardManager is joining an actor");
        	addActor(sender());
            getContext().watch(sender()); // Watch actor so we can detect when they die.
        } else if (message instanceof Terminated) {
            // One of our watched actor has died.
        	removeActor(sender());
        } else if (message instanceof String) {
        	System.err.println("--------------------------------------> DashboardManager is sending to all active actors ("+dashActors.size()+")");
            for (ActorRef dashActor : dashActors) {
            	dashActor.tell(message, getSelf());
            }
        }
    }
}
