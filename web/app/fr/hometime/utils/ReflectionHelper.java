package fr.hometime.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import akka.japi.Option;

public class ReflectionHelper {

	public static List<String> guessPossibleAccessorMethodsForAField(String fieldName) {
		if (fieldName == null || fieldName.equals(""))
			return new ArrayList<String>();
		
		List<String> possibleAccessorMethods = new ArrayList<String>();
		String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1).toLowerCase();
		
		possibleAccessorMethods.add("get"+capitalizedFieldName);
		possibleAccessorMethods.add("is"+capitalizedFieldName);
		possibleAccessorMethods.add("has"+capitalizedFieldName);
		possibleAccessorMethods.add("is_"+capitalizedFieldName);
		possibleAccessorMethods.add("has_"+capitalizedFieldName);
		
		return possibleAccessorMethods;
	}
	
	public static Optional<?> getValue(Object object, List<String> fieldPossibleAccessorMethods) {
		if ( object == null
				|| fieldPossibleAccessorMethods == null
				|| fieldPossibleAccessorMethods.size() == 0 )
			return Optional.empty();
		
		for (String accessor : fieldPossibleAccessorMethods) {
			Optional<?> value = getValue(object, accessor);
			if (value.isPresent())
				return value;
		}
		return Optional.empty();

	}
	
	public static Optional<?> getValue(Object object, String accessorMethod) {
		if ( object == null
				|| accessorMethod == null
				|| accessorMethod.equals("") )
			return Optional.empty();
		
		Class<?> c = object.getClass();
		try {
			return Optional.ofNullable(c.getMethod(accessorMethod).invoke(object));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			// TODO Auto-generated catch block
			return Optional.empty();
		}
	}
	
	
	public static Optional<String> getStringValue(Object object, List<String> fieldPossibleAccessorMethods) {
		return getStringValue(getValue(object, fieldPossibleAccessorMethods));
	}
	
	public static Optional<String> getStringValue(Object object, String fieldAccessorMethod) {
		return getStringValue(getValue(object, fieldAccessorMethod));
	}
	
	private static Optional<String> getStringValue(Optional<?> object) {
		if (!object.isPresent())
			return Optional.empty();
		
		String typeOfReturn = object.get().getClass().getName();
		switch (typeOfReturn) {
		  case "java.lang.String":
			  return Optional.of((java.lang.String) object.get());
		  case "java.lang.Long":
			  return Optional.of(Long.toString((java.lang.Long) object.get()));
		  case "java.lang.Float":
			  return Optional.of(Float.toString((java.lang.Float) object.get()));
		  case "java.util.Date":
			  return Optional.of(Long.toString(((java.util.Date) object.get()).getTime()));
		  case "java.lang.Boolean":
			  return Optional.of(((java.lang.Boolean) object.get()).toString());
		  case "java.lang.Integer" :
			  return Optional.of(object.get().toString());
		  default:
			  if (typeOfReturn.startsWith("models."))
				  return Optional.of(object.get().toString());
			  return Optional.of("unsupported type ["+typeOfReturn +"] - " +object.get().toString());
		}
	}
}
