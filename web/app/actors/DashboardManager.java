package actors;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import fr.hometime.utils.DashboardManagerHelper;
import fr.hometime.utils.ListenableModel;

import static fr.hometime.utils.DashboardManagerHelper.manageUpdate;
import static fr.hometime.utils.DashboardManagerHelper.orderUpdate;
import static fr.hometime.utils.DashboardManagerHelper.appointmentsUpdate;
import static fr.hometime.utils.DashboardManagerHelper.customerWatchesAllocated;
import static fr.hometime.utils.DashboardManagerHelper.customerWatchesPriorized;
import static fr.hometime.utils.DashboardManagerHelper.customerWatchesEmergency;
import static fr.hometime.utils.DashboardManagerHelper.customerWatchesQuickWins;

import play.Logger;

public class DashboardManager extends UntypedActor {
	private final Set<ActorRef> dashActors = new HashSet<>();
	
	private DashboardManager() {
		Logger.debug("Starting DashboardManager");
		
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(orderUpdate(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(appointmentsUpdate(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(customerWatchesAllocated(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(customerWatchesPriorized(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(customerWatchesQuickWins(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(customerWatchesEmergency(), this.self(), model));
	}
	
    public static Props props() {
        return Props.create(DashboardActor.class);
    }
	
    private void addActor(ActorRef actorRef){
    	Logger.debug("DashboardManager is adding an actor, number of actors will be "+(dashActors.size()+1));
    	dashActors.add(actorRef);
    }

    private void removeActor(ActorRef actorRef){
    	Logger.debug("DashboardManager is removing an actor, number of actors will be "+(dashActors.size()-1));
    	dashActors.remove(actorRef);
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
    	Logger.debug("--------------------------------------> DashboardManager is receiving a message");
        if (message instanceof JoinMessage) {
        	Logger.debug("DashboardManager is joining an actor "+sender().path());
        	ActorRef newActor = getSender();
        	addActor(newActor);
            getContext().watch(newActor); // Watch actor so we can detect when they die.
            CompletableFuture.runAsync(() -> {
            	DashboardManagerHelper.forceUpdate(orderUpdate(), newActor);
            	DashboardManagerHelper.forceUpdate(appointmentsUpdate(), newActor);
            	DashboardManagerHelper.forceUpdate(customerWatchesAllocated(), newActor);
            	DashboardManagerHelper.forceUpdate(customerWatchesPriorized(), newActor);
            	DashboardManagerHelper.forceUpdate(customerWatchesQuickWins(), newActor);
            	DashboardManagerHelper.forceUpdate(customerWatchesEmergency(), newActor);
            });
        } else if (message instanceof Terminated) {
            // One of our watched actor has died.
        	removeActor(sender());
        } else if (message instanceof ForwardJsonMessage) {
        	Logger.debug("--------------------------------------> DashboardManager is sending to all active actors ("+dashActors.size()+")");
            for (ActorRef dashActor : dashActors) {
            	dashActor.tell(message, getSelf());
            }
        } else {
        	Logger.error("DashboardManager received un unkown message type");
        }
    }
}
