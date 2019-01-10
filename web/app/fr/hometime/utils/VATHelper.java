package fr.hometime.utils;

/**
 * Helper for VAT
 * 
 * @author Raphael
 *
 */

public class VATHelper {
	public static float VAT_RATE = 0.20f;

	public static float getVATRate() {
		return VAT_RATE;
	}
	
	public static float getPriceAfterVAT(float netPrice) {
		return netPrice * (1 + getVATRate());
	}
	
	public static float getPriceBeforeVAT(float rawPrice) {
		return rawPrice / (1 + getVATRate());
	}
	
	public static float getVATAmountForNetPrice(float netPrice) {
		return netPrice * getVATRate();
	}
}