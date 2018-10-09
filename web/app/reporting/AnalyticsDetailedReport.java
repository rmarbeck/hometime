package reporting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fr.hometime.utils.UniqueAccountingNumber;
import models.AccountingDocument;
import models.AccountingLineAnalytic;

public class AnalyticsDetailedReport {
	public String year;
	public String month;
	public String financialYearIndex;
	public String analyticCode;
	public String analyticCodeXX;
	public String analyticCodeXXX;
	public Float price = 0f;
	public Float cost = 0f;
	public boolean checked = false;
	public boolean doubleChecked = false;
	
	private AnalyticsDetailedReport(AccountingLineAnalytic analyticLine) {
		AccountingDocument currentDocument = analyticLine.accountingLine.document;
		Long lAnalyticCode = analyticLine.analyticCode.analyticCode;
		String sInvoiceUAN = InvoiceLineReport.guessUAN(currentDocument).orElse("");
		UniqueAccountingNumber invoiceUAN = UniqueAccountingNumber.fromString(sInvoiceUAN, false);
		
		this.year = invoiceUAN.extractYearFromUAN()+"";
		this.month = invoiceUAN.extractMonthFromUAN()+"";
		this.financialYearIndex = UniqueAccountingNumber.getFinancialYearSequenceNumberFromUAN(invoiceUAN).get()+"";
		
		String foundAnalyticCode = lAnalyticCode.toString(); 
		
		this.analyticCode = foundAnalyticCode;
		analyticCodeXX =  foundAnalyticCode.substring(0, foundAnalyticCode.length()-2);
		analyticCodeXXX =  foundAnalyticCode.substring(0, foundAnalyticCode.length()-1);
		
		if (analyticLine.price > 0f)
			price = analyticLine.price;
		
		if (analyticLine.cost > 0f)
			cost = analyticLine.cost;
		
		checked = analyticLine.checked;
		doubleChecked = analyticLine.doubleChecked;
	}
	
	private void addPriceAndCost(Float price, Float cost) {
		this.price+=price;
		this.cost+=cost;
	}
		
	public static List<AnalyticsDetailedReport> generateReport() {
		return generateReport(() -> AccountingLineAnalytic.findAll());
	}
	
	public static List<AnalyticsDetailedReport> generateReportEnhanced(Long nbOfMonths) {
		return generateReport(() -> AccountingLineAnalytic.findAll());
	}
	
	private static List<AnalyticsDetailedReport> generateReport(Supplier<List<AccountingLineAnalytic>> supplier) {
		HashMap<String, AnalyticsDetailedReport> reportBuilder = new HashMap<>();
		List<AccountingLineAnalytic> lines = supplier.get();
		for(AccountingLineAnalytic line : lines)
			addNewLine(new AnalyticsDetailedReport(line), reportBuilder);
		return reportBuilder.values().stream().sorted(Comparator.comparing((value) -> evaluateKey(value))).collect(Collectors.toList());
	}

	private static void addNewLine(AnalyticsDetailedReport newLine, HashMap<String, AnalyticsDetailedReport> reportBuilder) {
		String key = evaluateKey(newLine);
		if (reportBuilder.containsKey(key)) {
			reportBuilder.get(key).addPriceAndCost(newLine.price, newLine.cost);
		} else {
			reportBuilder.put(key, newLine);
		}
	}
	
	private static String evaluateKey(AnalyticsDetailedReport newLine) {
		return newLine.year+newLine.month+newLine.analyticCode;
	}
	
}
