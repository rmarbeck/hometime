package reporting;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fr.hometime.utils.CustomerWatchHelper;
import models.CustomerWatch;

public class CurrentOrdersReport {
	public String sStatus;
	public Float price = 0f;
	
	private CurrentOrdersReport(CustomerWatch currentWatch) {
		sStatus = CustomerWatchHelper.getStatusName(currentWatch);
		price = currentWatch.finalCustomerServicePrice.floatValue();
	}
	
	private void addPrice(Float price) {
		this.price+=price;
	}
		
	public static List<CurrentOrdersReport> generateReport() {
		return generateReport(() -> CustomerWatch.findAllUnderOurResponsability());
	}
	
	private static List<CurrentOrdersReport> generateReport(Supplier<List<CustomerWatch>> supplier) {
		HashMap<String, CurrentOrdersReport> reportBuilder = new HashMap<>();
		List<CustomerWatch> lines = supplier.get();
		for(CustomerWatch line : lines)
			addNewLine(new CurrentOrdersReport(line), reportBuilder);
		return reportBuilder.values().stream().sorted(Comparator.comparing(CurrentOrdersReport::evaluateKey).reversed()).collect(Collectors.toList());
	}

	private static void addNewLine(CurrentOrdersReport newLine, HashMap<String, CurrentOrdersReport> reportBuilder) {
		String key = evaluateKey(newLine);
		if (reportBuilder.containsKey(key)) {
			reportBuilder.get(key).addPrice(newLine.price);
		} else {
			reportBuilder.put(key, newLine);
		}
	}
	
	private static String evaluateKey(CurrentOrdersReport newLine) {
		return newLine.sStatus;
	}
	
}
