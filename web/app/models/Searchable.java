package models;

import java.util.List;
import java.util.Optional;

public interface Searchable {
	public Optional<List<? extends Searchable>> findMatching(String pattern);
	
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
