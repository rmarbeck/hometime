package reporting;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ReportWithStats {
	private Optional<List<? extends MesurableReport>> report = Optional.empty();
	private Instant startTime;
	private long startFreeMemory;
	private float calculationTime = 0f;
	private long memoryDelta = 0l;
	
	private ReportWithStats() {
		startTime = Instant.now();
		startFreeMemory = Runtime.getRuntime().freeMemory();
	}
	
	public static ReportWithStats start() {
		return new ReportWithStats();
	}
	
	public ReportWithStats stop(List<? extends MesurableReport> report) {
		this.report = Optional.ofNullable(report);
		calculationTime = Duration.between(startTime, Instant.now()).toMillis();
		memoryDelta = Runtime.getRuntime().freeMemory() - startFreeMemory;
		return this;
	}

	public float getCalculationTime() {
		return calculationTime;
	}

	public long getMemoryDelta() {
		return memoryDelta;
	}
	
	public boolean isReportReady() {
		return report.isPresent();
	}

	public List<? extends MesurableReport> getReport() {
		return report.get();
	}
	
	public static ReportWithStats mesure(Supplier<List<? extends MesurableReport>> reportBuilder) {
		return doIt(reportBuilder);
	}
	
	private static ReportWithStats doIt(Supplier<List<? extends MesurableReport>> reportBuilder) {
		ReportWithStats reportWithStat = ReportWithStats.start();
		List<? extends MesurableReport> result = reportBuilder.get();
		return reportWithStat.stop(result);
	}
}
