package models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import play.Logger;

public class AppointmentOption {
	public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";
	public static final String DATE_TIME_FORMAT_TO_DISPLAY = "EEEE d MMMM à HH:mm";
	public static final Locale APPOINTMENT_LOCALE = Locale.FRANCE;
	public static final ZoneId APPOINTMENT_ZONEID =  ZoneId.of("Europe/Paris");
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, APPOINTMENT_LOCALE);
	public static final DateTimeFormatter DATE_FORMATTER_TO_DISPLAY = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_TO_DISPLAY, APPOINTMENT_LOCALE);
	
	public String id;
	
	public LocalDateTime datetime;
	
	public boolean available;
	
	public String datetimeAsStringNiceToDisplay;
	
	public AppointmentOption() {
		this(LocalDateTime.now(), true);
	}
	
	public AppointmentOption(LocalDateTime option) {
		this(LocalDateTime.now(), true);
	}
	
	public AppointmentOption(LocalDateTime option, boolean available) {
		this(generateId(option), option, available);
	}
	
	
	public AppointmentOption(String id, LocalDateTime option, boolean available) {
		this.id = id;
		this.datetime = option;
		this.available = available;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(AppointmentOption.class))
			return this.id.equals(((AppointmentOption)obj).id);
		return false;
	}
	
	public String toString() {
		return datetimeWithAvailability()+" ("+id+")";
	}
	
	public String getDatetimeAsString() {
		return formatDatetime(datetime);
	}
	
	public String getDatetimeAsStringNiceToDisplay() {
		return datetime.format(DATE_FORMATTER_TO_DISPLAY);
	}
	
	public String datetimeWithAvailability() {
		return datetime.format(DATE_FORMATTER_TO_DISPLAY)+" <- "+(available?"disponible":"réservé");
	}
	
	private static String generateId(LocalDateTime ldtime) {
		return formatDatetime(ldtime);
	}

	private static String formatDatetime(LocalDateTime ldtime) {
		return ldtime.format(DATE_FORMATTER);
	}
}
