package reporting;

import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import fr.hometime.utils.UniqueAccountingNumber;
import fr.hometime.utils.VATHelper;
import models.AccountingDocument;
import models.AccountingLineAnalytic;
import play.Logger;

public class AnalyticsReport {
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
	
	public class OvertimeFigures {
		private final Predicate<UniqueAccountingNumber> isCurrentMonth = (value) -> (value.getYearAndMonthFromUAN().equals(YearMonth.now()));
		private final Predicate<UniqueAccountingNumber> isLastMonth = (value) -> (value.getYearAndMonthFromUAN().equals(YearMonth.now().minusMonths(1)));
		private final Predicate<UniqueAccountingNumber> isLastYearSameMonth = (value) -> (value.getYearAndMonthFromUAN().equals(YearMonth.now().minusMonths(12)));
		
		private final Predicate<UniqueAccountingNumber> isCurrentAccounting = UniqueAccountingNumber::isInCurrentFinancialYear;
		private final Predicate<UniqueAccountingNumber> isLastAccounting = UniqueAccountingNumber::isInPreviousFinancialYear;
		private final Predicate<UniqueAccountingNumber> isLastYearSameMonthAccounting = (value) -> (value.isInPreviousFinancialYear() && value.getYearAndMonthFromUAN().isBefore(YearMonth.now().minusMonths(11)));
		
		public Float currentMonthMargin = 0f;
		public Float lastMonthMargin = 0f;
		public Float lastYearSameMonthMargin = 0f;
		
		public Float currentAccountingMargin = 0f;
		public Float lastAccountingMargin = 0f;
		public Float lastYearSameMonthAccountingMargin = 0f;
		
		
		public Float currentMonthTurnOver = 0f;
		public Float lastMonthTurnOver = 0f;
		public Float lastYearSameMonthTurnOver = 0f;
		
		public Float currentAccountingTurnOver = 0f;
		public Float lastAccountingTurnOver = 0f;
		public Float lastYearSameMonthAccountingTurnOver = 0f;
		
		
		public Float currentMonthMarginLocalServicingOnly = 0f;
		public Float lastMonthMarginLocalServicingOnly = 0f;
		public Float lastYearSameMonthMarginLocalServicingOnly = 0f;
		
		public Float currentAccountingMarginLocalServicingOnly = 0f;
		public Float lastAccountingMarginLocalServicingOnly = 0f;
		public Float lastYearSameMonthAccountingMarginLocalServicingOnly = 0f;
		
		
		public Float currentMonthTurnOverLocalServicingOnly = 0f;
		public Float lastMonthTurnOverLocalServicingOnly = 0f;
		
		public Float currentAccountingTurnOverLocalServicingOnly = 0f;
		public Float lastAccountingTurnOverLocalServicingOnly = 0f;
		
		
		public Float currentMonthMarginSellingOnly = 0f;
		public Float lastMonthMarginSellingOnly = 0f;
		public Float lastYearSameMonthMarginSellingOnly = 0f;
		
		public Float currentAccountingMarginSellingOnly = 0f;
		public Float lastAccountingMarginSellingOnly = 0f;
		public Float lastYearSameMonthAccountingMarginSellingOnly = 0f;
		
		
		public Float currentMonthTurnOverSellingOnly = 0f;
		public Float lastMonthTurnOverSellingOnly = 0f;
		
		public Float currentAccountingTurnOverSellingOnly = 0f;
		public Float lastAccountingTurnOverSellingOnly = 0f;
		
		public Float currentMonthMarginSimpleQuartzOnly = 0f;
		public Float lastMonthMarginSimpleQuartzOnly = 0f;
		
		public Float currentAccountingMarginSimpleQuartzOnly = 0f;
		public Float lastAccountingMarginSimpleQuartzOnly = 0f;
		
		public Float currentMonthTurnOverSimpleQuartzOnly = 0f;
		public Float lastMonthTurnOverSimpleQuartzOnly = 0f;
		
		public Float currentAccountingTurnOverSimpleQuartzOnly = 0f;
		public Float lastAccountingTurnOverSimpleQuartzOnly = 0f;
		
