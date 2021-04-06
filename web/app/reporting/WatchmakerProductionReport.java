package reporting;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fr.hometime.utils.DateHelper;
import models.CustomerWatch;
import models.CustomerWatch.CustomerWatchStatus;

public class WatchmakerProductionReport implements MesurableReport {
	public String periodAsString;
	public Map<String, LongSummaryStatistics> stats;
	
	private WatchmakerProductionReport(String periodAsString, Map<String, LongSummaryStatistics> stats) {
		this.periodAsString = periodAsString;
		this.stats = stats;
	}
	
	private static String calculatePeriod(CustomerWatch currentWatch) {
		if (currentWatch.status.equals(CustomerWatchStatus.BACK_TO_CUSTOMER))
			return DateHelper.asMonthYear(currentWatch.lastStatusUpdate);
		return "A venir";
	}
	
	private static String findWatchMaker(CustomerWatch newWatch) {
		return newWatch.managedBy.firstname;
	}

	public static List<WatchmakerProductionReport> generateReport() {
		return generateReport(() -> CustomerWatch.findFinishedByWatchmaker());
	}
	
	private static List<WatchmakerProductionReport> generateReport(Supplier<List<CustomerWatch>> supplier) {
		return statsGroupedByPeriodAndWatchMaker(supplier.get()).entrySet().parallelStream()
																.sorted(Entry.<String, Map<String, LongSummaryStatistics>>comparingByKey().reversed())
																.map((entry) -> new WatchmakerProductionReport(entry.getKey(), entry.getValue()))
																.collect(Collectors.toList());
	}
	
	private static Map<String, Map<String, LongSummaryStatistics>> statsGroupedByPeriodAndWatchMaker(List<CustomerWatch> watches) {
		return watches.parallelStream().collect(Collectors.groupingByConcurrent(WatchmakerProductionReport::calculatePeriod,
				Collectors.groupingBy(WatchmakerProductionReport::findWatchMaker, Collectors.summarizingLong((watch) -> watch.finalCustomerServicePrice))));
	}
	
	public int getLouCount() {
		return getCountByFirstname("Lou");
	}
	
	public int getAndyCount() {
		return getCountByFirstname("Andy");
	}
	
	public long getLouTurnover() {
		return getTurnoverByFirstname("Lou");
	}
	
	public long getAndyTurnover() {
		return getTurnoverByFirstname("Andy");
	}
	
	private int getCountByFirstname(String firstname) {
		return getValueByFirstname(firstname, LongSummaryStatistics::getCount, 0l).intValue();
	}
	
	private long getTurnoverByFirstname(String firstname) {
		return getValueByFirstname(firstname, LongSummaryStatistics::getSum, 0l);
	}
	
	private <T> T getValueByFirstname(String name, Function<LongSummaryStatistics, T> getter, T defaultValue) {
		return stats.entrySet().stream().filter((entry) -> entry.getKey().equals(name)).findFirst().map(Entry::getValue).map(getter).orElse(defaultValue);
	}
}
