package fr.hometime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import models.AccountingLine;
import models.AccountingLineAnalytic;
import models.AccountingLineAnalyticPreset;
import models.AccountingLineAnalyticPresetItem;
import models.Invoice;
import models.WatchToSell;
import play.Logger;

/**
 * Helper for managing Accounting Line Analytics
 * 
 * @author Raphael
 *
 */

public class AccountingAnalyticsHelper {
	private static Long META_CODE_FOR_PURCHASING_USED_WATCH_FROM_PRO = 10021l;
	private static Long META_CODE_FOR_PURCHASING_USED_WATCH_FROM_PART = 10011l;
	private static Long META_CODE_FOR_PURCHASING_USED_WATCH_FROM_US = 10031l;
	private static Long META_CODE_FOR_PURCHASING_USED_WATCH = 10991l;
	private static Long META_CODE_FOR_PURCHASING_NEW_WATCH_FROM_PRO = 11021l;
	private static Long META_CODE_FOR_PURCHASING_NEW_WATCH_FROM_PART = 11011l;
	private static Long META_CODE_FOR_PURCHASING_NEW_WATCH_FROM_US = 11101l;
	private static Long META_CODE_FOR_PURCHASING_NEW_WATCH = 11991l;
	
	private static Long META_CODE_FOR_PARTIAL_SERVICING_QUARTZ_WATCH = 20551l;
	private static Long META_CODE_FOR_PARTIAL_SERVICING_QUARTZ_WATCH_AND_POLISHING = 20541l;
	private static Long META_CODE_FOR_PARTIAL_SERVICING_QUARTZ_WATCH_WITHOUT_WATERING_BUT_POLISHING = 20561l;
	private static Long META_CODE_FOR_PARTIAL_SERVICING_QUARTZ_WATCH_WITHOUT_WATERING = 20571l;
	
	private static Long META_CODE_FOR_PARTIAL_SERVICING_MECANICAL_WATCH = 20051l;
	private static Long META_CODE_FOR_PARTIAL_SERVICING_MECANICAL_WATCH_AND_POLISHING = 20041l;
	private static Long META_CODE_FOR_PARTIAL_SERVICING_MECANICAL_WATCH_WITHOUT_WATERING_BUT_POLISHING = 20061l;
	private static Long META_CODE_FOR_PARTIAL_SERVICING_MECANICAL_WATCH_WITHOUT_WATERING = 20071l;
	
	private static Long META_CODE_FOR_SERVICING_MECANICAL_WATCH = 20011l;
	private static Long META_CODE_FOR_RESTORING_MECANICAL_WATCH = 20031l;
	private static Long META_CODE_FOR_REPAIRING_MECANICAL_WATCH = 20031l;
	
	private static Long META_CODE_FOR_SPARE_PARTS_ON_ORDER = 31011l;
	
	
	public static Long findMetaCodeForSellingAWatch(WatchToSell watch) {
		if (watch.isNew) {
			switch (watch.ownerStatus) {
				case OWNED_BY_CUSTOMER: return META_CODE_FOR_PURCHASING_NEW_WATCH_FROM_PART;
				case OWNED_BY_PARTNER: return META_CODE_FOR_PURCHASING_NEW_WATCH_FROM_PRO;
				case OWNED_BY_US: return META_CODE_FOR_PURCHASING_NEW_WATCH_FROM_US;
				default: return META_CODE_FOR_PURCHASING_NEW_WATCH;
			}
		} else {
			switch (watch.ownerStatus) {
				case OWNED_BY_CUSTOMER: return META_CODE_FOR_PURCHASING_USED_WATCH_FROM_PART;
				case OWNED_BY_PARTNER: return META_CODE_FOR_PURCHASING_USED_WATCH_FROM_PRO;
				case OWNED_BY_US: return META_CODE_FOR_PURCHASING_USED_WATCH_FROM_US;
				default: return META_CODE_FOR_PURCHASING_USED_WATCH;
			}
		}
	}
	
	public static Long findMetaCodeForPartialServicingQuartzWatch(boolean withWatering, boolean withPolishing) {
		if (withWatering) {
			if (withPolishing)
				return META_CODE_FOR_PARTIAL_SERVICING_QUARTZ_WATCH_AND_POLISHING;
			return META_CODE_FOR_PARTIAL_SERVICING_QUARTZ_WATCH;
		} else {
			if (withPolishing)
				return META_CODE_FOR_PARTIAL_SERVICING_QUARTZ_WATCH_WITHOUT_WATERING_BUT_POLISHING;
			return META_CODE_FOR_PARTIAL_SERVICING_QUARTZ_WATCH_WITHOUT_WATERING;
		}
	}
	
	public static Long findMetaCodeForPartialServicingMecanicalWatch(boolean withWatering, boolean withPolishing) {
		if (withWatering) {
			if (withPolishing)
				return META_CODE_FOR_PARTIAL_SERVICING_MECANICAL_WATCH_AND_POLISHING;
			return META_CODE_FOR_PARTIAL_SERVICING_MECANICAL_WATCH;
		} else {
			if (withPolishing)
				return META_CODE_FOR_PARTIAL_SERVICING_MECANICAL_WATCH_WITHOUT_WATERING_BUT_POLISHING;
			return META_CODE_FOR_PARTIAL_SERVICING_MECANICAL_WATCH_WITHOUT_WATERING;
		}
	}
	
