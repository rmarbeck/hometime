package reporting;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.hometime.utils.UniqueAccountingNumber;
import fr.hometime.utils.UsefullHelper;
import fr.hometime.utils.VATHelper;
import models.AccountingLineAnalytic;

public class AnalyticsReportEnhanced2 {
	public final static String PERIOD_CURRENT_MONTH = "currentMonth";
	public final static String PERIOD_LAST_MONTH = "lastMonth";
	public final static String PERIOD_LAST_YEAR_SAME_MONTH = "lastYearSameMonth";
	public final static String PERIOD_CURRENT_ACCOUNTING = "currentAccounting";
	public final static String PERIOD_LAST_ACCOUNTING = "lastAccounting";
	public final static String PERIOD_LAST_YEAR_SAME_MONTH_ACCOUNTING = "lastYearSameMonthAccounting";
	
	public final static String FILTER_ALL = "all";
	public final static String FILTER_LOCAL_SERVICING = "localServicing";
	public final static String FILTER_SELLING_ONLY = "sellingOnly";
	public final static String FILTER_SIMPLE_QUARTZ_ONLY = "simpleQuartzOnly";
	public final static String FILTER_SELLING_NEW_ONLY = "sellingNewOnly";
	public final static String FILTER_SELLING_USED_ONLY = "sellingUsedOnly";
	
	public final static String FIGURE_MARGIN = "margin";
	public final static String FIGURE_TURNOVER = "turnover";
	
	private final static Predicate<Long> alwaysTrue = (value) -> true;
	private final static Predicate<Long> isWatchSelling = (value) -> (value > 1000L && value < 2000L);
	private final static Predicate<Long> isUsedWatchSelling = (value) -> (value > 1000L && value < 1100L);
	private final static Predicate<Long> alwaysButUsedWatchSelling = isUsedWatchSelling.negate();
	private final static Predicate<Long> isNewWatchSelling = (value) -> (value > 1100L && value < 1200L);
	private final static Predicate<Long> isAccessorySelling = (value) -> (value > 3000L && value < 4000L);
	private final static Predicate<Long> isLocalServicing = (value) -> (value > 2000L && value < 2100L);
	private final static Predicate<Long> isExternalServicing = (value) -> (value > 2100L && value < 3000L);
	private final static Predicate<Long> isValueAddedServiceProviding = (value) -> (value > 6000L && value < 7000L);
	private final static Predicate<Long> isQuartzSimpleService = (value) -> (value >= 2054L && value <= 2057L);
	
	/**
	 * 
	 * Tuple for storing a value by name
	 *
	 */
	class Figure {
		private String name;
		private float value = 0f;
		
		public Figure(String name, float value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public float getValue() {
			return value;
		}
	}
	
	/**
	 * 
	 * Collector
	 *
	 */
	class OvertimeFiguresCollector implements Collector<AccountingLineAnalytic, OvertimeFigures, OvertimeFigures> {

		@Override
		public Supplier<OvertimeFigures> supplier() {
			return OvertimeFigures::new;
		}

		@Override
		public BiConsumer<OvertimeFigures, AccountingLineAnalytic> accumulator() {
			return (figures, data) ->  {
				Long analyticCode = data.analyticCode.analyticCode;
				String invoiceUAN = data.uan;
				Float margin = data.price-data.cost;
				
				figures.add(UniqueAccountingNumber.fromStringIfValidOnly(invoiceUAN, false), analyticCode, margin, data.price);
			};
		}

		@Override
		public BinaryOperator<OvertimeFigures> combiner() {
			return (figures1, figures2) -> figures1.mergeWith(figures2);
		}

		@Override
		public Function<OvertimeFigures, OvertimeFigures> finisher() {
			return Function.identity();
		}

