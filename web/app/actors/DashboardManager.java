package actors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import fr.hometime.utils.DashboardManagerHelper;
import fr.hometime.utils.DashboardManagerHelper.ModelsProducer;
import fr.hometime.utils.ListenableModel;

import static fr.hometime.utils.DashboardManagerHelper.manageUpdate;
import static fr.hometime.utils.DashboardManagerHelper.orderUpdate;
import static fr.hometime.utils.DashboardManagerHelper.updateIfNeeded;
import static fr.hometime.utils.DashboardManagerHelper.appointmentsUpdate;
import static fr.hometime.utils.DashboardManagerHelper.customerWatchesAllocated;
import static fr.hometime.utils.DashboardManagerHelper.customerWatchesPriorized;
import static fr.hometime.utils.DashboardManagerHelper.customerWatchesEmergency;
import static fr.hometime.utils.DashboardManagerHelper.customerWatchesQuickWins;
import static fr.hometime.utils.DashboardManagerHelper.internalMessages;
import static fr.hometime.utils.DashboardManagerHelper.spareParts;
import static fr.hometime.utils.DashboardManagerHelper.stats;

import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;

public class DashboardManager extends UntypedActor {
	private static final long APPOINTMENT_REFRESH_FREQUENCY = 10l;
	private static final String APPOINTMENT_REFRESH_UNIT = "minutes";
	
	private static List<Supplier<ModelsProducer>> producers =  Arrays.asList(	() -> orderUpdate(),
			() -> appointmentsUpdate(),
			() -> customerWatchesAllocated(),
			() -> customerWatchesPriorized(),
			() -> customerWatchesQuickWins(),
			() -> customerWatchesEmergency(),
			() -> internalMessages(),
			() -> spareParts(),
			() -> stats());

	private final Set<ActorRef> dashActors = new HashSet<>();
	
	private DashboardManager() {
		Logger.debug("Starting DashboardManager");

		producers.stream().forEach(producer -> ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(producer.get(), this.self(), model)));
		
		/*ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(orderUpdate(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(appointmentsUpdate(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(customerWatchesAllocated(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(customerWatchesPriorized(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(customerWatchesQuickWins(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(customerWatchesEmergency(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(internalMessages(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(spareParts(), this.self(), model));
		ListenableModel.getListener().addConsumer((model, action) -> manageUpdate(stats(), this.self(), model));*/
        Akka.system().scheduler().schedule(
        		FiniteDuration.apply(APPOINTMENT_REFRESH_FREQUENCY, APPOINTMENT_REFRESH_UNIT),
        		FiniteDuration.apply(APPOINTMENT_REFRESH_FREQUENCY, APPOINTMENT_REFRESH_UNIT),
                () -> updateIfNeeded(appointmentsUpdate(), this.self()),
                Akka.system().dispatcher());
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
            	
            	producers.stream().forEach(producer -> DashboardManagerHelper.forceUpdate(producer.get(), newActor));
            	/*DashboardManagerHelper.forceUpdate(orderUpdate(), newActor);
            	DashboardManagerHelper.forceUpdate(appointmentsUpdate(), newActor);
            	DashboardManagerHelper.forceUpdate(customerWatchesAllocated(), newActor);
            	DashboardManagerHelper.forceUpdate(customerWatchesPriorized(), newActor);
            	DashboardManagerHelper.forceUpdate(customerWatchesQuickWins(), newActor);
            	DashboardManagerHelper.forceUpdate(customerWatchesEmergency(), newActor);
            	DashboardManagerHelper.forceUpdate(internalMessages(), newActor);
            	DashboardManagerHelper.forceUpdate(spareParts(), newActor);*/
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