		public void add(Optional<UniqueAccountingNumber> ouan, Long type, float marginValue, float turnoverValue) {
			if (ouan.isPresent()) {
				UniqueAccountingNumber uan = ouan.get();
				currentMonthMargin += add(uan, isCurrentMonth, type, alwaysButUsedWatchSelling, marginValue);
				currentMonthMargin += add(uan, isCurrentMonth, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				lastMonthMargin += add(uan, isLastMonth, type, alwaysButUsedWatchSelling, marginValue);
				lastMonthMargin += add(uan, isLastMonth, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				lastYearSameMonthMargin += add(uan, isLastYearSameMonth, type, alwaysButUsedWatchSelling, marginValue);
				lastYearSameMonthMargin += add(uan, isLastYearSameMonth, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				currentAccountingMargin += add(uan, isCurrentAccounting, type, alwaysButUsedWatchSelling, marginValue);
				currentAccountingMargin += add(uan, isCurrentAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				lastAccountingMargin += add(uan, isLastAccounting, type, alwaysButUsedWatchSelling, marginValue);
				lastAccountingMargin += add(uan, isLastAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				lastYearSameMonthAccountingMargin += add(uan, isLastYearSameMonthAccounting, type, alwaysButUsedWatchSelling, marginValue);
				lastYearSameMonthAccountingMargin += add(uan, isLastYearSameMonthAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				
				currentMonthTurnOver += addWithTurnoverCoputation(uan, isCurrentMonth, type, alwaysTrue, marginValue, turnoverValue);
				lastMonthTurnOver += addWithTurnoverCoputation(uan, isLastMonth, type, alwaysTrue, marginValue, turnoverValue);
				lastYearSameMonthTurnOver += addWithTurnoverCoputation(uan, isLastYearSameMonth, type, alwaysTrue, marginValue, turnoverValue);
				currentAccountingTurnOver += addWithTurnoverCoputation(uan, isCurrentAccounting, type, alwaysTrue, marginValue, turnoverValue); 
				lastAccountingTurnOver += addWithTurnoverCoputation(uan, isLastAccounting, type, alwaysTrue, marginValue, turnoverValue);
				lastYearSameMonthAccountingTurnOver += addWithTurnoverCoputation(uan, isLastYearSameMonthAccounting, type, alwaysTrue, marginValue, turnoverValue);
								
				currentMonthMarginLocalServicingOnly += add(uan, isCurrentMonth, type, isLocalServicing, marginValue);
				lastMonthMarginLocalServicingOnly += add(uan, isLastMonth, type, isLocalServicing, marginValue);
				lastYearSameMonthMarginLocalServicingOnly += add(uan, isLastYearSameMonth, type, isLocalServicing, marginValue);
				currentAccountingMarginLocalServicingOnly += add(uan, isCurrentAccounting, type, isLocalServicing, marginValue);
				lastAccountingMarginLocalServicingOnly += add(uan, isLastAccounting, type, isLocalServicing, marginValue);
				lastYearSameMonthAccountingMarginLocalServicingOnly += add(uan, isLastYearSameMonthAccounting, type, isLocalServicing, marginValue);
				
				currentMonthTurnOverLocalServicingOnly += addWithTurnoverCoputation(uan, isCurrentMonth, type, isLocalServicing, marginValue, turnoverValue);
				lastMonthTurnOverLocalServicingOnly += addWithTurnoverCoputation(uan, isLastMonth, type, isLocalServicing, marginValue, turnoverValue);
				currentAccountingTurnOverLocalServicingOnly += addWithTurnoverCoputation(uan, isCurrentAccounting, type, isLocalServicing, marginValue, turnoverValue);
				lastAccountingTurnOverLocalServicingOnly += addWithTurnoverCoputation(uan, isLastAccounting, type, isLocalServicing, marginValue, turnoverValue);
				
				currentMonthMarginSellingOnly += add(uan, isCurrentMonth, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				currentMonthMarginSellingOnly += add(uan, isCurrentMonth, type, isNewWatchSelling, marginValue);
				lastMonthMarginSellingOnly += add(uan, isLastMonth, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				lastMonthMarginSellingOnly += add(uan, isLastMonth, type, isNewWatchSelling, marginValue);
				lastYearSameMonthMarginSellingOnly += add(uan, isLastYearSameMonth, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				lastYearSameMonthMarginSellingOnly += add(uan, isLastYearSameMonth, type, isNewWatchSelling, marginValue);
				currentAccountingMarginSellingOnly += add(uan, isCurrentAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				currentAccountingMarginSellingOnly += add(uan, isCurrentAccounting, type, isNewWatchSelling, marginValue);
				lastAccountingMarginSellingOnly += add(uan, isLastAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				lastAccountingMarginSellingOnly += add(uan, isLastAccounting, type, isNewWatchSelling, marginValue);
				lastYearSameMonthAccountingMarginSellingOnly += add(uan, isLastYearSameMonthAccounting, type, isUsedWatchSelling, VATHelper.getPriceBeforeVAT(marginValue));
				lastYearSameMonthAccountingMarginSellingOnly += add(uan, isLastYearSameMonthAccounting, type, isNewWatchSelling, marginValue);
				
				currentMonthTurnOverSellingOnly += addWithTurnoverCoputation(uan, isCurrentMonth, type, isWatchSelling, marginValue, turnoverValue);
				lastMonthTurnOverSellingOnly += addWithTurnoverCoputation(uan, isLastMonth, type, isWatchSelling, marginValue, turnoverValue);
				currentAccountingTurnOverSellingOnly += addWithTurnoverCoputation(uan, isCurrentAccounting, type, isWatchSelling, marginValue, turnoverValue);
				lastAccountingTurnOverSellingOnly += addWithTurnoverCoputation(uan, isLastAccounting, type, isWatchSelling, marginValue, turnoverValue);
				
				currentMonthMarginSimpleQuartzOnly += add(uan, isCurrentMonth, type, isQuartzSimpleService, marginValue);
				lastMonthMarginSimpleQuartzOnly += add(uan, isLastMonth, type, isQuartzSimpleService, marginValue);
				currentAccountingMarginSimpleQuartzOnly += add(uan, isCurrentAccounting, type, isQuartzSimpleService, marginValue);
				lastAccountingMarginSimpleQuartzOnly += add(uan, isLastAccounting, type, isQuartzSimpleService, marginValue);
				
				currentMonthTurnOverSimpleQuartzOnly += addWithTurnoverCoputation(uan, isCurrentMonth, type, isQuartzSimpleService, marginValue, turnoverValue);
				lastMonthTurnOverSimpleQuartzOnly += addWithTurnoverCoputation(uan, isLastMonth, type, isQuartzSimpleService, marginValue, turnoverValue);
				currentAccountingTurnOverSimpleQuartzOnly += addWithTurnoverCoputation(uan, isCurrentAccounting, type, isQuartzSimpleService, marginValue, turnoverValue);
				lastAccountingTurnOverSimpleQuartzOnly += addWithTurnoverCoputation(uan, isLastAccounting, type, isQuartzSimpleService, marginValue, turnoverValue);
			}
			
		}
		
		private float add(UniqueAccountingNumber uan, Predicate<UniqueAccountingNumber> shouldBeAddedByDate, Long type, Predicate<Long> shouldBeAddedByType, float value) {
			return (shouldBeAddedByDate.test(uan) && shouldBeAddedByType.test(type))?value:0f;
		}
		
		private float addWithTurnoverCoputation(UniqueAccountingNumber uan, Predicate<UniqueAccountingNumber> shouldBeAddedByDate, Long type, Predicate<Long> shouldBeAddedByType, float marginValue, float turnoverValue) {
			return add(uan, shouldBeAddedByDate, type, shouldBeAddedByType, computeTurnoverValue(type, marginValue, turnoverValue));
		}
		
		private float computeTurnoverValue(Long type, float marginValue, float turnoverValue) {
			if (alwaysButUsedWatchSelling.test(type))
				return turnoverValue;
			return VATHelper.getTurnoverFromMarginModelSelling(turnoverValue, marginValue);
		}
	}
	
	public Date date;
	public OvertimeFigures figures;
	
	private AnalyticsReport() {
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

	public static AnalyticsReport generateReportUnhanced() {
		AnalyticsReport report = new AnalyticsReport();
		List<AccountingLineAnalytic> lines = AccountingLineAnalytic.findAllForReportingUnhanced();
		for(AccountingLineAnalytic line : lines)
			report.loadAnalyticLineUnhanced(line);
		return report;
	}
		
	public static AnalyticsReport generateReport() {
		AnalyticsReport report = new AnalyticsReport();
		List<AccountingLineAnalytic> lines = AccountingLineAnalytic.findAllForReporting();
		for(AccountingLineAnalytic line : lines)
			report.loadAnalyticLine(line);
		return report;
	}
	
	
}
