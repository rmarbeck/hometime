package reporting;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fr.hometime.utils.CustomerWatchHelper;
import models.CustomerWatch;

public class CurrentOrdersReport implements MesurableReport {
	private static String TOTAL = "zzTotal";
	public String sStatus;
	public Float price = 0f;
	public int count = 1;
	public long watchValue = 0;
	
	
	private CurrentOrdersReport() {
		sStatus = TOTAL;
		price = 0f;
		watchValue = 0;
	}

	private CurrentOrdersReport(CustomerWatch currentWatch) {
		sStatus = CustomerWatchHelper.getStatusName(currentWatch);
		price = currentWatch.finalCustomerServicePrice.floatValue();
		watchValue = currentWatch.quotation!=null?currentWatch.quotation:0;
	}
	
	private void addPrice(Float price) {
		this.price+=price;
	}
	
	private void addValue(long watchValue) {
		this.watchValue+=watchValue;
	}
	
	private void addOne() {
		this.count++;
	}
		
	public static List<CurrentOrdersReport> generateReport() {
		return generateReport(() -> CustomerWatch.findAllUnderOurResponsability());
	}
	
	private static List<CurrentOrdersReport> generateReport(Supplier<List<CustomerWatch>> supplier) {
		HashMap<String, CurrentOrdersReport> reportBuilder = new HashMap<>();
		List<CustomerWatch> lines = supplier.get();
		lines.stream().forEach(line -> addNewLine(new CurrentOrdersReport(line), reportBuilder));
		return reportBuilder.values().stream().sorted(Comparator.comparing(CurrentOrdersReport::evaluateKey).reversed()).collect(Collectors.toList());
	}

	private static void addNewLine(CurrentOrdersReport newLine, HashMap<String, CurrentOrdersReport> reportBuilder) {
		String key = evaluateKey(newLine);
		if (reportBuilder.containsKey(key)) {
			reportBuilder.get(key).addPrice(newLine.price);
			reportBuilder.get(key).addOne();
			reportBuilder.get(key).addValue(newLine.watchValue);
		} else {
			reportBuilder.put(key, newLine);
		}
		addToTotal(newLine, reportBuilder);
	}
	
	private static void addToTotal(CurrentOrdersReport newLine, HashMap<String, CurrentOrdersReport> reportBuilder) {
		if (!reportBuilder.containsKey(TOTAL))
			reportBuilder.put(TOTAL, new CurrentOrdersReport());
		reportBuilder.get(TOTAL).addPrice(newLine.price);
		reportBuilder.get(TOTAL).addOne();
		reportBuilder.get(TOTAL).addValue(newLine.watchValue);
	}
	
	private static String evaluateKey(CurrentOrdersReport newLine) {
		return newLine.sStatus;
	}
	
	public static String toLink(int index) {
		return "to_link";
	}
	
}
