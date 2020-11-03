package fr.hometime.utils;

import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.client.AsyncHttpClient;

import models.LiveConfig;
import play.Logger;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;


/**
 * Helper for MailJet API
 * 
 * @author Raphael
 *
 */
public class MailjetSMS {
	private final static String LIVE_CONFIG_MAILJET_SMS_TOKEN = "mailjet_sms_token";
	
	private final static String LIVE_CONFIG_MAILJET_SMS_FROM_VALUE = "mailjet_sms_from_value";
	
	private final static String MAILJET_SMS_PROTOCOL = "https";
	private final static String MAILJET_SMS_HOST = "api.mailjet.com";
	private final static String MAILJET_SMS_VERSION = "v4";
	private final static String MAILJET_SMS_PORT = "443";

	private final static String MAILJET_SMS_TOKEN_HEADER_KEY = "Authorization";
	private final static String MAILJET_SMS_TOKEN_PREPEND = "Bearer ";
	private final static String MAILJET_SMS_CONTENT_TYPE_HEADER_KEY = "content-type";
	private final static String MAILJET_API_CONTENT_TYPE_PARAM_JSON_VALUE = "application/json";
	
	private final static String MAILJET_SMS_TO_KEY = "To";
	private final static String MAILJET_SMS_FROM_KEY = "From";
	private final static String MAILJET_SMS_TEXT_KEY = "Text";
	
	private static String defaultContentType = MAILJET_API_CONTENT_TYPE_PARAM_JSON_VALUE;
	private static String currentContentType = defaultContentType;

	private static String token = null;
	private static String from = null;

	private static WSClient customClient = null;
	
	private final static String API_METHOD_SMS_SEND = "sms-send";
	
	public static class MailjetSMSStatus {
		public Date sendingDate;
		public int statusCode;
		public String status = null;
		public String messageId = null;
		public int smsCount = -1;
		
		public String errorMessage = null;
		
		public MailjetSMSStatus(WSResponse response) {
			Logger.info("in MailjetSMSStatus");
			this.sendingDate = new Date();
			this.statusCode = response.getStatus();
			if (statusCode == 200) {
				JsonNode data = response.asJson();
				this.status = data.get("Status").get("Description").asText();
				this.messageId = data.get("ID").asText();
				this.smsCount = data.get("SmsCount").asInt();
			} else {
				this.errorMessage = response.asJson().get("ErrorMessage").asText();
				Logger.error("Error in SMS Sending : "+errorMessage);
				//Logger.error("Error in SMS Sending : "+response.getBody());
			}
		}
	}
	
	public static Promise<MailjetSMSStatus> sendSMS(String phoneNumber, String message) throws Exception {
		return wsSendSMS(from, phoneNumber, message);
	}
	
	public static Promise<MailjetSMSStatus> sendSMS(String sender, String phoneNumber, String message) throws Exception {
		return wsSendSMS(sender, phoneNumber, message);
	}
	
	private static Promise<MailjetSMSStatus> wsSendSMS(String sender, String phoneNumber, String message) throws Exception {
		return simplePostCallWithJsonAsOutput(API_METHOD_SMS_SEND, getBody(sender, phoneNumber, message))
			.map(response -> {
					System.out.println(response); 
					return new MailjetSMSStatus(response);
				});
	}
	
	private static String getToken() {
		return MAILJET_SMS_TOKEN_PREPEND+token;
	}
	
	private static String getBody(String sender, String phoneNumber, String message) {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put(MAILJET_SMS_FROM_KEY, sender);
		values.put(MAILJET_SMS_TO_KEY, PhoneNumberHelper.getFrenchPhoneNumberInInternationalFormat(phoneNumber));
		values.put(MAILJET_SMS_TEXT_KEY, message);
		return getBodyPayLoad(values);
	}

		
	private static Promise<WSResponse> simplePostCallWithJsonAsOutput(String method, String body) throws Exception {
		return doPostCallWithJsonAsOutput(customURL(method), body);
	}
	
