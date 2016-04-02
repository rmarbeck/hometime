package models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import views.html.admin.watch;
import fr.hometime.utils.DateHelper;
import models.AccountingLine.LineType;

public class StockReport {
	private final static String TOTAL_KEY = "admin.report.stock.total.label";
	
	public Date dateOfStockEntry;
	public Long nbOfDaysInStock;
	public String status;
	public String brand;
	public String modelName;
	public Float purchasePrice;
	public Float expectedSellingPrice;
	public Float expectedMarginRate;
	public boolean registryWarning;
	
	private StockReport() {
	}
	
	private StockReport(WatchToSell watchToSell) {
		this();
		this.status = watchToSell.status.toString();
		this.brand = watchToSell.brand.toString();
		this.modelName = watchToSell.model;
		this.purchasePrice = (float) watchToSell.purchasingPrice;
		this.expectedSellingPrice = (float) watchToSell.sellingPrice;
		this.expectedMarginRate = calculateExpectedMargin();
		this.dateOfStockEntry = watchToSell.purchaseDate;
		this.nbOfDaysInStock = calculateNbOfDaysInStock();
		this.registryWarning = defineRegistryWarning(watchToSell);
	}
	

	private static StockReport generateTotalLine(Float totalPurchasingPrices, Float totalExpectedSellingPrices) {
		StockReport reportLine = new StockReport();
		reportLine.dateOfStockEntry = new Date();
		reportLine.nbOfDaysInStock = reportLine.calculateNbOfDaysInStock();
		reportLine.brand = "TOTAL_KEY";
		reportLine.purchasePrice = totalPurchasingPrices;
		reportLine.expectedSellingPrice = totalExpectedSellingPrices;
		reportLine.expectedMarginRate = reportLine.calculateExpectedMargin();
		reportLine.modelName = "";
		reportLine.status = "";
		return reportLine;
		
	}
	private static boolean defineRegistryWarning(WatchToSell watchToSell) {
		if (watchToSell.shouldBeInRegistry)
			return ! watchToSell.isInRegistry;
		return false;
	}
	
	private long calculateNbOfDaysInStock() {
		if (dateOfStockEntry != null)
			return DateHelper.nbDaysTillToday(dateOfStockEntry);
		this.dateOfStockEntry = new Date();
		return -1;
	}
	
	private float calculateExpectedMargin() {
		if (purchasePrice != null && expectedSellingPrice != null && purchasePrice < expectedSellingPrice)
			return (expectedSellingPrice - purchasePrice) / purchasePrice * 100;
		return -1;
	}
	
	public static List<StockReport> generateReport() {
		List<StockReport> report = new ArrayList<StockReport>();
		List<WatchToSell> watches = WatchToSell.findAllByPurchaseDateAsc();
		float totalPurchasingPrices = 0f;
		float totalExpectedSellingPrices = 0f;
		for(WatchToSell watch : watches)
			if (lineIsSellingWatch(watch)) {
				report.add(new StockReport(watch));
				totalPurchasingPrices += watch.purchasingPrice;
				totalExpectedSellingPrices += watch.sellingPrice;
			}
		report.add(generateTotalLine(totalPurchasingPrices, totalExpectedSellingPrices));
		return report;
	}
	
	private static boolean lineIsSellingWatch(WatchToSell watch) {
		switch (watch.status) {
			case TO_SELL:
			case RESERVED_FOR_A_CUSTOMER:
				return true;
			default :
				return false;
		}
	}
}
