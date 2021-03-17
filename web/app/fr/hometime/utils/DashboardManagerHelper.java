package fr.hometime.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import actors.ForwardJsonMessage;
import akka.actor.ActorRef;
import controllers.Accounting.TriConsumer;
import models.AppointmentRequest;
import models.CustomerWatch;
import models.OrderRequest;
import play.Logger;
import play.libs.Json;

public class DashboardManagerHelper {
	private static class ModelsProducer {
		private Supplier<List<? extends Jsonable>> supplier;
		private String key;
		private Predicate<ListenableModel> filter;
		
		private ModelsProducer(Supplier<List<? extends Jsonable>> supplier, String key, Predicate<ListenableModel> filter) {
			this.supplier = supplier;
			this.key = key;
			this.filter = filter;
		}
		
		public static ModelsProducer of(Supplier<List<? extends Jsonable>> supplier, String key, Predicate<ListenableModel> filter) {
			return new ModelsProducer(supplier, key, filter);
		}
		
	}
	
	private static String JSON_TYPE = "type";
	private static String ORDER_REQUESTS = "orderRequest";
	private static String APPOINTMENTS = "appointments";
	private static String C_WATCH_ALLOCATED = "customerWatchesAllocated";
	private static String C_WATCH_PRIORITIZED = "customerWatchesPrioritized";
	
	private static Map<String, Integer> lastHashesSent = new HashMap<>();
	
	public static ModelsProducer orderUpdate() {
		return ModelsProducer.of(OrderRequest::findAllUnManaged, ORDER_REQUESTS, (model) -> model instanceof OrderRequest);
	}
	
	public static ModelsProducer appointmentsUpdate() {
		return ModelsProducer.of(AppointmentRequest::findCurrentAndInFutureOnly, APPOINTMENTS, (model) -> model instanceof AppointmentRequest);
	}
	
	public static ModelsProducer customerWatchesAllocated() {
		return ModelsProducer.of(CustomerWatch::findAllByWatchmaker, C_WATCH_ALLOCATED, (model) -> model instanceof CustomerWatch);
	}
	
	public static ModelsProducer customerWatchesPriorized() {
		return ModelsProducer.of(CustomerWatch::findAllEmergencyOrderedByPriority, C_WATCH_PRIORITIZED, (model) -> model instanceof CustomerWatch);
	}
	
	public static TriConsumer<Supplier<List<? extends Jsonable>>, String, ActorRef> manage(Boolean forceUpdate) {
		return (supplier, type, manager) -> {
			Logger.debug(type+" is updated, managing it ****************");
			JsonNode node = createJson(supplier, type).get();
			if (forceUpdate || hasChanged(node)) {
				Logger.debug(type+" has changed ****************");
				manager.tell(ForwardJsonMessage.of(node), manager);
			} else {
				Logger.debug(type+" has NOT changed ****************");
			}
		};
	}

	public static void forceUpdate(ModelsProducer producer, ActorRef manager) {
		manage(true).accept(producer.supplier, producer.key, manager);
	}
	
	public static void manageUpdate(ModelsProducer producer, ActorRef manager, ListenableModel model) {
		if (producer.filter.test(model))
			manage(false).accept(producer.supplier, producer.key, manager);
	}
	
	//return ok(dashboard.render("", null/*Customer.findWithOpenTopic()*/, OrderRequest.findAllUnManaged(), BuyRequest.findAllUnReplied(5), CustomerWatch.findAllEmergencyOrderedByPriority(), models.SparePart.findAllToReceiveByCreationDateDesc(), models.CustomerWatch.findAllByWatchmaker(), models.AppointmentRequest.findCurrentAndInFutureOnly(), models.CustomerWatch.findAllUnderOurResponsabilityWithQuickWinPossibleOrderedByID(), models.InternalMessage.findAllActiveForDash(), page));
	
	private static boolean hasChanged(JsonNode toCheck) {
		String currentType = toCheck.findValue(JSON_TYPE).asText();
		Integer newHash = toHash(toCheck);
		Logger.debug(" New Hash is "+newHash);
		if (lastHashesSent.containsKey(currentType)) {
			Logger.debug(" Last Hash was "+lastHashesSent.get(currentType));
			if (lastHashesSent.get(currentType).equals(newHash))
				return false;
		}
		lastHashesSent.put(currentType, newHash);
		return true;
	}
	
	private static Integer toHash(JsonNode node) {
		Logger.debug("Node is **************** : "+node.toString());
		return Integer.valueOf(node.toString().hashCode());
	}
	
	private static Optional<JsonNode> createJson(Supplier<List<? extends Jsonable>> supplier, String type) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return Optional.of(Json.newObject().put(JSON_TYPE , type).put(type, mapper.writeValueAsString(supplier.get().stream().map(Jsonable::toJson).collect(Collectors.toList()))));
		} catch (JsonProcessingException e) {
			Logger.error("Unable to create Json list for type {"+type+"}");
			e.printStackTrace();
		}
		return Optional.of(Json.newObject().put(JSON_TYPE , type).put(type, mapper.createArrayNode()));
	}

}
