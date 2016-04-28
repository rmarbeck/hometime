package fr.watchnext.store.utils.payment.systempay;

import static fr.watchnext.store.utils.payment.systempay.DataDictionnary.PARAMETER_PREFIX;

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

}
