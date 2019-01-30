package fr.hometime.utils;

import java.util.Optional;
import java.util.function.Function;

import models.Brand;
import models.Price;

/**
 * Helper for Prices
 * 
 * @author Raphael
 *
 */

public class PriceHelper {
	public static String getPricesForAutoOrder(Brand brand) {
		StringBuilder result = new StringBuilder();
		Optional<Price> priceFound = getBrandPrice(brand);
		
		if (priceFound.isPresent()) {
			appendWithLeadingSeparator(getBatteryChangePrice(priceFound), result);
			appendWithLeadingSeparator(getBatteryChangeAndWaterPrice(priceFound), result);
			appendWithLeadingSeparator(getLowServicePriceSimplePrice(priceFound), result);
			appendWithLeadingSeparator(getHighServicePriceSimplePrice(priceFound), result);
			appendWithLeadingSeparator(getLowServicePriceChronoPrice(priceFound), result);
			appendWithLeadingSeparator(getHighServicePriceChronoPrice(priceFound), result);
			appendWithLeadingSeparator(getLowServicePriceGmtPrice(priceFound), result);
			appendWithLeadingSeparator(getHighServicePriceGmtPrice(priceFound), result);
			appendWithLeadingSeparator(getLowServicePriceComplexPrice(priceFound), result);
			appendWithLeadingSeparator(getHighServicePriceComplexPrice(priceFound), result);
			appendWithLeadingSeparator(getHighEmergencyFactorPrice(priceFound), result);
			appendWithLeadingSeparator(getLowEmergencyFactorPrice(priceFound), result);
		}
		
		return result.toString();
	}
	
	private static void appendWithLeadingSeparator(Long value, StringBuilder result) {
		result.append(",");
		result.append(value);
	}
	
	public static Brand getBrandFromId(Long id) {
		return Brand.findById(id);
	}

	public static Long getBatteryChangePrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getBatteryChange);
	}

	public static Long getBatteryChangeAndWaterPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getBatteryChangeAndWater);
	}
	
	public static Long getBatteryChangeAndWaterAndPolishPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getBatteryChangeAndWaterAndPolish);
	}
	
	public static Long getLowServicePriceSimplePrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getLowServicePriceSimple);
	}
	
	public static Long getHighServicePriceSimplePrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getHighServicePriceSimple);
	}
	
	public static Long getLowServicePriceChronoPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getLowServicePriceChrono);
	}
	
	public static Long getHighServicePriceChronoPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getHighServicePriceChrono);
	}
	
	public static Long getLowServicePriceGmtPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getLowServicePriceGmt);
	}
	
	public static Long getHighServicePriceGmtPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getHighServicePriceGmt);
	}
	
	public static Long getLowServicePriceComplexPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getLowServicePriceComplex);
	}
	
	public static Long getHighServicePriceComplexPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getHighServicePriceComplex);
	}

	public static Long getLowEmergencyFactorPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getLowEmergencyFactor);
	}
	
	public static Long getHighEmergencyFactorPrice(Optional<Price> price) {
		return getPriceLevel(price, Price::getHighEmergencyFactor);
	}
	
	
	
	public static Long getBatteryChangePrice(Brand brand) {
		return getBatteryChangePrice(getBrandPrice(brand));
	}
	
	
	public static Long getBatteryChangeAndWaterPrice(Brand brand) {
		return getBatteryChangeAndWaterPrice(getBrandPrice(brand));
	}
	
	public static Long getBatteryChangeAndWaterAndPolishPrice(Brand brand) {
		return getBatteryChangeAndWaterAndPolishPrice(getBrandPrice(brand));
	}
	
	public static Long getLowServicePriceSimplePrice(Brand brand) {
		return getLowServicePriceSimplePrice(getBrandPrice(brand));
	}
	
	public static Long getHighServicePriceSimplePrice(Brand brand) {
		return getHighServicePriceSimplePrice(getBrandPrice(brand));
	}
	
	public static Long getLowServicePriceChronoPrice(Brand brand) {
		return getLowServicePriceChronoPrice(getBrandPrice(brand));
	}
	
	public static Long getHighServicePriceChronoPrice(Brand brand) {
		return getHighServicePriceChronoPrice(getBrandPrice(brand));
	}
	
	public static Long getLowServicePriceGmtPrice(Brand brand) {
		return getLowServicePriceGmtPrice(getBrandPrice(brand));
	}
	
	public static Long getHighServicePriceGmtPrice(Brand brand) {
		return getHighServicePriceGmtPrice(getBrandPrice(brand));
	}
	
	public static Long getLowServicePriceComplexPrice(Brand brand) {
		return getLowServicePriceComplexPrice(getBrandPrice(brand));
	}
	
	public static Long getHighServicePriceComplexPrice(Brand brand) {
		return getHighServicePriceComplexPrice(getBrandPrice(brand));
	}

	public static Long getLowEmergencyFactorPrice(Brand brand) {
		return getLowEmergencyFactorPrice(getBrandPrice(brand));
	}
	
	public static Long getHighEmergencyFactorPrice(Brand brand) {
		return getHighEmergencyFactorPrice(getBrandPrice(brand));
	}
	
	public static Long getRoundedPrice(Brand brand, Function<Brand, Long> getRawPrice) {
		return getRoundedPrice(getRawPrice.apply(brand));
	}
	
	public static Long getRoundedPrice(Long value) {
		return value/10*10;
	}
	
	private static Long getPriceLevel(Brand brand, Function<Price, Long> priceGetter) {
		Optional<Price> priceFound = getBrandPrice(brand);
		if (priceFound.isPresent())
			return getPriceLevel(priceFound, priceGetter);
		return -1l;
	}
	
	private static Long getPriceLevel(Optional<Price> price, Function<Price, Long> priceGetter) {
		if (price.isPresent())
			return priceGetter.apply(price.get());
		return -1l;
	}
	
	private static Optional<Price> getBrandPrice(Brand brand) {
		return Optional.ofNullable(getPriceForSpecificBrand(brand).orElse(getPriceForCategoryOfBrand(brand).orElse(null)));
	}
	
	private static Optional<Price> getPriceForSpecificBrand(Brand brand) {
		return getPriceForBrand(brand, Price::findByBrand);
	}
	
	private static Optional<Price> getPriceForCategoryOfBrand(Brand brand) {
		return getPriceForBrand(brand, Price::findByBrandQuartzCategory);
	}
	
	private static Optional<Price> getPriceForBrand(Brand brand, Function<Brand, Price> priceSelector) {
		if (brand == null)
			return Optional.empty();
		Price foundPrice = priceSelector.apply(brand);
		if (foundPrice != null)
			return Optional.of(foundPrice);
		return Optional.empty();
	}
}