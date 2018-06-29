package fr.hometime.utils;

import java.util.Optional;
import java.util.function.Supplier;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Campaigndraft;
import com.mailjet.client.resource.CampaigndraftDetailcontent;
import com.mailjet.client.resource.Contact;
import com.mailjet.client.resource.Contactslist;
import com.mailjet.client.resource.ContactslistManageContact;

import akka.japi.Function;
import models.LiveConfig;
import play.Logger;


/**
 * Helper for MailJet API
 * 
 * @author Raphael
 *
 */
public class MailjetAdapterv3_1 {
	private final static String LIVE_CONFIG_MAILJET_API_KEY = "mailjet_api_key";
	private final static String LIVE_CONFIG_MAILJET_SECRET_KEY = "mailjet_secret_key";
	
	private final static String LIVE_CONFIG_MAILJET_FROM_EMAIL = "mailjet_from_email";
	private final static String LIVE_CONFIG_MAILJET_FROM_NAME = "mailjet_from_name";
	private final static String LIVE_CONFIG_MAILJET_REPLY_TO = "mailjet_reply_to";
	

	private static String apiKey = null;
	private static String apiSecretKey = null;
	private static String fromEmail = null;
	private static String fromName = null;
	private static String replyTo = null;
	
	
	private static String startOfURLForCheckingCampaigns = "https://app.mailjet.com/campaigns/summary2/";
	
	private static MailjetClient getClient() {
		if (shouldAPIBeInitialized())
			try {
				initializeApi();
			} catch (Exception e) {
				e.printStackTrace();
			}
		MailjetClient client = new MailjetClient(apiKey, apiSecretKey); 
		client.setDebug(MailjetClient.VERBOSE_DEBUG);
		return client;
	}
	
	private static boolean shouldAPIBeInitialized() {
		return (apiKey == null);
	}
	
	private static void initializeApi() throws Exception {
		if (!isEverythingConfigured())
			logErrorAndThrowException("Mailjet adapter is misconfigured, at least one LiveConfig value is missing.");
		
		apiKey = LiveConfig.getString(LIVE_CONFIG_MAILJET_API_KEY);
		apiSecretKey = LiveConfig.getString(LIVE_CONFIG_MAILJET_SECRET_KEY);
		fromEmail = LiveConfig.getString(LIVE_CONFIG_MAILJET_FROM_EMAIL);
		fromName = LiveConfig.getString(LIVE_CONFIG_MAILJET_FROM_NAME);
		replyTo = LiveConfig.isKeyDefined(LIVE_CONFIG_MAILJET_REPLY_TO)?LiveConfig.getString(LIVE_CONFIG_MAILJET_REPLY_TO):fromEmail;
	}
	
	private static boolean isEverythingConfigured() {
		return (LiveConfig.isKeyDefined(LIVE_CONFIG_MAILJET_API_KEY)
				&& LiveConfig.isKeyDefined(LIVE_CONFIG_MAILJET_SECRET_KEY)
				&& LiveConfig.isKeyDefined(LIVE_CONFIG_MAILJET_FROM_EMAIL)
				&& LiveConfig.isKeyDefined(LIVE_CONFIG_MAILJET_FROM_NAME));
	}
	
	
	/*
	 * 
	 * ----------------------------------------------
	 * 
	 */
	
	public static Optional<Integer> createContact(String email) throws MailAdapterException {
	    return getIdFromRequest(() -> new MailjetRequest(Contact.resource).property(Contact.EMAIL, email), getClient()::post);
	}
	
	public static Optional<Integer> getContactByEmail(String email) throws MailAdapterException {
	    return getIdFromRequest(() -> new MailjetRequest(Contact.resource, email), getClient()::get);
	}
	
	public static Optional<Integer> getOrCreateContactByEmail(String email) {
		return getOrCreateByKey(MailjetAdapterv3_1::getContactByEmail, MailjetAdapterv3_1::createContact, email);
	}
	
	public static Optional<Integer> createEmptyContactsListByName(String name) throws MailAdapterException {
	    return getIdFromRequest(() -> new MailjetRequest(Contactslist.resource).property(Contactslist.NAME, name), getClient()::post);
	}
	
	public static Optional<Integer> getContactsListByName(String name) throws MailAdapterException {
		return getIdFromRequest(() -> new MailjetRequest(Contactslist.resource).filter(Contactslist.NAME, name), getClient()::get);
	}
	
	public static void deleteContactsListByName(String name) throws Exception {
		Optional<Integer> foundContactsList = getContactsListByName(name);
		if (foundContactsList.isPresent())
			getClient().delete(new MailjetRequest(Contactslist.resource, foundContactsList.get()));
	}
	
	public static Optional<Integer> getOrCreatePopulatedContactsListForOneSingleEmail(String email) throws MailAdapterException {
		Optional<Integer> foundContactsList = getOrCreateEmptyContactsListForOneSingleEmail(email);
		Optional<Integer> numberOfContactsInList = retrieveNumberOfContactsInContactsList(foundContactsList.get());
		if (numberOfContactsInList.get() == 0)
			addContactToList(foundContactsList.get(), email);
		return foundContactsList;
	}
	
