package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import scala.concurrent.duration.FiniteDuration;

public class DashboardActor extends UntypedActor {
	private static final long KEEP_ALIVE_FREQUENCY = 40l;
	private static final String KEEP_ALIVE_UNIT = "seconds";
	
    public static Props props(ActorRef out, ActorRef manager) {
        return Props.create(DashboardActor.class, out, manager);
    }
    
    private final ActorRef out;
    private final ActorRef manager;

    public DashboardActor(ActorRef out, ActorRef manager) {
    	Logger.info("Starting actor {"+this.self().path()+"}");
        this.out = out;
        this.manager = manager;
       
        this.self().tell(new InitMessage(), null);
        Akka.system().scheduler().schedule(
        		FiniteDuration.apply(KEEP_ALIVE_FREQUENCY, KEEP_ALIVE_UNIT),
        		FiniteDuration.apply(KEEP_ALIVE_FREQUENCY, KEEP_ALIVE_UNIT),
                this.getSelf(),
                new PingMessage(),
                Akka.system().dispatcher(),
                null);
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
		} else if (message instanceof PingMessage){
			Logger.debug("Pinging message {"+this.self().path()+"}");
			out.tell(Json.newObject().put("ping" , "ping"), self());
		} else {
			Logger.info("Actor just recevied an unsupported message ["+message.getClass()+"]");
		}
	}

	@Override
	public void postStop() throws Exception {
		Logger.info("Actor Stopped {"+this.self().path()+"}");
		super.postStop();
	}

}
