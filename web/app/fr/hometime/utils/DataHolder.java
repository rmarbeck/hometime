package fr.hometime.utils;

import play.Logger;


/**
 * Helper for date manipulation
 * 
 * @author Raphael
 *
 */

public class DataHolder {
	public enum DataType {
		VALUE ("VALUE"),
		PRICE ("PRICE"),
	    BOOLEAN ("BOOLEAN");;
	    
		private String name = "";
		    
		DataType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static DataType fromString(String name) {
	        for (DataType action : DataType.values()) {
	            if (action.name.equals(name)) {
	                return action;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	String dataKey;
	DataType dataType;
	Float numericValue;
	Boolean booleanValue;
	
	protected DataHolder(String dataKey, DataType dataType, Float numericValue, Boolean booleanValue) {
		this.dataKey = dataKey;
		this.dataType = dataType;
		this.numericValue = numericValue;
		this.booleanValue = booleanValue;
	}
	
	public static DataHolder ofAsNumericValue(String dataKey, Float numericValue) {
		return new DataHolder(dataKey, DataType.VALUE, numericValue, new Boolean(false));
	}
	
	public static DataHolder ofAsPrice(String dataKey, Float numericValue) {
		return new DataHolder(dataKey, DataType.PRICE, numericValue, new Boolean(false));
	}
	
	public static DataHolder ofAsBoolean(String dataKey, boolean booleanValue) {
		return new DataHolder(dataKey, DataType.BOOLEAN, 0f, new Boolean(booleanValue));
	}

	public String getDataKey() {
		return dataKey;
	}

	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public Float getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(Float numericValue) {
		this.numericValue = numericValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
}