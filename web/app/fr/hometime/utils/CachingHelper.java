package fr.hometime.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Security class containing functions for security purpose 
 * 
 * @author Raphael
 *
 */

public class CachingHelper {
	private static final Duration CACHE_VALIDITY_TIME = Duration.ofMillis(1000);
	public static Map<String, Optional<?>> cache = null;
	public static Map<String, Instant> cacheValidity = null;
	
	public static Optional<?> getDataFromCache(String key, Function<String, Optional<?>> dataGetter) {
		return getDataFromCache(key, dataGetter, "");
	}
	
	public static Optional<?> getDataFromCache(String key, Function<String, Optional<?>> dataGetter, String keyPrefix) {
		if (cache == null) {
			cache = new HashMap<String, Optional<?>>();
			cacheValidity = new HashMap<String, Instant>();
		}
		if (cacheValidity.containsKey(keyPrefix+key) && cacheValidity.get(keyPrefix+key).isAfter(Instant.now()))
			return cache.get(keyPrefix+key);
		
		cache.put(keyPrefix+key, dataGetter.apply(key));
		cacheValidity.put(keyPrefix+key, Instant.now().plus(CACHE_VALIDITY_TIME));
		
		return cache.get(keyPrefix+key);
	}
}
