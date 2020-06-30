package reporting;

import java.time.YearMonth;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fr.hometime.utils.UniqueAccountingNumber;
import fr.hometime.utils.VATHelper;
import models.AccountingDocument;
import models.AccountingLineAnalytic;
import play.Logger;

public class AnalyticsChartsReport {
	private final static Predicate<Long> alwaysTrue = (value) -> true;
	private final static Predicate<Long> isWatchSelling = (value) -> (value > 1000L && value < 2000L);
	private final static Predicate<Long> allButWatchSelling = isWatchSelling.negate();
	private final static Predicate<Long> isUsedWatchSelling = (value) -> (value > 1000L && value < 1100L);
	private final static Predicate<Long> alwaysButUsedWatchSelling = isUsedWatchSelling.negate();
	private final static Predicate<Long> isNewWatchSelling = (value) -> (value > 1100L && value < 1200L);
	private final static Predicate<Long> isAccessorySelling = (value) -> (value > 3000L && value < 4000L);
	private final static Predicate<Long> isLocalServicing = (value) -> (value > 2000L && value < 2100L);
	private final static Predicate<Long> isExternalServicing = (value) -> (value > 2100L && value < 3000L);
	private final static Predicate<Long> isValueAddedServiceProviding = (value) -> (value > 6000L && value < 7000L);
	private final static Predicate<Long> isQuartzSimpleService = (value) -> (value >= 2054L && value <= 2057L);
	
	public class OvertimeFigures {
		private final Predicate<UniqueAccountingNumber> isCurrentAccounting = UniqueAccountingNumber::isInCurrentFinancialYear;
		private final Predicate<UniqueAccountingNumber> isLastAccounting = UniqueAccountingNumber::isInPreviousFinancialYear;
		
		public Map<YearMonth, Float> currentAccountMargin = new HashMap<YearMonth, Float>();
		public Map<YearMonth, Float> lastAccountMargin = new HashMap<YearMonth, Float>();
		
		public Map<YearMonth, Float> currentAccountMarginLocalServicingOnly = new HashMap<YearMonth, Float>();
		public Map<YearMonth, Float> lastAccountMarginLocalServicingOnly = new HashMap<YearMonth, Float>();
		
		public Map<YearMonth, Float> currentAccountMarginSellingOnly = new HashMap<YearMonth, Float>();
		public Map<YearMonth, Float> lastAccountMarginSellingOnly = new HashMap<YearMonth, Float>();
		
		public Map<YearMonth, Float> currentAccountMarginSimpleQuartzOnly = new HashMap<YearMonth, Float>();
		public Map<YearMonth, Float> lastAccountMarginSimpleQuartzOnly = new HashMap<YearMonth, Float>();
		
