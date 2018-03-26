package fr.hometime.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import play.Logger;


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
		return Math.round(((differenceBetweenTwoDates(date1, date2)) / 1000 / 3600 / 24));
	}
	
	public static boolean isBeforeNow(Date date) {
		if (date != null)
			return differenceBetweenNowAndADate(date) > 0;
			
		return false;
	}
	
	public static boolean isAfterNow(Date date) {
		return !isBeforeNow(date);
	}
	
	public static boolean isWithinSameMonth(Date date1, Date date2) {
		ZonedDateTime dateZ1 = ZonedDateTime.ofInstant(date1.toInstant(), ZoneId.systemDefault());
		ZonedDateTime dateZ2 = ZonedDateTime.ofInstant(date2.toInstant(), ZoneId.systemDefault());
		return dateZ1.getMonth() == dateZ2.getMonth();
	}
	
	private static long differenceBetweenTwoDates(Date date1, Date date2) {
		return date1.getTime() - date2.getTime();
	}
	
	private static long differenceBetweenNowAndADate(Date date) {
		return differenceBetweenTwoDates(new Date(System.currentTimeMillis()), date);
	}
	
	public static Instant endOfTheDay(Date date) {
		LocalDate currentDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
		
		LocalDateTime endOfTheDay = currentDate.atTime(23, 59, 59);
	
		return endOfTheDay.atZone(ZoneId.systemDefault()).toInstant();
	}
	
	public static Instant beginningOfTheDay(Date date) {
		LocalDate currentDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
		
		LocalDateTime startOfTheDay = currentDate.atStartOfDay();
	
		return startOfTheDay.atZone(ZoneId.systemDefault()).toInstant();
	}
	
	public static Date toDate(Instant instant) {
		return new Date(instant.toEpochMilli());
	}
	
	public static String asShortDate(Date date) {
		if (date != null)
			return new SimpleDateFormat("dd/MM/yyyy", new Locale("fr", "FR")).format(date).toString();
		return "?";
	}
}