		@Override
		public Set<Characteristics> characteristics() {
			return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED));
		}
		
	}
	
	/**
	 * 
	 * Defining a "Type of analytic value", meaning for example margin, turnover, vat...
	 * 
	 * Use a filter (by analytic type) and a computation rule using margin and turnover values
	 *
	 */
	class TypeOfFiguresDescriptor {
		private String name;
		private Map<Predicate<Long>, BinaryOperator<Float>> appenders;
		
		public TypeOfFiguresDescriptor(String name) {
			this.name = name;
			this.appenders = new HashMap<Predicate<Long>, BinaryOperator<Float>>();
		}
		
		public TypeOfFiguresDescriptor add(Predicate<Long> newFilter, BinaryOperator<Float> newAppender) {
			this.appenders.put(newFilter, newAppender);
			return this;
		}
		
		public float computeValue(Long type, float margin, float turnover) {
			return appenders.keySet().stream().map((key) -> key.test(type)?appenders.get(key).apply(margin, turnover):0f).reduce(0f, Float::sum);
		}
	}
	
	/**
	 * 
	 * Defining an analytic value for given types of analytic codes (via filters), a set of time periods and types of analytic values
	 * 
	 * Allows to generate a set of values
	 *
	 */
	class OvertimeFiguresDescriptor {
		private String name;
		private Predicate<Long> filter;
		private Map<String, Predicate<UniqueAccountingNumber>> timePeriods;
		private List<TypeOfFiguresDescriptor> typeDescriptors;
		
		public OvertimeFiguresDescriptor(String name, Predicate<Long> filter) {
			this.name = name;
			this.timePeriods = new HashMap<String, Predicate<UniqueAccountingNumber>>();
			this.filter = filter;
			this.typeDescriptors = new ArrayList<TypeOfFiguresDescriptor>();
		}
		
		public OvertimeFiguresDescriptor addPeriod(String name, Predicate<UniqueAccountingNumber> timePeriod) {
			this.timePeriods.put(name, timePeriod);
			return this;
		}
		
		public OvertimeFiguresDescriptor addPeriods(Map<String, Predicate<UniqueAccountingNumber>> timePeriods) {
			this.timePeriods.putAll(timePeriods);
			return this;
		}
		
		public OvertimeFiguresDescriptor addType(TypeOfFiguresDescriptor type) {
			this.typeDescriptors.add(type);
			return this;
		}
		
		private String getName(String timePeriodName, TypeOfFiguresDescriptor typeDescriptor) {
			return AnalyticsReportEnhanced2.getKey(timePeriodName, this.name, typeDescriptor.name);
		}
		
		public List<String> getNames() {
			return timePeriods.keySet().stream().flatMap(timePeriod -> typeDescriptors.stream().map(typeDescriptor -> timePeriod+name.toUpperCase()+typeDescriptor)).collect(Collectors.toList());
		}
		
		public Map<String, Float> getValues(UniqueAccountingNumber uan, Long type, Float marginValue, Float turnoverValue) {
			return timePeriods.entrySet().stream().filter(x -> filter.test(type)).flatMap(
															timePeriod -> typeDescriptors.stream().map(
																								typeDescriptor -> new Figure(
																																getName(timePeriod.getKey(), typeDescriptor),
																																timePeriod.getValue().test(uan)?typeDescriptor.computeValue(type, marginValue, turnoverValue):0f
																																)
																								)
															).collect(Collectors.toMap(Figure::getName, Figure::getValue));
		}
		
	}
	
	/**
	 * 
	 * Holds the values defined in different containers (List of OvertimeFiguresDescriptor)
	 *
	 */
	public class OvertimeFigures {
		private List<OvertimeFiguresDescriptor> containers;
		private Map<String, Float> figures;
		
		private final YearMonth now = YearMonth.now();
		
		private final Predicate<UniqueAccountingNumber> isCurrentMonth = (value) -> (value.getYearAndMonthFromUAN().equals(now));
		private final Predicate<UniqueAccountingNumber> isLastMonth = (value) -> (value.getYearAndMonthFromUAN().equals(now.minusMonths(1)));
		private final Predicate<UniqueAccountingNumber> isLastYearSameMonth = (value) -> (value.getYearAndMonthFromUAN().equals(now.minusMonths(12)));
		
		private final Predicate<UniqueAccountingNumber> isCurrentAccounting = UniqueAccountingNumber::isInCurrentFinancialYear;
		private final Predicate<UniqueAccountingNumber> isLastAccounting = UniqueAccountingNumber::isInPreviousFinancialYear;
		private final Predicate<UniqueAccountingNumber> isLastYearSameMonthAccounting = (value) -> (value.isInPreviousFinancialYear() && value.getYearAndMonthFromUAN().isBefore(now.minusMonths(11)));
		
		private void createContainers() {
			TypeOfFiguresDescriptor margin = new TypeOfFiguresDescriptor(FIGURE_MARGIN)
											.add(alwaysButUsedWatchSelling, (marginValue, turnoverValue) -> marginValue)
											.add(isUsedWatchSelling, (marginValue, turnoverValue) -> VATHelper.getPriceBeforeVAT(marginValue));
			TypeOfFiguresDescriptor turnover = new TypeOfFiguresDescriptor(FIGURE_TURNOVER)
											.add(alwaysButUsedWatchSelling, (marginValue, turnoverValue) -> turnoverValue)
											.add(isUsedWatchSelling, (marginValue, turnoverValue) -> VATHelper.getTurnoverFromMarginModelSelling(turnoverValue, marginValue));
			
			containers = new ArrayList<AnalyticsReportEnhanced2.OvertimeFiguresDescriptor>();
			
			containers.add(new OvertimeFiguresDescriptor(FILTER_ALL, alwaysTrue)
							.addPeriods(getAllPeriods())
							.addType(margin).addType(turnover));
			
			containers.add(new OvertimeFiguresDescriptor(FILTER_LOCAL_SERVICING, isLocalServicing)
							.addPeriods(getAllPeriods())
							.addType(margin).addType(turnover));
			
			containers.add(new OvertimeFiguresDescriptor(FILTER_SELLING_ONLY, isWatchSelling)
							.addPeriods(getAllPeriods())
							.addType(margin).addType(turnover));
			
			containers.add(new OvertimeFiguresDescriptor(FILTER_SIMPLE_QUARTZ_ONLY, isQuartzSimpleService)
							.addPeriods(getAllPeriods())
							.addType(margin).addType(turnover));
			
			containers.add(new OvertimeFiguresDescriptor(FILTER_SELLING_NEW_ONLY, isNewWatchSelling)
					.addPeriods(getAllPeriods())
					.addType(margin).addType(turnover));
			
			containers.add(new OvertimeFiguresDescriptor(FILTER_SELLING_USED_ONLY, isUsedWatchSelling)
					.addPeriods(getAllPeriods())
					.addType(margin).addType(turnover));

		}
		
		private Map<String, Predicate<UniqueAccountingNumber>> getAllPeriods() {
			Map<String, Predicate<UniqueAccountingNumber>> allPeriods = new HashMap<String, Predicate<UniqueAccountingNumber>>();
			allPeriods.put(PERIOD_CURRENT_MONTH, isCurrentMonth);
			allPeriods.put(PERIOD_LAST_MONTH, isLastMonth);
			allPeriods.put(PERIOD_LAST_YEAR_SAME_MONTH, isLastYearSameMonth);
			allPeriods.put(PERIOD_CURRENT_ACCOUNTING, isCurrentAccounting);
			allPeriods.put(PERIOD_LAST_ACCOUNTING, isLastAccounting);
			allPeriods.put(PERIOD_LAST_YEAR_SAME_MONTH_ACCOUNTING, isLastYearSameMonthAccounting);
			return allPeriods;
		}
		
		
		public OvertimeFigures() {
			createContainers();
			figures = new HashMap<String, Float>();
		}
		
		public OvertimeFigures mergeWith(OvertimeFigures toMergeWith) {
			this.mergeWith(toMergeWith.figures);
			
			return this;
		}
		
		private OvertimeFigures mergeWith(Map<String, Float> toMergeWith) {
			toMergeWith.forEach((key, value) -> this.figures.merge(key, value, (v1, v2) -> v1 + v2));
			
			return this;
		}
		
		public void add(Optional<UniqueAccountingNumber> ouan, Long type, float marginValue, float turnoverValue) {
			if (ouan.isPresent()) {
				UniqueAccountingNumber uan = ouan.get();
				Stream<Map<String, Float>> valuesFromDifferentContainers = containers.stream().map(container -> container.getValues(uan, type, marginValue, turnoverValue));
				
				valuesFromDifferentContainers.forEach(partialMap -> this.mergeWith(partialMap));
			}
		}
		
		public Float getValue(String period, String filter, String figure) {
			return this.figures.get(getKey(period, filter, figure));
		}
		

	}
	
	public Date date;
	public OvertimeFigures figures;
	
	private AnalyticsReportEnhanced2() {
	}

	public static AnalyticsReportEnhanced2 generateReportEnhanced() {
		AnalyticsReportEnhanced2 report = new AnalyticsReportEnhanced2();
		List<AccountingLineAnalytic> lines = AccountingLineAnalytic.findAllForReportingEnhanced();
		report.figures = lines.parallelStream().collect(report.new OvertimeFiguresCollector());
		return report;
	}
	
	public Map<String, Float> getFigures() {
		return figures.figures;
	}
	
	public static String getKey(String period, String filter, String figure) {
		return period+UsefullHelper.capitalize(filter)+UsefullHelper.capitalize(figure);
	}
}
