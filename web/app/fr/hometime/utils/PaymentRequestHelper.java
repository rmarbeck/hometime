package fr.hometime.utils;

import java.util.List;
import java.util.stream.Collectors;

import models.MailTemplateType;
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
    
	private static Optional<PaymentRequest> getFirstIfExists(List<PaymentRequest> list) {
		if (list != null && list.size() >= 1)
    		return Optional.of(list.get(0));
    	return Optional.empty();
	}

}
