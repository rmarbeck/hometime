package fr.hometime.utils;

import java.util.Date;


/**
 * Helper for date manipulation
 * 
 * @author Raphael
 *
 */

public class DateHelper {
	public static int nbDaysTillToday(Date dateInThePast) {
		return nbDaysBetween(new Date(System.currentTimeMillis()), dateInThePast);
	}
	
	public static int nbDaysFromToday(Date dateInTheFuture) {
		return nbDaysBetween(dateInTheFuture, new Date(System.currentTimeMillis()));
	}
	
	public static int nbDaysBetween(Date date) {
		return nbDaysBetween(new Date(System.currentTimeMillis()), date);
	}
	
	public static int nbDaysBetween(Date date1, Date date2) {
		return Math.round(((date1.getTime() - date2.getTime()) / 1000 / 3600 / 24));
	}
}