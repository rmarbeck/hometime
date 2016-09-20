package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentsReport {
	private final static String METHOD_KEY = "admin.report.payment.method.";
	
	public Date date;
	public String invoiceName;
	public String customerName;
	public Float amount;
	public String paymentMethodName;
	public String remark;
	
	private PaymentsReport(Payment payment) {
		this.date = payment.inBankDate;
		this.invoiceName = payment.invoice.uniqueAccountingNumber;
		this.customerName = payment.invoice.document.customer.getFullName();
		this.amount = payment.amountInEuros;
		this.paymentMethodName = METHOD_KEY+payment.paymentMethod.name();
		this.remark = payment.description;
	}
	
	public static List<PaymentsReport> generateReport() {
		List<PaymentsReport> report = new ArrayList<PaymentsReport>();
		List<Payment> payments = Payment.findAllByInBankDateAsc();
		if (listNotEmpty(payments))
			for(Payment payment : payments)
					report.add(new PaymentsReport(payment));
		return report;
	}
	
	private static boolean listNotEmpty(List<?> list) {
		return (list != null && list.size() != 0);
	}
}