	public static Long findMetaCodeForServicingMecanicalWatch() {
		return META_CODE_FOR_SERVICING_MECANICAL_WATCH;
	}
	
	public static Long findMetaCodeForRepairingMecanicalWatch() {
		return META_CODE_FOR_REPAIRING_MECANICAL_WATCH;
	}
	
	public static Long findMetaCodeForSpareParts() {
		return META_CODE_FOR_SPARE_PARTS_ON_ORDER;
	}
	
	public static void addAnalyticsToLine(AccountingLine line, AccountingLineAnalyticPreset preset, Float oneTimeCostValue) {
		if (line.analytics == null || line.analytics.size() == 0) {
			createAccountingLineAnalyticsFromPresetItems(line, preset, oneTimeCostValue);
		} else {
			Logger.debug("Doing nothing as analytic lines already exist.");
		}
	}
	
	public static void addAnalyticsToLine(Invoice invoice, int lineIndexInInvoice, Long presetId, Float oneTimeCostValue) {
		Optional<AccountingLine> accountingLine = retrieveAccountingLineFromInvoice(invoice, lineIndexInInvoice);
		Optional<AccountingLineAnalyticPreset> preset = retrievePreset(presetId);
		if (accountingLine.isPresent() && preset.isPresent()) {
			addAnalyticsToLine(accountingLine.get(), preset.get(), oneTimeCostValue);
		} else {
			Logger.debug("NO accounting line or preset while adding Analytics.");
		}
	}
	
	private static Optional<AccountingLine> retrieveAccountingLineFromInvoice(Invoice invoice, int lineIndexInInvoice) {
		if (invoice == null || invoice.document.lines == null)
			return Optional.empty();
		if (invoice.document.lines.size() >= lineIndexInInvoice)
			return Optional.of(invoice.document.lines.get(lineIndexInInvoice));
		return Optional.empty();
	}
	
	private static Optional<AccountingLineAnalyticPreset> retrievePreset(Long presetId) {
		AccountingLineAnalyticPreset preset = AccountingLineAnalyticPreset.findById(presetId);
		if (preset != null)
			return Optional.of(preset);
		return Optional.empty();
	}
	
	private static Optional<List<AccountingLineAnalytic>> createAccountingLineAnalyticsFromPresetItems(AccountingLine line, AccountingLineAnalyticPreset preset, Float oneTimeCostValue) {
		if (isAccountingLineFullyQualified(line))
			return Optional.empty();
		
		List<AccountingLineAnalytic> analytics = new ArrayList<AccountingLineAnalytic>();
		
		for (AccountingLineAnalyticPresetItem item : preset.accountingLineAnalyticItems)
			analytics.add(createAccountingLineAnalyticFromPresetItem(line, item, oneTimeCostValue));
		
		line.analytics = analytics;
		
		return Optional.of(analytics);
	}
	
	private static AccountingLineAnalytic createAccountingLineAnalyticFromPresetItem(AccountingLine line, AccountingLineAnalyticPresetItem presetItem) {
		return createAccountingLineAnalyticFromPresetItem(line, presetItem, -1f);
	}
	
	private static AccountingLineAnalytic createAccountingLineAnalyticFromPresetItem(AccountingLine line, AccountingLineAnalyticPresetItem presetItem, Float oneTimeCostValue) {
		AccountingLineAnalytic newAnalytic = new AccountingLineAnalytic(line, presetItem.analyticCode);
		newAnalytic.price = chooseTheRightValue(presetItem.fixedPrice, presetItem.proportionalPrice, getFLoatValue(line));

		if (! presetItem.oneTimeCost || oneTimeCostValue == -1f) {
			newAnalytic.cost = chooseTheRightValue(presetItem.fixedCost, presetItem.proportionalCost, getFLoatValue(line));	
		} else {
			newAnalytic.cost = oneTimeCostValue; 
		}
		
		return newAnalytic;
	}
	
	private static Float chooseTheRightValue(Float fixedValue, Float proportionalValue, Float baseValue) {
		if (fixedValue != -1) {
			return fixedValue; 
		} else if (proportionalValue != -1) {
			return proportionalValue * baseValue / 100;
		}
		return -1f;
	}
	
	public static boolean isAnalysed(Invoice invoice) {
		for(AccountingLine line : invoice.document.lines)
			if (!isAccountingLineFullyQualified(line))
				return false;
		return true;
	}
	
	public static boolean isAccountingLineFullyQualified(AccountingLine line) {
		if (isNotZero(line) && isAnInvoice(line)) {
			return amountQualified(line).compareTo(getFLoatValue(line)) >= 0;
		}
		return true;
	}
	
	private static boolean isNotZero(AccountingLine line) {
		return getFLoatValue(line).floatValue() != 0f;
	}
	
	public static Float getFLoatValue(AccountingLine line) {
		if (line != null)
			return (line.unitPrice * line.unit);
		return 0F;
	}
	
	private static boolean isAnInvoice(AccountingLine line) {
		return true;
	}
	
	private static Float amountQualified(AccountingLine line) {
		List<AccountingLineAnalytic> analytics = line.analytics;
		return analytics.stream().map(analytic -> analytic.price).reduce(0F, Float::sum);
	}
}