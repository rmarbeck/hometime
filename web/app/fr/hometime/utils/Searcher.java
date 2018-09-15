package fr.hometime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.Customer;
import models.CustomerWatch;
import models.Searchable;
import models.WatchToSell;


/**
 * Searcher in registered searchable models
 * 
 * @author Raphael
 *
 */

public class Searcher {
	public static Optional<List<? extends Searchable>> search(String pattern) {
		
		Customer cust = new Customer();
		WatchToSell wts =  new WatchToSell();
		CustomerWatch custW =  new CustomerWatch();

		List<? extends Searchable> results = Stream.of(cust, wts, custW)
				.flatMap(s -> ((Optional<List<? extends Searchable>>) s.findMatchingIfAny(pattern)).orElse(emptyList()).stream())
				.collect(Collectors.toList());
		
		if (!results.isEmpty())
			return Optional.of(results);
		
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	private static List<? extends Searchable> emptyList() {
		return new ArrayList();
	}
	
	public static String generateDetails(List<String> values) {
		StringBuilder result = new StringBuilder();
		for(String value : values)
			if (value != null && !"".equals(value)) {
				if (result.length() != 0)
					result.append(" - ");
				result.append(value);
			}
		return result.toString();
	}
}