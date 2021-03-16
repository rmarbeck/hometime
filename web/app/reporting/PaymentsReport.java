package reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import models.Payment;

public class PaymentsReport {
	private final static String METHOD_KEY = "admin.report.payment.method.";
	
	public Date date;
	public Date creationDate;
	public String invoiceName;
	public String customerName;
	public Float amount;
	public String paymentMethodName;
	public String remark;
	
	private PaymentsReport(Payment payment) {
		this.date = payment.inBankDate;
		this.creationDate = payment.creationDate;
		this.invoiceName = payment.invoice.uniqueAccountingNumber;
		this.customerName = payment.invoice.document.customer.getFullName();
		this.amount = payment.amountInEuros;
		this.paymentMethodName = METHOD_KEY+payment.paymentMethod.name();
		this.remark = payment.description;
	}
	
	public static List<PaymentsReport> generateReportEnhanced() {
		return generateReportEnhanced(() -> Payment.findAllByInBankDateDescForReporting());
	}
	
	public static List<PaymentsReport> generateReportEnhancedCreationDateDESC() {
		return generateReportEnhanced(() -> Payment.findAllByCreationDateDescForReporting());
	}
	
	private static List<PaymentsReport> generateReportEnhanced(Supplier<List<Payment>> paymentReader) {
		List<PaymentsReport> report = new ArrayList<PaymentsReport>();
		List<Payment> payments = paymentReader.get();
		if (listNotEmpty(payments))
			payments.stream().forEach(payment -> report.add(new PaymentsReport(payment)));
		return report;
	}
	
	private static boolean listNotEmpty(List<?> list) {
		return (list != null && list.size() != 0);
	}
}
