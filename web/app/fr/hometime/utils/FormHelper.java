package fr.hometime.utils;

import java.util.List;
import java.util.ArrayList;

/**
 * Helper for different usefull functions for form displaying
 * 
 * @author Raphael
 *
 */

public class FormHelper {
	public class KeysAndValues {
		private List<String> keys;
		private List<String> values;
		
		public KeysAndValues() {
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
		}
		
		public void add(String key, String value) {
			this.keys.add(key);
			this.values.add(value);
		}
		
		public List<String> getKeys() {
			return keys;
		}
		
		public List<String> getValues() {
			return values;
		}
	}
}