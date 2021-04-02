package reporting;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fr.hometime.utils.DateHelper;
import models.CustomerWatch;

public class WatchmakerProductionReport implements MesurableReport {
	class ProductionStats {
		public int count = 0;
		public long turnover = 0;
		
		public ProductionStats(CustomerWatch currentWatch) {
			count = 1;
			turnover = currentWatch.finalCustomerServicePrice;
		}
		
		public ProductionStats absorb(ProductionStats statsToMergeIn) {
			count = count + statsToMergeIn.count;
			turnover = turnover + statsToMergeIn.turnover;
			return this;
		}
		
		public int getCount() {
			return count;
		}
		
		public long getTurnover() {
			return turnover;
		}
	}
		
	public String periodAsString;
	public Map<String, ProductionStats> stats;
	
	private WatchmakerProductionReport(CustomerWatch currentWatch) {
		stats = new HashMap<>();
		stats.put(findStatsMergeKey(currentWatch), new ProductionStats(currentWatch));
		periodAsString = DateHelper.asMonthYear(currentWatch.lastStatusUpdate);
	}
	
	private void mergeStats(WatchmakerProductionReport newReport) {
		if (periodAsString.equals(newReport.periodAsString))
			newReport.stats.entrySet().stream().forEach(entry -> this.stats.get(entry.getKey()).absorb(entry.getValue()));
	}
		
	public static List<WatchmakerProductionReport> generateReport() {
		return generateReport(() -> CustomerWatch.findFinishedByWatchmaker());
	}
	
	private static List<WatchmakerProductionReport> generateReport(Supplier<List<CustomerWatch>> supplier) {
		HashMap<String, WatchmakerProductionReport> reportBuilder = new HashMap<>();
		List<CustomerWatch> lines = supplier.get();
		lines.stream().forEach(line -> addNewLine(new WatchmakerProductionReport(line), reportBuilder));
		return reportBuilder.values().stream().sorted(Comparator.comparing(WatchmakerProductionReport::evaluateKey).reversed()).collect(Collectors.toList());
	}

	private static void addNewLine(WatchmakerProductionReport newLine, HashMap<String, WatchmakerProductionReport> reportBuilder) {
		String key = newLine.periodAsString;
		if (reportBuilder.containsKey(key)) {
			reportBuilder.get(key).mergeStats(newLine);
		} else {
			reportBuilder.put(key, newLine);
		}
	}
	
	private static String findStatsMergeKey(CustomerWatch newWatch) {
		return newWatch.managedBy.firstname;
	}
	
	private static String evaluateKey(WatchmakerProductionReport newLine) {
		return newLine.periodAsString;
	}
	
	public int getFirstCount() {
		return stats.values().stream().findFirst().map(ProductionStats::getCount).orElse(0);
	}
	
	public long getSecondCount() {
		return stats.values().stream().skip(1).findFirst().map(ProductionStats::getCount).orElse(0);
	}
	
	public long getFirstTurnover() {
		return stats.values().stream().findFirst().map(ProductionStats::getTurnover).orElse(0l);
	}
	
	public long getSecondTurnover() {
		return stats.values().stream().skip(1).findFirst().map(ProductionStats::getTurnover).orElse(0l);
	}
}
