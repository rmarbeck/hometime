package fr.hometime.utils;

import static org.junit.Assert.*;
import static org.fest.assertions.Assertions.assertThat;
import models.ServiceTest;

import org.junit.Test;

import play.Logger;

public class ServiceTestHelperTest {

	@Test
	public void newWatchWithNoProblemTest() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR;
		
		for (ServiceTest.MovementTypes mouvementtype : ServiceTest.MovementTypes.values())
			for (ServiceTest.LastServiceYear lastserviceyear : ServiceTest.LastServiceYear.values())
				for (ServiceTest.UsageFrequency usagefrequency : ServiceTest.UsageFrequency.values())
					for (ServiceTest.MovementComplexity movementComplexity : ServiceTest.MovementComplexity.values())
						if (usagefrequency != ServiceTest.UsageFrequency.NEVER) {
								serviceTest.lastServiceYear = lastserviceyear;
								serviceTest.movementType = mouvementtype;
								serviceTest.usageFrequency = usagefrequency;
								serviceTest.movementComplexity = movementComplexity;
								assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_MORE_THAN_5_YEARS);
						}
	}
	
	@Test
	public void newWatchWithNoProblemTestDoingSport() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = true;
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR;
		
		for (ServiceTest.MovementTypes mouvementtype : ServiceTest.MovementTypes.values())
			for (ServiceTest.LastServiceYear lastserviceyear : ServiceTest.LastServiceYear.values())
				for (ServiceTest.UsageFrequency usagefrequency : ServiceTest.UsageFrequency.values())
					for (ServiceTest.MovementComplexity movementComplexity : ServiceTest.MovementComplexity.values())
						if (usagefrequency != ServiceTest.UsageFrequency.NEVER) {
								serviceTest.lastServiceYear = lastserviceyear;
								serviceTest.movementType = mouvementtype;
								serviceTest.usageFrequency = usagefrequency;
								serviceTest.movementComplexity = movementComplexity;
								assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_2_TO_3_YEARS);
						}
	}
	
	@Test
	public void serviceForNewWatches1Test() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR_MINUS_6;
		serviceTest.lastServiceYear = ServiceTest.LastServiceYear.NONE_AFTER_BUYING;
		serviceTest.movementType = ServiceTest.MovementTypes.AUTO;
		serviceTest.usageFrequency = ServiceTest.UsageFrequency.MOST_OF_TIME;
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.THREE_HANDS;
		
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE);
	}
	
	@Test
	public void serviceForNewWatches2Test() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR_MINUS_7;
		serviceTest.lastServiceYear = ServiceTest.LastServiceYear.NONE_AFTER_BUYING;
		serviceTest.movementType = ServiceTest.MovementTypes.AUTO;
		serviceTest.usageFrequency = ServiceTest.UsageFrequency.MOST_OF_TIME;
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.THREE_HANDS;
		
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE);
	}
	
	@Test
	public void serviceForNewWatches3Test() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR_MINUS_3;
		serviceTest.lastServiceYear = ServiceTest.LastServiceYear.NONE_AFTER_BUYING;
		serviceTest.movementType = ServiceTest.MovementTypes.AUTO;
		serviceTest.usageFrequency = ServiceTest.UsageFrequency.MOST_OF_TIME;
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.THREE_HANDS;
		
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_2_TO_3_YEARS);
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.CHRONO;
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.NEXT_YEAR);
	}
	
	
	@Test
	public void serviceForNewWatches4Test() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR;
		serviceTest.lastServiceYear = ServiceTest.LastServiceYear.THIS_YEAR_MINUS_3;
		serviceTest.movementType = ServiceTest.MovementTypes.AUTO;
		serviceTest.usageFrequency = ServiceTest.UsageFrequency.MOST_OF_TIME;
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.CHRONO;
		
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_MORE_THAN_5_YEARS);
	}
	
	@Test
	public void serviceForNewWatches5Test() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR_MINUS_10;
		serviceTest.lastServiceYear = ServiceTest.LastServiceYear.THIS_YEAR;
		serviceTest.movementType = ServiceTest.MovementTypes.AUTO;
		serviceTest.usageFrequency = ServiceTest.UsageFrequency.MOST_OF_TIME;
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.THREE_HANDS;
		
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_4_YEARS);
		
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.CHRONO;
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_2_TO_3_YEARS);
	}
	
	@Test
	public void serviceForNewWatches6Test() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR_MINUS_11_20;
		serviceTest.lastServiceYear = ServiceTest.LastServiceYear.THIS_YEAR;
		serviceTest.movementType = ServiceTest.MovementTypes.AUTO;
		serviceTest.usageFrequency = ServiceTest.UsageFrequency.MOST_OF_TIME;
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.THREE_HANDS;
		
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_4_YEARS);
		
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.CHRONO;
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_2_TO_3_YEARS);
	}
	
	@Test
	public void serviceForNewWatches7Test() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.buildPeriod = ServiceTest.BuildPeriod.THIS_YEAR_MINUS_10;
		serviceTest.lastServiceYear = ServiceTest.LastServiceYear.THIS_YEAR;
		serviceTest.movementType = ServiceTest.MovementTypes.MANUAL;
		serviceTest.usageFrequency = ServiceTest.UsageFrequency.RARELY;
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.THREE_HANDS;
		
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_4_YEARS);
		
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.CHRONO;
		assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_2_TO_3_YEARS);
	}
	
	@Test
	public void serviceForOldWatchesTest() {
		serviceForWatches(ServiceTestHelper.DELAY_FOR_WATCH_TO_BECOME_OLD + 1, ServiceTest.BuildPeriod.values().length, ServiceTestHelper.DELAY_FOR_OLD_WATCH_BETWEEN_SERVICES);
		
		serviceForChrono(ServiceTestHelper.DELAY_FOR_WATCH_TO_BECOME_OLD + 1, ServiceTest.BuildPeriod.values().length, ServiceTestHelper.DELAY_FOR_OLD_WATCH_BETWEEN_SERVICES);
	}
	
	
	public void serviceForNewWatchesTest() {
		serviceForWatches(0, ServiceTestHelper.DELAY_FOR_WATCH_TO_BECOME_OLD, ServiceTestHelper.DELAY_FOR_NEW_WATCH_BETWEEN_SERVICES);
	}
	
	public void serviceForWatches(int startYear, int endYear, int delay) {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.THREE_HANDS;
		
		for (ServiceTest.MovementTypes mouvementtype : ServiceTest.MovementTypes.values())
			for (int buildValue = startYear ; buildValue < endYear ; buildValue++)
				for (ServiceTest.LastServiceYear lastserviceyear : ServiceTest.LastServiceYear.values())
					for (ServiceTest.UsageFrequency usagefrequency : ServiceTest.UsageFrequency.values())
						if (usagefrequency != ServiceTest.UsageFrequency.NEVER) {
								serviceTest.buildPeriod = ServiceTest.BuildPeriod.fromString(""+buildValue);
								serviceTest.lastServiceYear = lastserviceyear;
								serviceTest.movementType = mouvementtype;
								serviceTest.usageFrequency = usagefrequency;
								int nbOfYearsAfterRecommendedService;
								if (lastserviceyear.intValue() >= buildValue)
									nbOfYearsAfterRecommendedService = delay - buildValue;
								else
									nbOfYearsAfterRecommendedService = delay - lastserviceyear.intValue();
								if (nbOfYearsAfterRecommendedService >= 0)
									testServiceInGivenNbOfYears(nbOfYearsAfterRecommendedService, serviceTest);

						}
	}
	
	public void serviceForChrono(int startYear, int endYear, int delay) {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		serviceTest.doingSport = false;
		
		serviceTest.movementComplexity = ServiceTest.MovementComplexity.CHRONO;
		
		for (ServiceTest.MovementTypes mouvementtype : ServiceTest.MovementTypes.values())
			for (int buildValue = startYear ; buildValue < endYear ; buildValue++)
				for (ServiceTest.LastServiceYear lastserviceyear : ServiceTest.LastServiceYear.values())
					for (ServiceTest.UsageFrequency usagefrequency : ServiceTest.UsageFrequency.values())
						if (usagefrequency != ServiceTest.UsageFrequency.NEVER) {
								serviceTest.buildPeriod = ServiceTest.BuildPeriod.fromString(""+buildValue);
								serviceTest.lastServiceYear = lastserviceyear;
								serviceTest.movementType = mouvementtype;
								serviceTest.usageFrequency = usagefrequency;
								int nbOfYearsAfterRecommendedService;
								if (lastserviceyear.intValue() >= buildValue)
									nbOfYearsAfterRecommendedService = delay - buildValue - 1;
								else
									nbOfYearsAfterRecommendedService = delay - lastserviceyear.intValue() - 1;
								if (nbOfYearsAfterRecommendedService >= 0)
									testServiceInGivenNbOfYears(nbOfYearsAfterRecommendedService, serviceTest);

						}
	}
	
	
	private void testServiceInGivenNbOfYears(int nbOfYears, ServiceTest serviceTest) {
		switch (nbOfYears) {
			case 0:
				assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE);
				break;
			case 1:
				assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.NEXT_YEAR);
				break;
			case 2:
				assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_2_TO_3_YEARS);
				break;
			case 3:
				assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_2_TO_3_YEARS);
				break;
			case 4:
				assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_4_YEARS);
				break;
			case 5:
				assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_5_YEARS);
				break;
			default:
				assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.IN_MORE_THAN_5_YEARS);
		}
	}

	@Test
	public void serviceNowForWaterPoblemTest() {
		testForAllCases(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE, true, true, true);
		testForAllCases(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE, true, false, true);
		testForAllCases(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE, false, true, true);
		testForAllCases(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE, false, false, true);
	}
	
	@Test
	public void serviceNowForPowerReservePoblemTest() {
		testForAllCases(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE, true, true, true);
		testForAllCases(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE, true, true, false);
		testForAllCases(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE, false, true, true);
		testForAllCases(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE, false, true, false);
	}
	
	@Test
	public void softServiceNowForPerformancePoblemOnlyTest() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = true;
		
		for (ServiceTest.MovementTypes mouvementtype : ServiceTest.MovementTypes.values())
			for (ServiceTest.LastServiceYear lastserviceyear : ServiceTest.LastServiceYear.values())
				for (ServiceTest.UsageFrequency usagefrequency : ServiceTest.UsageFrequency.values())
					for (ServiceTest.BuildPeriod buildperiod : ServiceTest.BuildPeriod.values())
						if (usagefrequency != ServiceTest.UsageFrequency.NEVER)
							if (lastserviceyear.intValue() < 5) {
								serviceTest.lastServiceYear = lastserviceyear;
								serviceTest.movementType = mouvementtype;
								serviceTest.usageFrequency = usagefrequency;
								serviceTest.buildPeriod = buildperiod;
								assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.NOW_FOR_SOFT_SERVICE);
							}
	}
	
	@Test
	public void serviceCausedByNoUsageTest() {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = false;
		serviceTest.powerReserveIssue = false;
		serviceTest.performanceIssue = false;
		
		for (ServiceTest.MovementTypes mouvementtype : ServiceTest.MovementTypes.values())
			for (ServiceTest.LastServiceYear lastserviceyear : ServiceTest.LastServiceYear.values())
				for (ServiceTest.UsageFrequency usagefrequency : ServiceTest.UsageFrequency.values())
					for (ServiceTest.BuildPeriod buildperiod : ServiceTest.BuildPeriod.values())
						if (usagefrequency == ServiceTest.UsageFrequency.NEVER) {
							serviceTest.lastServiceYear = lastserviceyear;
							serviceTest.movementType = mouvementtype;
							serviceTest.usageFrequency = usagefrequency;
							serviceTest.buildPeriod = buildperiod;
							assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(ServiceTest.TestResult.NOW_FOR_FULL_SERVICE);
						}
	}
	
	private void testForAllCases(ServiceTest.TestResult expectedResult, boolean performanceIssue, boolean powerReserveIssue, boolean waterIssue) {
		ServiceTest serviceTest = new ServiceTest();
		serviceTest.waterIssue = waterIssue;
		serviceTest.performanceIssue = performanceIssue;
		serviceTest.powerReserveIssue = powerReserveIssue;
		
		for (ServiceTest.LastServiceYear lastserviceyear : ServiceTest.LastServiceYear.values())
			for (ServiceTest.MovementTypes mouvementtype : ServiceTest.MovementTypes.values())
				for (ServiceTest.UsageFrequency usagefrequency : ServiceTest.UsageFrequency.values())
					for (ServiceTest.BuildPeriod buildperiod : ServiceTest.BuildPeriod.values()) {
						serviceTest.lastServiceYear = lastserviceyear;
						serviceTest.movementType = mouvementtype;
						serviceTest.usageFrequency = usagefrequency;
						serviceTest.buildPeriod = buildperiod;
						assertThat(ServiceTestHelper.whenDoServiceIsRecommended(serviceTest)).isEqualTo(expectedResult);
					}
	}
}

