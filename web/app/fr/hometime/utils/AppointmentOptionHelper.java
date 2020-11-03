package fr.hometime.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.AppointmentOption;
import models.AppointmentRequest;


/**
 * Helper for appointment actions
 * 
 * @author Raphael
 *
 */

public class AppointmentOptionHelper {
	private static int APPOINTMENT_DEFAULT_DURATION_IN_MINUTES = 30;
	private static int APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES = 60;
	private static LocalTime APPOINTMENT_DEFAULT_FIRST_HOUR_AVAILABLE = LocalTime.MIDNIGHT.plusHours(10);
	private static LocalTime APPOINTMENT_DEFAULT_LAST_HOUR_AVAILABLE = LocalTime.MIDNIGHT.plusHours(19);
	private static List<DayOfWeek> DAYS_OPEN = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
	
	public static List<AppointmentOption> getAvailableAppointmentOptions() {
		List<AppointmentOption> allOptions = getAppointmentOptions();
		allOptions.removeAll(getUnavailableOptions());
		return allOptions;
	}
	
	private static List<AppointmentOption> getAppointmentOptions() {
		return Stream.iterate(getFirstPossibleAppointement(), d -> d.plusMinutes(APPOINTMENT_DEFAULT_DURATION_IN_MINUTES)).filter(AppointmentOptionHelper::isOpen).limit(60).map(d -> new AppointmentOption(d, true)).collect(Collectors.toList()); 
	}
	
	private static List<AppointmentOption> getUnavailableOptions() {
		List<AppointmentRequest> inFutureOnly = AppointmentRequest.findInFutureOnly();
		if (inFutureOnly != null)
			return inFutureOnly.stream().filter(AppointmentRequest::notAvailableForNewOption).map(current -> new AppointmentOption(convertToLocalDate(current.appointmentAsDate), false)).collect(Collectors.toList());
		return new ArrayList<AppointmentOption>();
	}
	
	private static boolean isOpen(LocalDateTime toTest) {
		if (DAYS_OPEN.contains(toTest.getDayOfWeek()) && isDuringOpenningHours(toTest))
			return true;
		return false; 
	}
	
	private static LocalDateTime getFirstPossibleAppointement() {
		LocalDateTime nextAvailable = LocalDateTime.now().plusMinutes(APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES).withSecond(0).withNano(0);
		int minutesInCurrentHour = nextAvailable.getMinute();

		if (minutesInCurrentHour < APPOINTMENT_DEFAULT_DURATION_IN_MINUTES)
			return nextAvailable.withMinute(APPOINTMENT_DEFAULT_DURATION_IN_MINUTES);
		return nextAvailable.withHour(nextAvailable.getHour()+1).withMinute(0);
	}
	
	private static boolean isDuringOpenningHours(LocalDateTime toTest) {
		if (toTest.toLocalTime().isBefore(APPOINTMENT_DEFAULT_FIRST_HOUR_AVAILABLE) || toTest.toLocalTime().isAfter(APPOINTMENT_DEFAULT_LAST_HOUR_AVAILABLE.minusMinutes(APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES)))
			return false;
		return true;
	}
	
	public static LocalDateTime convertToLocalDate(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(AppointmentOption.APPOINTMENT_ZONEID)
	      .toLocalDateTime();
	}
	
	public static String convertToLocalDateAsString(Date dateToConvert) {
	    return convertToLocalDate(dateToConvert).format(AppointmentOption.DATE_FORMATTER);
	}
	
	public static Date convertToDate(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(AppointmentOption.APPOINTMENT_ZONEID)
	      .toInstant());
	}
	
	public static Date convertToDateFromString(String dateToConvertAsString) {
	    return convertToDate(LocalDateTime.parse(dateToConvertAsString, AppointmentOption.DATE_FORMATTER));
	}
}