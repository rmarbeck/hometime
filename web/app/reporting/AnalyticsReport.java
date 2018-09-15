package reporting;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import fr.hometime.utils.UniqueAccountingNumber;
import models.AccountingDocument;
import models.AccountingLine;
import models.AccountingLine.LineType;

public class AnalyticsReport {
	private final static Predicate<Long> alwaysTrue = (value) -> true;
	private final static Predicate<Long> isWatchSelling = (value) -> (value > 10000L && value < 15000L);
	private final static Predicate<Long> isUsedWatchSelling = (value) -> (value > 10000L && value < 11000L);
	private final static Predicate<Long> isNewWatchSelling = (value) -> (value > 11000L && value < 12000L);
	private final static Predicate<Long> isAccessorySelling = (value) -> (value > 15000L && value < 20000L);
	private final static Predicate<Long> isLocalServicing = (value) -> (value > 20000L && value < 25000L);
	private final static Predicate<Long> isExternalServicing = (value) -> (value > 25000L && value < 30000L);
	private final static Predicate<Long> isValueAddedServiceProviding = (value) -> (value > 30000L && value < 40000L);
	
	public class OvertimeFigures {
		private final Predicate<UniqueAccountingNumber> isCurrentMonth = (value) -> (value.getYearAndMonthFromUAN().equals(YearMonth.now()));
		private final Predicate<UniqueAccountingNumber> isLastMonth = (value) -> (value.getYearAndMonthFromUAN().equals(YearMonth.now().minusMonths(1)));
		
		private final Predicate<UniqueAccountingNumber> isCurrentAccounting = (value) -> (false);
		private final Predicate<UniqueAccountingNumber> isLastAccounting = (value) -> (false);
		
		Float currentMonthMargin = 0f;
		Float lastMonthMargin = 0f;
		Float currentAccountingMargin = 0f;
		Float lastAccountingMargin = 0f;
		Float currentMonthTurnOver = 0f;
		Float lastMonthTurnOver = 0f;
		Float currentAccountingTurnOver = 0f;
		Float lastAccountingTurnOver = 0f;
		Float currentMonthMarginLocalServicingOnly = 0f;
		Float lastMonthMarginLocalServicingOnly = 0f;
		Float currentAccountingMarginLocalServicingOnly = 0f;
		Float lastAccountingMarginLocalServicingOnly = 0f;
		Float currentMonthTurnOverLocalServicingOnly = 0f;
		Float lastMonthTurnOverLocalServicingOnly = 0f;
		Float currentAccountingTurnOverLocalServicingOnly = 0f;
		Float lastAccountingTurnOverLocalServicingOnly = 0f;
		Float currentMonthMarginSellingOnly = 0f;
		Float lastMonthMarginSellingOnly = 0f;
		Float currentAccountingMarginSellingOnly = 0f;
		Float lastAccountingMarginSellingOnly = 0f;
		Float currentMonthTurnOverSellingOnly = 0f;
		Float lastMonthTurnOverSellingOnly = 0f;
		Float currentAccountingTurnOverSellingOnly = 0f;
		Float lastAccountingTurnOverSellingOnly = 0f;
		
		public void add(UniqueAccountingNumber uan, Long type, float marginValue, float turnoverValue) {
			currentMonthMargin += add(uan, isCurrentMonth, type, alwaysTrue, marginValue);
			lastMonthMargin += add(uan, isLastMonth, type, alwaysTrue, marginValue);
			currentAccountingMargin += add(uan, isCurrentAccounting, type, alwaysTrue, marginValue);
			lastAccountingMargin += add(uan, isLastAccounting, type, alwaysTrue, marginValue);
			currentMonthTurnOver += add(uan, isCurrentMonth, type, alwaysTrue, turnoverValue);
			lastMonthTurnOver += add(uan, isLastMonth, type, alwaysTrue, turnoverValue);
			currentAccountingTurnOver += add(uan, isCurrentAccounting, type, alwaysTrue, turnoverValue);
			lastAccountingTurnOver += add(uan, isLastAccounting, type, alwaysTrue, turnoverValue);
			
			currentMonthMarginLocalServicingOnly += add(uan, isCurrentMonth, type, isLocalServicing, marginValue);
			lastMonthMarginLocalServicingOnly += add(uan, isLastMonth, type, isLocalServicing, marginValue);
			currentAccountingMarginLocalServicingOnly += add(uan, isCurrentAccounting, type, isLocalServicing, marginValue);
			lastAccountingMarginLocalServicingOnly += add(uan, isLastAccounting, type, isLocalServicing, marginValue);
			currentMonthTurnOverLocalServicingOnly += add(uan, isCurrentMonth, type, isLocalServicing, turnoverValue);
			lastMonthTurnOverLocalServicingOnly += add(uan, isLastMonth, type, isLocalServicing, turnoverValue);
			currentAccountingTurnOverLocalServicingOnly += add(uan, isCurrentAccounting, type, isLocalServicing, turnoverValue);
			lastAccountingTurnOverLocalServicingOnly += add(uan, isLastAccounting, type, isLocalServicing, turnoverValue);
			
			currentMonthMarginSellingOnly += add(uan, isCurrentMonth, type, isWatchSelling, marginValue);
			lastMonthMarginSellingOnly += add(uan, isLastMonth, type, isWatchSelling, marginValue);
			currentAccountingMarginSellingOnly += add(uan, isCurrentAccounting, type, isWatchSelling, marginValue);
			lastAccountingMarginSellingOnly += add(uan, isLastAccounting, type, isWatchSelling, marginValue);
			currentMonthTurnOverSellingOnly += add(uan, isCurrentMonth, type, isWatchSelling, turnoverValue);
			lastMonthTurnOverSellingOnly += add(uan, isLastMonth, type, isWatchSelling, turnoverValue);
			currentAccountingTurnOverSellingOnly += add(uan, isCurrentAccounting, type, isWatchSelling, turnoverValue);
			lastAccountingTurnOverSellingOnly += add(uan, isLastAccounting, type, isWatchSelling, turnoverValue);
			
		}
		
		private float add(UniqueAccountingNumber uan, Predicate<UniqueAccountingNumber> shouldBeAddedByDate, Long type, Predicate<Long> shouldBeAddedByType, float value) {
			return (shouldBeAddedByDate.test(uan) && shouldBeAddedByType.test(type))?value:0f;
		}
	}
	
	public Date date;
	public OvertimeFigures figures;
	
	private AnalyticsReport() {
		figures = new OvertimeFigures();
	}
	
	private void loadInvoiceLine(AccountingLine currentLine) {
		AccountingDocument currentDocument = currentLine.document;
		String invoiceType = InvoiceLineReport.guessInvoiceType(currentDocument).orElse("");
		String invoiceUAN = InvoiceLineReport.guessUAN(currentDocument).orElse("");
		
		float lineAmount = 0f;
		if (currentLine.unitPrice != null && currentLine.unit != null)
			lineAmount = currentLine.unitPrice * currentLine.unit;
		
		this.figures.add(UniqueAccountingNumber.fromString(invoiceUAN, false), 0l, 0f, 0f);
	}

		
	public static AnalyticsReport generateReport() {
		AnalyticsReport report = new AnalyticsReport();
		List<AccountingLine> lines = AccountingLine.findAllByDescendingDate();
		for(AccountingLine line : lines)
			if (InvoiceLineReport.lineIsInvoice(line))
				if (InvoiceLineReport.lineIsNotEmpty(line))
					report.loadInvoiceLine(line);
		return report;
	}
	
	
}
