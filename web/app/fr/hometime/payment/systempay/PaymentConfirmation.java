package fr.hometime.payment.systempay;

import static fr.hometime.payment.systempay.DataDictionnary.*;

import java.util.Map;

public class PaymentConfirmation extends PaymentForm {
	private PaymentConfirmation() {
		super();
	}
	
	public static PaymentConfirmation of(Map<String, String> values) {
		PaymentConfirmation instance = new PaymentConfirmation();
		if (values != null)
			for (String key : values.keySet())
				if (key.startsWith(PARAMETER_PREFIX))
						instance.addParameter(PaymentFormParameter.of(key, values.get(key)));

		return instance;
	}
	
	public String getOrderId() {
		return getParameterValueAsString(Helper.getParameterFullName(ORDER_ID));
	}
	
	public String getResult() {
		return getParameterValueAsString(Helper.getParameterFullName(RESULT));
	}
	
	public String getWarrantyResult() {
		return getParameterValueAsString(Helper.getParameterFullName(WARRANTY_RESULT));
	}
	
	public String getTransactionStatus() {
		return getParameterValueAsString(Helper.getParameterFullName(TRANSACTION_STATUS));
	}
	
	public int getEffectiveAmount() {
		return Integer.parseInt(getParameterValueAsString(Helper.getParameterFullName(EFFECTIVE_AMOUNT)));
	}
	
	public boolean containsWarnings() {
		if (getWarrantyResult().equals(WARRANTY_RESULT_OK) && getResult().equals(RESULT_OK) && isTransactionAuthorized())
			return false;
		return true;
	}
	
	public boolean isTransactionAuthorized() {
		return getTransactionStatus().startsWith(TRANSACTION_STATUS_OK_PATTERN);
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		for (PaymentFormParameter currentParam : this.getParameters())
			result.append(currentParam.getParameterName() + " - > " + currentParam.getValue() + "\n");
		return result.toString();
	}
}
