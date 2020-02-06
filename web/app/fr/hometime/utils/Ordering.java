package fr.hometime.utils;

import java.util.Optional;

public class Ordering {
	Optional<String> sortBy;
	Optional<String> order;
	
	private Ordering(String sortBy, String order) {
		this.sortBy = checkAndSet(sortBy);
		this.order = checkAndSet(order);
	}
	
	private Ordering(String sortBy, String order, String defaultSortBy, String defaultOrder) {
		if (checkAndSet(sortBy).isPresent()) {
			this.sortBy = checkAndSet(sortBy);
			this.order = checkAndSet(order);
		} else {
			this.sortBy = checkAndSet(defaultSortBy);
			this.order = checkAndSet(defaultOrder);
		}
	}
	
	private static Optional<String> checkAndSet(String value) {
		if (value == null || value.equals(""))
			return Optional.empty();
		return Optional.of(value);
	}
	
	public static Ordering of(String sortBy, String order) {
		return new Ordering(sortBy, order);
	}
	
	public static Ordering of(String sortBy, String order, String defaultSortBy, String defaultOrder) {
		if (checkAndSet(sortBy).isPresent())
			return new Ordering(sortBy, order);
		return new Ordering(defaultSortBy, defaultOrder);
	}
	
	public String asSql() {
		return (sortBy.orElse("") + " " + order.orElse(""));
	}
}