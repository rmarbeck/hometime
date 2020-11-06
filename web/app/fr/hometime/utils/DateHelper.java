package fr.hometime.utils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

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
	
	public static Date addOpenDays(int nbOfDays) {
		LocalDate newDate = LocalDate.now().plusDays(nbOfDays);
		if (newDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || newDate.getDayOfWeek().equals(DayOfWeek.SUNDAY))
			newDate = newDate.plusDays(2);
		return convertToDate(newDate.atStartOfDay());
	}
	
	private static long differenceBetweenTwoDates(Date date1, Date date2) {
		Long date1AsLong = convertToLocalDate(date1).toLocalDate().atStartOfDay().toEpochSecond(ZoneId.of("Europe/Paris").getRules().getOffset(LocalDateTime.now()));
		Long date2AsLong = convertToLocalDate(date2).toLocalDate().atStartOfDay().toEpochSecond(ZoneId.of("Europe/Paris").getRules().getOffset(LocalDateTime.now()));
		return date1AsLong - date2AsLong;
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
	
	public static Instant startTimer() {
		return Instant.now();
	}
	
	public static long getTimeFromInMillis(Instant previousInstant) {
		return Duration.between(previousInstant, Instant.now()).toMillis();
	}
	
	public static Date todayPlusNDays(int nbDays) {
		return Date.from(Instant.now().plus(Period.ofDays(nbDays)));
	}
	
	public static Optional<Date> firstDate(Date date1, Date date2) {
		if (date1 == null)
			return Optional.ofNullable(date2);
		if (date2 == null || date1.before(date2))
			return Optional.of(date1);
		return Optional.of(date2);
	}
	
	public static Date getFirstDate(Date date1, Date date2) {
		return firstDate(date1, date2).orElse(null);
	}
	
	public static LocalDateTime convertToLocalDate(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	public static Date convertToDate(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}
}