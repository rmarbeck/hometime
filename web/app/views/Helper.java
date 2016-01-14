package views;

import java.time.LocalDate;

public class Helper {
	public static java.util.Date parseDate(String toParse) {
		LocalDate dateParsed = LocalDate.parse(toParse);
		return new java.util.Date(dateParsed.toEpochDay());
	}
	
	public static java.util.Date parseDateInMillis(String toParse) {
		return new java.util.Date(Long.parseLong(toParse));
	}
}
