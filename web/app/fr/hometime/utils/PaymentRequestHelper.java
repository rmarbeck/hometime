package fr.hometime.utils;

import java.util.List;
import java.util.Optional;

import models.PaymentRequest;

public class PaymentRequestHelper {
	public static Optional<PaymentRequest> getFromAccessKey(String accessKey) {
    	if (accessKey == null)
    		return Optional.empty();

    	List<PaymentRequest> requests = PaymentRequest.find.where().eq("access_key", accessKey).findList();
    	return getFirstIfExists(requests);
    }
    
    public static Optional<PaymentRequest> getValidRequestFromAccessKey(String accessKey) {
    	Optional<PaymentRequest> request = getFromAccessKey(accessKey);
    	
    	if (request.isPresent())
    		if (request.get().isValid())
    			return request;
    	
   		return Optional.empty();
    }
    
    public static Optional<PaymentRequest> getLastFromOrderId(String orderId) {
    	if (orderId == null)
    		return Optional.empty();
    	
    	List<PaymentRequest> requests = PaymentRequest.find.where().eq("order_number", orderId).orderBy("creation_date DESC").findList();
    	return getFirstIfExists(requests);
    }
    
    public static Optional<PaymentRequest> getBestFitFromOrderId(String orderId, int orderAmountX100) {
    	if (orderId == null)
    		return Optional.empty();
    	
    	List<PaymentRequest> requests = PaymentRequest.find.where().eq("order_number", orderId).orderBy("creation_date DESC").findList();
    	return getBestFitIfExists(requests, orderAmountX100);
    }
    
	private static Optional<PaymentRequest> getFirstIfExists(List<PaymentRequest> list) {
		if (list != null && list.size() >= 1)
    		return Optional.of(list.get(0));
    	return Optional.empty();
	}
	
	private static Optional<PaymentRequest> getBestFitIfExists(List<PaymentRequest> list, int orderAmountX100) {
		if (list != null && list.size() >= 1) {
			return Optional.of(list.stream().min((value1, value2) -> comparator(value1.priceInEuros, value2.priceInEuros, orderAmountX100)).get());
		}

    	return Optional.empty();
	}
	
	private static int comparator(float value1, float value2, int orderAmountX100) {
		if (Math.abs(value1 * 100 - orderAmountX100) < Math.abs(value2 * 100 - orderAmountX100))
			return -1;
		if (Math.abs(value1 * 100 - orderAmountX100) > Math.abs(value2 * 100 - orderAmountX100))
			return 1;
		return 0;
	}

}
