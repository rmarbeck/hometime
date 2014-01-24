package fr.hometime.utils;

import models.ServiceTest;


/**
 * Helper for calculating service date
 * 
 * @author Raphael
 *
 */

public class ServiceTestHelper {
	
	protected static int DELAY_FOR_WATCH_TO_BECOME_OLD = 10;
	protected static int DELAY_FOR_OLD_WATCH_BETWEEN_SERVICES = 5;
	protected static int DELAY_FOR_NEW_WATCH_BETWEEN_SERVICES = 8;
	private static int LAST_SERVICE_NB_OF_YEARS_MAX = 11;
	
	private ServiceTest serviceToTest = null;
	
	private ServiceTestHelper(ServiceTest serviceToTest) {
		this.serviceToTest = serviceToTest;
	}
	
	public static ServiceTest.TestResult whenDoServiceIsRecommended(ServiceTest serviceToTest) {
		return new ServiceTestHelper(serviceToTest).whenDoServiceIsRecommended();
	}
	
	private ServiceTest.TestResult whenDoServiceIsRecommended() {
		if (shouldDoItNowForFullService())
			return ServiceTest.TestResult.NOW_FOR_FULL_SERVICE;
		
		if (shouldDoItNowForSoftService(serviceToTest))
			return ServiceTest.TestResult.NOW_FOR_SOFT_SERVICE;
		
		switch(calculateNumberOfYearsBeforeRecommendedService()) {
			case 0:
				return ServiceTest.TestResult.NOW_FOR_FULL_SERVICE;
			case 1:
				return ServiceTest.TestResult.NEXT_YEAR;
			case 2:
			case 3:
				return ServiceTest.TestResult.IN_2_TO_3_YEARS;
			case 4:
				return ServiceTest.TestResult.IN_4_YEARS;
			case 5:
				return ServiceTest.TestResult.IN_5_YEARS;
			default:
				return ServiceTest.TestResult.IN_MORE_THAN_5_YEARS;
		}
	}

	private boolean shouldDoItNowForFullService() {
		if (hasAMajorFailure())
			return true;
		if (watchIsOld() && lastServiceIsOutdatedForAnOldWatch())
			return true;
		if (watchIsNew() && lastServiceIsOutdatedForANewWatch())
			return true;
		return false;
	}

	private boolean hasAMajorFailure() {
		if (serviceToTest.powerReserveIssue || serviceToTest.waterIssue)
			return true;
		if (serviceToTest.usageFrequency == ServiceTest.UsageFrequency.NEVER)
			return true;
		return false;
	}
	
	private static boolean shouldDoItNowForSoftService(ServiceTest serviceToTest) {
		if (serviceToTest.performanceIssue)
			return true;
		return false;
	}
	
	private int calculateNumberOfYearsBeforeRecommendedService() {
		if (watchIsOld() && lastServiceIsNOTOutdatedForAnOldWatch())
			return DELAY_FOR_OLD_WATCH_BETWEEN_SERVICES - getNbOfYearsFromLastService();
		if (watchIsNew() && lastServiceIsNOTOutdatedForANewWatch())
			return DELAY_FOR_NEW_WATCH_BETWEEN_SERVICES - getNbOfYearsFromLastService();
		return 0;
	}
	
	private boolean watchIsOld() {
		return serviceToTest.buildPeriod.intValue() > DELAY_FOR_WATCH_TO_BECOME_OLD;
	}
	
	private boolean watchIsNew() {
		return !watchIsOld();
	}
	
	private boolean lastServiceIsOutdatedForAnOldWatch() {
		return getNbOfYearsFromLastService() > DELAY_FOR_OLD_WATCH_BETWEEN_SERVICES;
	}
	
	private boolean lastServiceIsNOTOutdatedForAnOldWatch() {
		return !lastServiceIsOutdatedForAnOldWatch();
	}
	
	private boolean lastServiceIsOutdatedForANewWatch() {
		return getNbOfYearsFromLastService() > DELAY_FOR_NEW_WATCH_BETWEEN_SERVICES;
	}
	
	private boolean lastServiceIsNOTOutdatedForANewWatch() {
		return !lastServiceIsOutdatedForANewWatch();
	}
	
	private int getNbOfYearsFromLastService() {
		int nbOfYearsFromLastService = LAST_SERVICE_NB_OF_YEARS_MAX;
		nbOfYearsFromLastService = Math.min(nbOfYearsFromLastService, getNbOfYearsFromBuild());
		if (serviceToTest.lastServiceYear == ServiceTest.LastServiceYear.NONE_AFTER_BUYING ||
				serviceToTest.lastServiceYear == ServiceTest.LastServiceYear.UNKNOWN)
			return nbOfYearsFromLastService;

		return Math.min(nbOfYearsFromLastService, serviceToTest.lastServiceYear.intValue());
	}
	
	private int getNbOfYearsFromBuild() {
		if (serviceToTest.buildPeriod.intValue() < 11)
			return serviceToTest.buildPeriod.intValue();
		return (((serviceToTest.buildPeriod.intValue() - 10) * 10) + 5);
	}
}
