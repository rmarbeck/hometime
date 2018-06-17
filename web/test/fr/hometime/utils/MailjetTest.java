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
	
	@Test
	public void testingPresenceOfAContact() throws MailjetException, MailjetSocketTimeoutException {
		Optional<Integer> idOfContactFound = MailjetAdapterv3_1.getContactByEmail("rmarbeck@gmail.com");
		assertThat(idOfContactFound.isPresent());
		assertThat(idOfContactFound.get().equals(979065101));
	}

}

