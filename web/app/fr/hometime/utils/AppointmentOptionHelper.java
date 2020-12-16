package fr.hometime.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
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
	private static int APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES = 90;
	private static LocalTime APPOINTMENT_DEFAULT_FIRST_HOUR_AVAILABLE = LocalTime.MIDNIGHT.plusHours(10);
	private static LocalTime APPOINTMENT_DEFAULT_LAST_HOUR_AVAILABLE = LocalTime.MIDNIGHT.plusHours(19);
	private static List<DayOfWeek> DAYS_OPEN = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
	
	private static int EXTENDED_APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES = 0;
	private static LocalTime EXTENDED_APPOINTMENT_DEFAULT_FIRST_HOUR_AVAILABLE = LocalTime.MIDNIGHT.plusHours(8);
	private static List<DayOfWeek> EXTENDED_DAYS_OPEN = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);
	
	public static List<AppointmentOption> getAvailableAppointmentOptions() {
		List<AppointmentOption> allOptions = getAppointmentOptions();
		allOptions.removeAll(getUnavailableOptions());
		return allOptions;
	}
	
	public static List<AppointmentOption> getExtendedAvailableAppointmentOptions() {
		List<AppointmentOption> allOptions = getExtendedAppointmentOptions();
		allOptions.removeAll(getUnavailableOptions());
		return allOptions;
	}
	
	private static List<AppointmentOption> getAppointmentOptions() {
		return Stream.iterate(getFirstPossibleAppointement(APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES), d -> d.plusMinutes(APPOINTMENT_DEFAULT_DURATION_IN_MINUTES)).filter(AppointmentOptionHelper::isOpenNormal).limit(60).map(d -> new AppointmentOption(d, true)).collect(Collectors.toList()); 
	}
	
	private static List<AppointmentOption> getExtendedAppointmentOptions() {
		return Stream.iterate(getFirstPossibleAppointement(EXTENDED_APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES), d -> d.plusMinutes(APPOINTMENT_DEFAULT_DURATION_IN_MINUTES)).filter(AppointmentOptionHelper::isOpenExtended).limit(200).map(d -> new AppointmentOption(d, true)).collect(Collectors.toList()); 
	}
	
	private static List<AppointmentOption> getUnavailableOptions() {
		List<AppointmentRequest> inFutureOnly = AppointmentRequest.findInFutureOnly();
		if (inFutureOnly != null)
			return inFutureOnly.stream().filter(AppointmentRequest::notAvailableForNewOption).map(current -> new AppointmentOption(convertToLocalDate(current.appointmentAsDate), false)).collect(Collectors.toList());
		return new ArrayList<AppointmentOption>();
	}
	
	private static boolean isOpenNormal(LocalDateTime toTest) {
		return isOpen(toTest, DAYS_OPEN, AppointmentOptionHelper::isDuringNormalOpenningHours); 
	}
	
	private static boolean isOpenExtended(LocalDateTime toTest) {
		return isOpen(toTest, EXTENDED_DAYS_OPEN, AppointmentOptionHelper::isDuringExtendedOpenningHours);
	}
	
	private static boolean isOpen(LocalDateTime toTest, List<DayOfWeek> days, Predicate<LocalDateTime> openingHoursFilter) {
		if (days.contains(toTest.getDayOfWeek()) && openingHoursFilter.test(toTest))
			return true;
		return false; 
	}
	
	private static LocalDateTime getFirstPossibleAppointement(int delayBeforeNext) {
		LocalDateTime nextAvailable = LocalDateTime.now(AppointmentOption.APPOINTMENT_ZONEID).plusMinutes(delayBeforeNext).withSecond(0).withNano(0);
		int minutesInCurrentHour = nextAvailable.getMinute();

		if (minutesInCurrentHour < APPOINTMENT_DEFAULT_DURATION_IN_MINUTES)
			return nextAvailable.withMinute(APPOINTMENT_DEFAULT_DURATION_IN_MINUTES);
		return nextAvailable.plusHours(1).withMinute(0);
	}
	
	private static boolean isDuringNormalOpenningHours(LocalDateTime toTest) {
		return isDuringOpenningHours(toTest, APPOINTMENT_DEFAULT_FIRST_HOUR_AVAILABLE, APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES);
	}
	
	private static boolean isDuringExtendedOpenningHours(LocalDateTime toTest) {
			return isDuringOpenningHours(toTest, EXTENDED_APPOINTMENT_DEFAULT_FIRST_HOUR_AVAILABLE, EXTENDED_APPOINTMENT_DEFAULT_DELAY_BEFORE_NEXT_AVAILABLE_IN_MINUTES);
	}
	
	private static boolean isDuringOpenningHours(LocalDateTime toTest, LocalTime firstHour, int delayBeforeNext) {
		if (toTest.toLocalTime().isBefore(firstHour) || toTest.toLocalTime().isAfter(APPOINTMENT_DEFAULT_LAST_HOUR_AVAILABLE.minusMinutes(delayBeforeNext)))
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