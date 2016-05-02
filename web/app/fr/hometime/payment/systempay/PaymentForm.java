package fr.hometime.payment.systempay;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PaymentForm {
	protected Map<String, PaymentFormParameter> parameters;
	
	protected PaymentForm() {
		parameters = new HashMap<String, PaymentFormParameter>();
	}
	
	public void addParameter(PaymentFormParameter parameter) {
		this.parameters.put(parameter.getParameterName(), parameter);
	}
	
	public Collection<PaymentFormParameter> getParameters() {
		return this.parameters.values();
	}
	
	public Set<String> getParameterFullNames() {
		return this.parameters.keySet();
	}
	
	public String getParameterValueAsString(String parameterFullName) {
		return this.parameters.get(parameterFullName).getValue();
	}
	
	public String getSignature() {
		return Helper.generateSignature(this);
	}
}
