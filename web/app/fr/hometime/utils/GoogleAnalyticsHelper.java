package fr.hometime.utils;

import play.mvc.Http.Context;


/**
 * Helper for different actions
 * 
 * @author Raphael
 *
 */

public class GoogleAnalyticsHelper {
	
	public static String FLASH_FLAG = "push_ga_vars";
	public static String CUSTOM_VARIABLES_NB = "pgv_size";
	public static String CUSTOM_VARIABLES_PREPEND = "pgv_";
	public static String CUSTOM_VARIABLES_SLOT = "slot";
	public static String CUSTOM_VARIABLES_NAME = "name";
	public static String CUSTOM_VARIABLES_VALUE = "value";
	public static String CUSTOM_VARIABLES_SCOPE = "scope";
	
	public static String FLASH_EVENT_FLAG = "push_ga_events";
	public static String EVENT_NB = "pgv_event_size";
	public static String EVENT_PREPEND = "pgv_event_";
	public static String EVENT_CATEGORY = "category";
	public static String EVENT_ACTION = "action";
	
	public static int SLOT_PROSPECT = 1;
	public static int SLOT_CAMPAIGN_BOOLEAN = 2;
	public static int SLOT_CAMPAIGN_VALUE = 3;
	public static int SLOT_USER_LOGGED_BOOLEAN = 4;

	public enum Level {
		//Object created statically
		PAGE (3),
		SESSION (2),
		VISIT (1);

		private int value = 0;

		Level(int value){
			this.value = value;
		}

		public String toString(){
			return ""+value;
		}
	}

	public static void pushCustomVariable(int slot, String name, String value, Level level, Context ctx) {
		String currentValue = ctx.flash().get(CUSTOM_VARIABLES_NB);
		int currentSize = 0;
		if (currentValue!=null) {
			currentSize = Integer.parseInt(ctx.flash().get(CUSTOM_VARIABLES_NB));
		} else {
			ctx.flash().put(FLASH_FLAG, "true");
		}
		ctx.flash().put(CUSTOM_VARIABLES_NB, ""+(currentSize+1));
		ctx.flash().put(CUSTOM_VARIABLES_PREPEND+currentSize+"."+CUSTOM_VARIABLES_SLOT, ""+slot);
		ctx.flash().put(CUSTOM_VARIABLES_PREPEND+currentSize+"."+CUSTOM_VARIABLES_NAME, ""+name);
		ctx.flash().put(CUSTOM_VARIABLES_PREPEND+currentSize+"."+CUSTOM_VARIABLES_VALUE, ""+value);
		ctx.flash().put(CUSTOM_VARIABLES_PREPEND+currentSize+"."+CUSTOM_VARIABLES_SCOPE, ""+level.value);
		

	}
	
	public static void pushEvent(String category, String action, Context ctx) {
		String currentValue = ctx.flash().get(EVENT_NB);
		int currentSize = 0;
		if (currentValue!=null) {
			currentSize = Integer.parseInt(ctx.flash().get(EVENT_NB));
		} else {
			ctx.flash().put(FLASH_EVENT_FLAG, "true");
		}
		ctx.flash().put(EVENT_NB, ""+(currentSize+1));
		ctx.flash().put(EVENT_PREPEND+currentSize+"."+EVENT_CATEGORY, ""+category);
		ctx.flash().put(EVENT_PREPEND+currentSize+"."+EVENT_ACTION, ""+action);
	}
}
