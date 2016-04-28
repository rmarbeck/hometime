package fr.watchnext.store.utils.payment.systempay;

import java.util.Optional;

public class PaymentFormParameter {
	public enum ParameterType {
	    STRING ("STRING"),
	    INTEGER ("INTEGER"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2");
	    
		private String name = "";
		ParameterType(String name){
		    this.name = name;
		}
		public String toString(){
		    return name;
		}
		public int intValue() {
			return Integer.valueOf(name);
		}
		public static ParameterType fromString(String name) {
	        for (ParameterType type : ParameterType.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	private String shortName;
	private ParameterType type;
	private Optional<String> stringValue = Optional.empty();
	private Optional<Integer> intValue = Optional.empty();
	private boolean mandatory = false;
	
	private PaymentFormParameter(String shortOrLongName, Boolean mandatory) {
		if (shortOrLongName.startsWith(DataDictionnary.PARAMETER_PREFIX)) {
			this.shortName = shortOrLongName.substring(DataDictionnary.PARAMETER_PREFIX.length());
		} else {
			this.shortName = shortOrLongName;
		}
		this.mandatory = mandatory;
	}
	
	private PaymentFormParameter(String shortOrLongName, String value, Boolean mandatory) {
		this(shortOrLongName, mandatory);
		this.type = ParameterType.STRING;
		this.stringValue = Optional.of(value);
	}
	
	private PaymentFormParameter(String shortOrLongName, int value, Boolean mandatory) {
		this(shortOrLongName, mandatory);
		this.type = ParameterType.INTEGER;
		this.intValue = Optional.of(value);
	}
	
	public static PaymentFormParameter of(String shortOrLongName, String value, Boolean mandatory) {
		return new PaymentFormParameter(shortOrLongName, value, mandatory);
	}
	
	public static PaymentFormParameter of(String shortOrLongName, int value, Boolean mandatory) {
		return new PaymentFormParameter(shortOrLongName, value, mandatory);
	}
	
	public static PaymentFormParameter of(String shortOrLongName, String value) {
		return PaymentFormParameter.of(shortOrLongName, value, false);
	}
	
	public static PaymentFormParameter of(String shortOrLongName, int value) {
		return PaymentFormParameter.of(shortOrLongName, value, false);
	}
	
	public String getParameterName() {
		return Helper.getParameterPrefix() + shortName;
	}
	
	public String getValue() {
		switch(type) {
			case STRING:
				return stringValue.orElse("Error Missing Value");
			case INTEGER:
				if (intValue.isPresent())
					return intValue.get().toString();
				return "Error Missing Value";
			default:
				return "Not supported";
		}
	}
	
	public Boolean isMandatory() {
		return mandatory;
	}
}
