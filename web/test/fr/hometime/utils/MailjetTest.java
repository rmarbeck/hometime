package fr.hometime.utils;

import static org.junit.Assert.*;

import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;

public class MailjetTest extends WithApplication {
	
	
	public void testingPresenceOfAContact() throws Exception {
		Optional<Integer> idOfContactFound = MailjetAdapterv3_1.getContactByEmail("rmarbeck@gmail.com");
		assertThat(idOfContactFound.isPresent());
		assertThat(idOfContactFound.get().equals(979065101));
	}
	
	
	public void testingPresenceOfAContactsList() throws Exception {
		Optional<Integer> idOfContactsListFound = MailjetAdapterv3_1.getContactsListByName("Moi");
		assertThat(idOfContactsListFound.isPresent());
		System.out.println("-------------> "+idOfContactsListFound.get());
	}
	
	
	public void createContactsList() throws Exception {
		Optional<Integer> idOfContactsListFound = MailjetAdapterv3_1.getOrCreatePopulatedContactsListForOneSingleEmail("Zobi@toto.com");
		assertThat(idOfContactsListFound.isPresent());
		System.out.println("-------------> "+idOfContactsListFound.get());
	}

	public void deleteContactsList() throws Exception {
		MailjetAdapterv3_1.deleteContactsListByName("Zobi@toto.com");
		Optional<Integer> idOfContactsListFound = MailjetAdapterv3_1.getContactsListByName("Zobi@toto.com");
		assertThat(!idOfContactsListFound.isPresent());
	}
	
	@Test
	public void createACampaign() throws Exception {
		Optional<Integer> idOfCampaignFound = MailjetAdapterv3_1.createACampaignWithHtmlContent("Test de campagne subject", "Test de campagne title 4", "rmarbeck@gmail.com" , "<html><head></head><body>Hello</body><html>", "Hello");
		System.out.println("-----------> "+idOfCampaignFound.get());
		assertThat(idOfCampaignFound.isPresent());
	}


}