		public Map<YearMonth, Float> currentAccountMarginAllButSelling = new HashMap<YearMonth, Float>();
		public Map<YearMonth, Float> lastAccountMarginAllButSelling = new HashMap<YearMonth, Float>();
		

		
		public void add(Optional<UniqueAccountingNumber> ouan, Long type, float marginValue, float turnoverValue) {
			if (ouan.isPresent()) {
				UniqueAccountingNumber uan = ouan.get();
				
				addToMap(currentAccountMargin, uan, isCurrentAccounting, type, alwaysButUsedWatchSelling, marginValue);
				addToMap(currentAccountMargin, uan, isCurrentAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				
				addToMap(lastAccountMargin, uan, isLastAccounting, type, alwaysButUsedWatchSelling, marginValue);
				addToMap(lastAccountMargin, uan, isLastAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				
				
				addToMap(currentAccountMarginLocalServicingOnly, uan, isCurrentAccounting, type, isLocalServicing, marginValue);
				
				addToMap(lastAccountMarginLocalServicingOnly, uan, isLastAccounting, type, isLocalServicing, marginValue);
				
				
				addToMap(currentAccountMarginSellingOnly, uan, isCurrentAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				addToMap(currentAccountMarginSellingOnly, uan, isCurrentAccounting, type, isNewWatchSelling, marginValue);
				
				addToMap(lastAccountMarginSellingOnly, uan, isLastAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				addToMap(lastAccountMarginSellingOnly, uan, isLastAccounting, type, isNewWatchSelling, marginValue);
				
				
				addToMap(currentAccountMarginSimpleQuartzOnly, uan, isCurrentAccounting, type, isQuartzSimpleService, marginValue);
				addToMap(lastAccountMarginSimpleQuartzOnly, uan, isLastAccounting, type, isQuartzSimpleService, marginValue);
				
				addToMap(currentAccountMarginAllButSelling, uan, isCurrentAccounting, type, allButWatchSelling, marginValue);
				addToMap(lastAccountMarginAllButSelling, uan, isLastAccounting, type, allButWatchSelling, marginValue);
			}
			
		}
		
		private Map<YearMonth, Float> addToMap(Map<YearMonth, Float> currentMap, UniqueAccountingNumber uan, Predicate<UniqueAccountingNumber> shouldBeAddedByDate, Long type, Predicate<Long> shouldBeAddedByType, float value) {
			if (shouldBeAddedByDate.test(uan) && shouldBeAddedByType.test(type)) {
				addValue(currentMap, uan, value);
			} else {
				addValue(currentMap, uan, 0f);
			}
			return currentMap;
		}
		
		private void addValue(Map<YearMonth, Float> map, UniqueAccountingNumber uan, float value) {
			if (map.containsKey(uan.getYearAndMonthFromUAN())) {
				map.put(uan.getYearAndMonthFromUAN(), map.get(uan.getYearAndMonthFromUAN())+value);
			} else {
				map.put(uan.getYearAndMonthFromUAN(), value);
			}
		}
	}
	
	
	public Date date;
	public OvertimeFigures figures;
	
	public Map<YearMonth, Float> getSortedCurrentAccountMargin() {
		return getSortedMap(figures.currentAccountMargin);
	}
	
	public Map<YearMonth, Float> getSortedLastAccountMargin() {
		return getSortedMap(figures.lastAccountMargin);

	}
	
	public Map<YearMonth, Float> getSortedCurrentAccountMarginLocalServicingOnly() {
		return getSortedMap(figures.currentAccountMarginLocalServicingOnly);
	}
	
	public Map<YearMonth, Float> getSortedLastAccountMarginLocalServicingOnly() {
		return getSortedMap(figures.lastAccountMarginLocalServicingOnly);

	}
	
	public Map<YearMonth, Float> getSortedCurrentAccountMarginSellingOnly() {
		return getSortedMap(figures.currentAccountMarginSellingOnly);
	}
	
	public Map<YearMonth, Float> getSortedLastAccountMarginSellingOnly() {
		return getSortedMap(figures.lastAccountMarginSellingOnly);

	}
	
	public Map<YearMonth, Float> getSortedCurrentAccountMarginSimpleQuartzOnly() {
		return getSortedMap(figures.currentAccountMarginSimpleQuartzOnly);
	}
	
	public Map<YearMonth, Float> getSortedLastAccountMarginSimpleQuartzOnly() {
		return getSortedMap(figures.lastAccountMarginSimpleQuartzOnly);
	}
	
	public Map<YearMonth, Float> getSortedCurrentAccountMarginAllButSelling() {
		return getSortedMap(figures.currentAccountMarginAllButSelling);
	}
	
	public Map<YearMonth, Float> getSortedLastAccountMarginAllButSelling() {
		return getSortedMap(figures.lastAccountMarginAllButSelling);
	}
	
	
	private Map<YearMonth, Float> getSortedMap(Map<YearMonth, Float> unsorted) {
		return unsorted.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}
	
	private AnalyticsChartsReport() {
		figures = new OvertimeFigures();
	}
	
	private void loadAnalyticLine(AccountingLineAnalytic currentLine) {
		AccountingDocument currentDocument = currentLine.accountingLine.document;
		Long analyticCode = currentLine.analyticCode.analyticCode;
		String invoiceUAN = InvoiceLineReport.guessUAN(currentDocument).orElse("");
		
		this.figures.add(UniqueAccountingNumber.fromStringIfValidOnly(invoiceUAN, false), analyticCode, currentLine.price-currentLine.cost, currentLine.price);
	}
	
	private void loadAnalyticLineUnhanced(AccountingLineAnalytic currentLine) {
		Long analyticCode = currentLine.analyticCode.analyticCode;
		String invoiceUAN = currentLine.uan	;
		
		this.figures.add(UniqueAccountingNumber.fromStringIfValidOnly(invoiceUAN, false), analyticCode, currentLine.price-currentLine.cost, currentLine.price);
	}

	public static AnalyticsChartsReport generateReportUnhanced() {
		AnalyticsChartsReport report = new AnalyticsChartsReport();
		List<AccountingLineAnalytic> lines = AccountingLineAnalytic.findAllForReportingEnhanced();
		for(AccountingLineAnalytic line : lines)
			report.loadAnalyticLineUnhanced(line);
		return report;
	}
		
	public static AnalyticsChartsReport generateReport() {
		AnalyticsChartsReport report = new AnalyticsChartsReport();
		List<AccountingLineAnalytic> lines = AccountingLineAnalytic.findAllForReporting();
		for(AccountingLineAnalytic line : lines)
			report.loadAnalyticLine(line);
		return report;
	}
	
	
}
