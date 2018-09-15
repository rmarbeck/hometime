package models;

import java.util.List;
import java.util.Optional;

public interface Searchable {
	public List<? extends Searchable> findMatching(String pattern);
	
	public default Optional<List<? extends Searchable>> findMatchingIfAny(String pattern) {
		return Optional.ofNullable(findMatching(pattern));
	}
	
	default String getType() {
		return this.getClass().getSimpleName();
	}
	
	default String getDisplayName() {
		return this.toString();
	}
	
	default String getDetails() {
		return "";
	}
	
	public Long retrieveId();
}
