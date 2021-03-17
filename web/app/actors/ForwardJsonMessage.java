package actors;

import com.fasterxml.jackson.databind.JsonNode;

public class ForwardJsonMessage {
	private final JsonNode jsonNode;
	
	private ForwardJsonMessage(JsonNode node) {
		jsonNode = node;
	}
	
	public JsonNode getJsonMessage() {
		return jsonNode;
	}
	
	public static ForwardJsonMessage of(JsonNode node) {
		return new ForwardJsonMessage(node);
	}

}
