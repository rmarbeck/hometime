package fr.hometime.utils;

import com.fasterxml.jackson.databind.JsonNode;

public interface Jsonable {
	public JsonNode toJson();
}
