package fr.hometime.utils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import models.WatchToSell;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class WatchToSellSerializer extends JsonSerializer<WatchToSell> {
	JsonGenerator jsonGenerator;
	WatchToSell instance;

	@Override
	public void serialize(WatchToSell watchToSell, JsonGenerator jsonGenerator,
			SerializerProvider serializerProvider) throws IOException,
			JsonProcessingException {
		this.jsonGenerator = jsonGenerator;
		this.instance = watchToSell;
		jsonGenerator.writeStartObject();
		writeStringFields(asList("id", "creationDate", "brand", "model", "reference", "serial", "serial2", "movement", "strap", "year", "status", "ownerStatus", "additionnalInfos", "additionnalModelInfos"));
		writeBooleanFields(asList("hasBox", "hasPapers", "isNew"));
		jsonGenerator.writeNumberField("price", instance.sellingPrice);
        jsonGenerator.writeEndObject();	
		
	}
	
	private void writeStringFields(List<String> fieldNames) throws JsonProcessingException, IOException {
		writeFields("String", fieldNames);
	}
	
	private void writeBooleanFields(List<String> fieldNames) throws JsonProcessingException, IOException {
		writeFields("Boolean", fieldNames);
	}
	
	private void writeFields(String type, List<String> fieldNames) throws JsonProcessingException, IOException {
		for (String fieldName : fieldNames)
			writeField(type, fieldName);
	}
	
	private void writeStringField(String fieldName) throws JsonProcessingException, IOException {
		writeField("String", fieldName);
	}
	
	private void writeBooleanField(String fieldName) throws JsonProcessingException, IOException {
		writeField("Boolean", fieldName);
	}
	
	private void writeField(String type, String fieldName) throws JsonProcessingException, IOException {
		Optional<?> fieldValue = getFieldValue(fieldName);
		if (fieldValue.isPresent()) {
			switch(type) {
				case "String":
					this.jsonGenerator.writeStringField(fieldName, getFieldValueAsString(fieldName).get());
					break;
				case "Boolean":
					this.jsonGenerator.writeBooleanField(fieldName, getFieldValueAsBoolean(fieldName).get());
					break;
			}
		} else {
			switch(type) {
				case "String":
					this.jsonGenerator.writeStringField(fieldName, "?");
					break;
				case "Boolean":
					this.jsonGenerator.writeBooleanField(fieldName, false);
					break;
			}
		}
	}
	
	private Optional<String> getFieldValueAsString(String fieldName) {
		return ReflectionHelper.getStringValue(instance, ReflectionHelper.guessPossibleAccessorMethodsForAField(fieldName));
	}
	
	@SuppressWarnings("unchecked")
	private Optional<Boolean> getFieldValueAsBoolean(String fieldName) {
		return ((Optional<Boolean>) getFieldValue(fieldName));
	}
	
	private Optional<?> getFieldValue(String fieldName) {
		return ReflectionHelper.getValue(instance, ReflectionHelper.guessPossibleAccessorMethodsForAField(fieldName));
	}

}