	private static Promise<WSResponse> doPostCallWithJsonAsOutput(WSRequestHolder holder, String body) throws Exception {
		prepareCallWithJsonAsOutput();
		holder.setContentType(currentContentType);
		Logger.info("about to call webservices ["+holder.getUrl()+"] through a POST call");
		Logger.info("about to call webservices ["+holder.getHeaders()+"] through a POST call");
		Logger.debug("about to call webservices ["+body+"] through a POST call");
		
		Promise<WSResponse> response = holder.post(body).map(getResponse -> {
			logCallStatus(getResponse.getStatus());
			return getResponse;
		});
		
		response.onFailure(throwable -> Logger.error("call of webservice ["+holder.getUrl()+"] failed, error is : "+throwable.getMessage()));
		
		return response;
	}
	
	private static void logCallStatus(int status) {
		if (status != 200) {
			Logger.info("call is NOT successful, error code is ["+status+"]");
		} else {
			Logger.info("call is successful");
		}
		
	}
	
	private static void prepareCallWithJsonAsOutput() throws Exception {
		if (shouldAPIBeInitialized())
			initializeApi();
		setOutputModeToJSON();
	}
	

	public static void setOutputModeToJSON() {
		currentContentType = MAILJET_API_CONTENT_TYPE_PARAM_JSON_VALUE;
	}
	
	private static boolean shouldAPIBeInitialized() {
		return (token == null);
	}
	
	private static void initializeApi() throws Exception {
		if (!isEverythingConfigured())
			logErrorAndThrowException("Mailjet SMS adapter is misconfigured, at least one LiveConfig value is missing.");
		
		token = LiveConfig.getString(LIVE_CONFIG_MAILJET_SMS_TOKEN);
		from = LiveConfig.getString(LIVE_CONFIG_MAILJET_SMS_FROM_VALUE);
	}
	
	private static boolean isEverythingConfigured() {
		return (LiveConfig.isKeyDefined(LIVE_CONFIG_MAILJET_SMS_TOKEN)
				&& LiveConfig.isKeyDefined(LIVE_CONFIG_MAILJET_SMS_FROM_VALUE));
	}
	
	private static void logErrorAndThrowException(String message) throws Exception {
		Logger.error(message);
		throw new Exception(message);
	}
	
	private static WSRequestHolder buildUrl(String method) {
		return WS.url(MAILJET_SMS_PROTOCOL+"://"+MAILJET_SMS_HOST+":"+MAILJET_SMS_PORT+"/"+MAILJET_SMS_VERSION+"/"+method).setHeader(MAILJET_SMS_TOKEN_HEADER_KEY, getToken());
	}
	
	private static WSRequestHolder customURL(String method) {
		if (customClient != null) {
			AsyncHttpClient client = (AsyncHttpClient) customClient.getUnderlying();
			client.close();
		}
		com.ning.http.client.AsyncHttpClientConfig customConfig =
				new com.ning.http.client.AsyncHttpClientConfig.Builder().build();
		customClient = new play.libs.ws.ning.NingWSClient(customConfig);
		return customClient.url(MAILJET_SMS_PROTOCOL+"://"+MAILJET_SMS_HOST+":"+MAILJET_SMS_PORT+"/"+MAILJET_SMS_VERSION+"/"+method).setHeader(MAILJET_SMS_TOKEN_HEADER_KEY, getToken());
	}
	
	private static String getBodyPayLoad(HashMap<String, String> values) {
		StringBuilder content = new StringBuilder();
		content.append("{");
		for (String currentKey : values.keySet()) {
			if (content.length() != 1)
				content.append(",\n");
			content.append("\"");
			content.append(currentKey);
			content.append("\": \"");
			content.append(values.get(currentKey));
			content.append("\"");
		}
		content.append("\n}");
		Logger.info("Body : "+content.toString());
		return content.toString();
	}
	
	public static String getDefaultSender() throws Exception {
		if (shouldAPIBeInitialized())
			initializeApi();
		return from;
	}
}