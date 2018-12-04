package fr.hometime.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;


public class PhoneNumberTest {

	@Test
	public void frenchMobileNumbers() {
		assertThat(PhoneNumberHelper.getFrenchPhoneNumberInInternationalFormat("0033620383295")).isEqualTo("+33620383295");
		assertThat(PhoneNumberHelper.getFrenchPhoneNumberInInternationalFormat("+33620383295")).isEqualTo("+33620383295");
		assertThat(PhoneNumberHelper.getFrenchPhoneNumberInInternationalFormat("+33 620383295")).isEqualTo("+33620383295");
		assertThat(PhoneNumberHelper.getFrenchPhoneNumberInInternationalFormat("+33 6 20 38 32 95")).isEqualTo("+33620383295");
		assertThat(PhoneNumberHelper.getFrenchPhoneNumberInInternationalFormat("06  20  383295")).isEqualTo("+33620383295");
		assertThat(PhoneNumberHelper.tryToGetFrenchPhoneNumberInInternationalFormat("0033620383295")).isNotEqualTo(Optional.empty());
	}
	
	@Test
	public void nonFrenchMobileNumbers() {
		assertThat(PhoneNumberHelper.isItAFrenchMobilePhoneNumber("0033420383295")).isFalse();
		assertThat(PhoneNumberHelper.isItAFrenchMobilePhoneNumber("+33420383295")).isFalse();
		assertThat(PhoneNumberHelper.isItAFrenchMobilePhoneNumber("0420383295")).isFalse();
		assertThat(PhoneNumberHelper.isItAFrenchMobilePhoneNumber("04203832  95")).isFalse();
		assertThat(PhoneNumberHelper.isItAFrenchMobilePhoneNumber("+32620383295")).isFalse();
		assertThat(PhoneNumberHelper.isItAFrenchMobilePhoneNumber("0032620383295")).isFalse();
		assertThat(PhoneNumberHelper.tryToGetFrenchPhoneNumberInInternationalFormat("0033420383295")).isEqualTo(Optional.empty());
	}
	
}

