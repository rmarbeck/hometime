package fr.hometime.utils;

import java.util.List;
import java.util.stream.Collectors;

import models.LiveConfig;
import play.Logger;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.SmtpApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

public class SendInBlueAdapter {
	private final static String API_KEY = "api-key";
	private final static String PARTNER_KEY = "partner-key";
	private final static String LIVE_CONFIG_API_KEY = "sendinblue_api_key";
	private final static String LIVE_CONFIG_PARTNER_KEY = "sendinblue_partner_key";
	
	private static String apiKey = null;
	private static String partnerKey = null;
	
	public static void sendSimpleEmail(String title, List<String> emails, String fromName, String fromEmail, String html, String text) throws MailAdapterException {
		try {
			sendSmtpEmail(title, emails, fromName, fromEmail, html, text);
		} catch(ApiException e) {
			throw new MailAdapterException(e);
		}
	}
	
	private static void sendSmtpEmail(String title, List<String> emails, String fromName, String fromEmail, String htmlContent, String textContent) throws ApiException {
		prepareClient();
		SmtpApi apiInstance = new SmtpApi();
		SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
		try {
			sendSmtpEmail.setHtmlContent(htmlContent);
			sendSmtpEmail.setSubject(title);
			sendSmtpEmail.setTextContent(textContent);
			sendSmtpEmail.setSender(new SendSmtpEmailSender().email(fromEmail).name(fromName));
			sendSmtpEmail.setTo(getEmailsTo(emails));
		    CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);
		    Logger.info(result.toString());
		} catch (ApiException e) {
		    Logger.error("Exception when calling SmtpApi#sendTransacEmail");
		    e.printStackTrace();
		    throw e;
		}
	}
	
	private static List<SendSmtpEmailTo> getEmailsTo(List<String> emails) {
		return emails.stream().map(email -> new SendSmtpEmailTo().email(email)).collect(Collectors.toList());
	}
	
	private static boolean shouldAPIBeInitialized() {
		return (apiKey == null);
	}
	
	private static void prepareClient() {
		if (shouldAPIBeInitialized())
			try {
				initializeApi();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private static void initializeApi() throws Exception {
		if (!isEverythingConfigured())
			logErrorAndThrowException("SendInBlue adapter is misconfigured, at least one LiveConfig value is missing.");
		
		apiKey = LiveConfig.getString(LIVE_CONFIG_API_KEY);
		partnerKey = LiveConfig.getString(LIVE_CONFIG_PARTNER_KEY);
		
		ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication(API_KEY);
        //apiKeyAuth.setApiKey(apiKey);
        apiKeyAuth.setApiKey("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");
        ApiKeyAuth partnerKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication(PARTNER_KEY);
        //partnerKeyAuth.setApiKey(partnerKey);
        partnerKeyAuth.setApiKey("9GUYB8NnpWP2SX46");
	}
	
	private static boolean isEverythingConfigured() {
		return (LiveConfig.isKeyDefined(LIVE_CONFIG_API_KEY)
				&& LiveConfig.isKeyDefined(LIVE_CONFIG_PARTNER_KEY));
	}
	
	private static void logErrorAndThrowException(String message) throws Exception {
		Logger.error(message);
		throw new Exception(message);
	}
}
