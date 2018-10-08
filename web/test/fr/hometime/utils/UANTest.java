package fr.hometime.utils;

import static org.junit.Assert.*;

import java.time.YearMonth;
import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;


public class UANTest {

	@Test
	public void firstOneIsGeneratedCorrectly() {
		UniqueAccountingNumber uan1 = UniqueAccountingNumber.fromString("201810-020", false);
		UniqueAccountingNumber uan2 = UniqueAccountingNumber.fromString("201810-021", false);
		assertThat(uan1.getYearAndMonthFromUAN()).isEqualTo(YearMonth.of(2018, 10));
	}
	
	@Test
	public void uanSequencingOrderTest() {
		UniqueAccountingNumber uan1 = UniqueAccountingNumber.fromString("201810-020", false);
		UniqueAccountingNumber uan2 = UniqueAccountingNumber.fromString("201810-021", false);
		assertThat(uan1.isOlderThan(uan2)).isEqualTo(false);
	}
	
	@Test
	public void financialYearTests() {
		UniqueAccountingNumber uan1 = UniqueAccountingNumber.fromString("201810-020", false);
		UniqueAccountingNumber uan2 = UniqueAccountingNumber.fromString("201810-021", false);
		UniqueAccountingNumber uan3 = UniqueAccountingNumber.fromString("201710-041", false);
		assertThat(UniqueAccountingNumber.areTheseUANInSameFinancialYear(uan1, uan2)).isEqualTo(Optional.of(true));
		assertThat(UniqueAccountingNumber.areTheseUANInSameFinancialYear(uan1, uan3)).isEqualTo(Optional.of(false));
	}
	
	@Test
	public void sameMonthTests() {
		UniqueAccountingNumber uan1 = UniqueAccountingNumber.fromString("201810-020", false);
		UniqueAccountingNumber uan2 = UniqueAccountingNumber.fromString("201810-021", false);
		UniqueAccountingNumber uan3 = UniqueAccountingNumber.fromString("201710-041", false);
		assertThat(UniqueAccountingNumber.areTheseUANInSameMonth(uan1, uan2)).isEqualTo(Optional.of(true));
		assertThat(UniqueAccountingNumber.areTheseUANInSameMonth(uan1, uan3)).isEqualTo(Optional.of(false));
	}
	
	@Test
	public void currentFinancialYearTests() {
		String currentYear = YearMonth.now().getYear()+"";
		String previousYear = (YearMonth.now().getYear()-1)+"";
		String currentMonth = YearMonth.now().getMonthValue()+"";
		UniqueAccountingNumber duan1 = UniqueAccountingNumber.fromString(currentYear+currentMonth+"-020", false);
		UniqueAccountingNumber duan2 = UniqueAccountingNumber.fromString(currentYear+currentMonth+"-021", false);
		UniqueAccountingNumber duan3 = UniqueAccountingNumber.fromString(previousYear+currentMonth+"-020", false);
		assertThat(UniqueAccountingNumber.getCurrentFinancialYearSequenceNumber()).isEqualTo(6);
		assertThat(UniqueAccountingNumber.getFinancialYearSequenceNumberFromUAN(duan1)).isEqualTo(Optional.of(6));
		assertThat(duan1.isInCurrentFinancialYear()).isEqualTo(true);
		assertThat(UniqueAccountingNumber.getFinancialYearSequenceNumberFromUAN(duan1).get()).isEqualTo(UniqueAccountingNumber.getCurrentFinancialYearSequenceNumber());
		
		assertThat(UniqueAccountingNumber.getFinancialYearSequenceNumberFromUAN(duan3).get()).isNotEqualTo(UniqueAccountingNumber.getCurrentFinancialYearSequenceNumber());
		assertThat(duan3.isInCurrentFinancialYear()).isEqualTo(false);
		assertThat(duan3.isInPreviousFinancialYear()).isEqualTo(true);
	}
}

