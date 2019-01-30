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
		appendWithLeadingSeparator(getBatteryChangePrice(brand), result);
		appendWithLeadingSeparator(getBatteryChangeAndWaterPrice(brand), result);
		appendWithLeadingSeparator(getLowServicePriceSimplePrice(brand), result);
		appendWithLeadingSeparator(getHighServicePriceSimplePrice(brand), result);
		appendWithLeadingSeparator(getLowServicePriceChronoPrice(brand), result);
		appendWithLeadingSeparator(getHighServicePriceChronoPrice(brand), result);
		appendWithLeadingSeparator(getLowServicePriceGmtPrice(brand), result);
		appendWithLeadingSeparator(getHighServicePriceGmtPrice(brand), result);
		appendWithLeadingSeparator(getLowServicePriceComplexPrice(brand), result);
		appendWithLeadingSeparator(getHighServicePriceComplexPrice(brand), result);
		appendWithLeadingSeparator(getHighEmergencyFactorPrice(brand), result);
		appendWithLeadingSeparator(getLowEmergencyFactorPrice(brand), result);
		
		return result.toString();
	}
	
	private static void appendWithLeadingSeparator(Long value, StringBuilder result) {
		result.append(",");
		result.append(value);
	}
	
	public static Brand getBrandFromId(Long id) {
		return Brand.findById(id);
	}

	public static Long getBatteryChangePrice(Brand brand) {
		return getPriceLevel(brand, Price::getBatteryChange);
	}
	
	public static Long getBatteryChangeAndWaterPrice(Brand brand) {
		return getPriceLevel(brand, Price::getBatteryChangeAndWater);
	}
	
	public static Long getBatteryChangeAndWaterAndPolishPrice(Brand brand) {
		return getPriceLevel(brand, Price::getBatteryChangeAndWaterAndPolish);
	}
	
	public static Long getLowServicePriceSimplePrice(Brand brand) {
		return getPriceLevel(brand, Price::getLowServicePriceSimple);
	}
	
	public static Long getHighServicePriceSimplePrice(Brand brand) {
		return getPriceLevel(brand, Price::getHighServicePriceSimple);
	}
	
	public static Long getLowServicePriceChronoPrice(Brand brand) {
		return getPriceLevel(brand, Price::getLowServicePriceChrono);
	}
	
	public static Long getHighServicePriceChronoPrice(Brand brand) {
		return getPriceLevel(brand, Price::getHighServicePriceChrono);
	}
	
	public static Long getLowServicePriceGmtPrice(Brand brand) {
		return getPriceLevel(brand, Price::getLowServicePriceGmt);
	}
	
	public static Long getHighServicePriceGmtPrice(Brand brand) {
		return getPriceLevel(brand, Price::getHighServicePriceGmt);
	}
	
	public static Long getLowServicePriceComplexPrice(Brand brand) {
		return getPriceLevel(brand, Price::getLowServicePriceComplex);
	}
	
	public static Long getHighServicePriceComplexPrice(Brand brand) {
		return getPriceLevel(brand, Price::getHighServicePriceComplex);
	}

	public static Long getLowEmergencyFactorPrice(Brand brand) {
		return getPriceLevel(brand, Price::getLowEmergencyFactor);
	}
	
	public static Long getHighEmergencyFactorPrice(Brand brand) {
		return getPriceLevel(brand, Price::getHighEmergencyFactor);
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
			return priceGetter.apply(priceFound.get());
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