	public static Optional<Integer> retrieveNumberOfContactsInContactsList(Integer id) throws MailAdapterException  {
	    return getIntegerFromRequest(() -> new MailjetRequest(Contactslist.resource, id), getClient()::get, "SubscriberCount");
	}
	
	public static void addContactToList(Integer id, String email) throws MailAdapterException  {
		getResponseFromRequest(() -> new MailjetRequest(ContactslistManageContact.resource, id).property(ContactslistManageContact.EMAIL, email).property(ContactslistManageContact.ACTION, "addnoforce"), getClient()::post);
	}
	
	public static Optional<Integer> getOrCreateEmptyContactsListForOneSingleEmail(String email) throws MailAdapterException {
		return getOrCreateByKey(MailjetAdapterv3_1::getContactsListByName, MailjetAdapterv3_1::createEmptyContactsListByName, email);
	}
	
	public static Optional<Integer> getIdFromRequest(Supplier<MailjetRequest> requestSupplier, Function<MailjetRequest, MailjetResponse> action) throws MailAdapterException {
		return getIntegerFromRequest(requestSupplier, action, "ID");
	}
	
	public static Optional<Integer> getIntegerFromRequest(Supplier<MailjetRequest> requestSupplier, Function<MailjetRequest, MailjetResponse> action, String NameOfIntegerInResponse) throws MailAdapterException {
		MailjetResponse response = getResponseFromRequest(requestSupplier, action).get();
		if (response.getData().getJSONObject(0).has(NameOfIntegerInResponse))
	    	return Optional.of(response.getData().getJSONObject(0).getInt(NameOfIntegerInResponse));
	    return Optional.empty();
	}
	
	public static Optional<MailjetResponse> getResponseFromRequest(Supplier<MailjetRequest> requestSupplier, Function<MailjetRequest, MailjetResponse> action) throws MailAdapterException {
		Optional<MailjetResponse> response;
		try {
			response = Optional.of(action.apply(requestSupplier.get()));
			if (response.isPresent() && isResponseOk(response.get()))
				return response;
		} catch (Exception e) {
			throw new MailAdapterException(e);
		}
		if (response.isPresent())
			throw new MailAdapterException(response.get());
		throw new MailAdapterException(new Exception("Unknown root cause"));
	}
	
	public static Optional<Integer> getOrCreateByKey(Function<String, Optional<Integer>> getFunction, Function<String, Optional<Integer>> createFunction, String key) {
		Optional<Integer> foundAlreadyExisting;
		try {
			foundAlreadyExisting = getFunction.apply(key);
			if (foundAlreadyExisting.isPresent())
				return foundAlreadyExisting;
		} catch (Exception e) {
			try {
				return createFunction.apply(key);	
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return Optional.empty();
	}
	
	private static boolean isResponseOk(MailjetResponse response) {
		return response.getStatus() >= 200 || response.getStatus() <= 204;
	}
	
	public static Optional<Integer> createACampaignWithHtmlContent(String subject, String title, String email, String html, String text) throws MailAdapterException {
		Optional<Integer> contactsListId = getOrCreatePopulatedContactsListForOneSingleEmail(email);
		Optional<Integer> idOfDraft = getIdFromRequest(() -> new MailjetRequest(Campaigndraft.resource)
													.property(Campaigndraft.SUBJECT, subject)
													.property(Campaigndraft.TITLE, title)
													.property(Campaigndraft.EDITMODE, "html2")
													.property("ContactsListID", ""+contactsListId.get())
													.property(Campaigndraft.SENDERNAME, fromName)
													.property(Campaigndraft.SENDEREMAIL, fromEmail)
													.property(Campaigndraft.LOCALE, "fr_FR")
				, getClient()::post);
		if (idOfDraft.isPresent())
			getResponseFromRequest(() -> new MailjetRequest(CampaigndraftDetailcontent.resource, idOfDraft.get())
													.property(CampaigndraftDetailcontent.HTMLPART, html)
													.property(CampaigndraftDetailcontent.TEXTPART, text)
				, getClient()::post);
		return idOfDraft;
	}
	
	public static Optional<String> createACampaignWithHtmlContentAndGetUrlToCheckIt(String subject, String title, String email, String html, String text) throws MailAdapterException {
		Optional<Integer> idOfCampaign  = createACampaignWithHtmlContent(subject, title, email, html, text);
		if (idOfCampaign.isPresent())
			return Optional.of(buildUrlToCheckCampaign(idOfCampaign.get()));
		return Optional.empty();
	}
	
	private static String buildUrlToCheckCampaign(Integer idOfCampaign) {
		return startOfURLForCheckingCampaigns+idOfCampaign;
	}
	
	
	private static void logErrorAndThrowException(String message) throws Exception {
		Logger.error(message);
		throw new Exception(message);
	}
}