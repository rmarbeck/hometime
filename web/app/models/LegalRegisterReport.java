package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.hometime.utils.DateHelper;

public class LegalRegisterReport {
	
	public Long num;
	public Date dateOfStockEntry;
	public String description;
	public String descriptionForHelper;
	public String seller;
	public Float purchasePrice;
	public boolean registryWarning;
	
	private LegalRegisterReport() {
	}
	
	private LegalRegisterReport(WatchToSell watchToSell, Long index) {
		this();
		this.num = index;
		this.description = generateDescription(watchToSell);
		this.descriptionForHelper = generateDescriptionForHelper(watchToSell);
		this.purchasePrice = (float) watchToSell.purchasingPrice;
		this.dateOfStockEntry = watchToSell.purchaseDate;
		this.seller = watchToSell.seller;
		this.registryWarning = defineRegistryWarning(watchToSell);
	}
	
	private static boolean defineRegistryWarning(WatchToSell watchToSell) {
		if (watchToSell.shouldBeInRegistry)
			return ! watchToSell.isInRegistry;
		return false;
	}
	
	private static String generateDescription(WatchToSell watchToSell) {
		StringBuilder description = new StringBuilder();
		description.append(watchToSell.brand.toString());
		description.append(" ");
		description.append(watchToSell.model);
		description.append("\n");
		description.append("ref : "+watchToSell.reference);
		description.append("\n");
		description.append("n° : "+watchToSell.serial);
		return description.toString();
	}
	
	private static String generateDescriptionForHelper(WatchToSell watchToSell) {
		StringBuilder description = new StringBuilder();
		description.append("Montre ");
		description.append(watchToSell.brand.toString());
		description.append(" ");
		description.append(watchToSell.model);
		description.append("\n");
		description.append("ref. "+watchToSell.reference);
		description.append(" n° "+watchToSell.serial);
		return description.toString();
	}
	
	public static List<LegalRegisterReport> generateReport(Long startingIndex) {
		List<LegalRegisterReport> report = new ArrayList<LegalRegisterReport>();
		List<WatchToSell> watches = WatchToSell.findAllByPurchaseDateAsc();
		Long index = startingIndex;
		for(WatchToSell watch : watches)
			if (lineIsSupposedToBeDisplayed(watch)) {
				report.add(new LegalRegisterReport(watch, index++));

			}
		return report;
	}
	
	public static List<LegalRegisterReport> generateReport() {
		return generateReport(1L);
	}
	
	private static boolean lineIsSupposedToBeDisplayed(WatchToSell watch) {
		return watch.shouldBeInRegistry;
	}
	
	public String getCssClass() {
		if (registryWarning)
			return "shouldAdd";
		return "shouldNotAdd";
	}
}
