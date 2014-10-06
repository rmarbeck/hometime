package fr.hometime.utils;

import java.net.URLEncoder;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import models.LiveConfig;
import play.Logger;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import fr.hometime.utils.mailjet.Campaign;


/**
 * Helper for MailJet API
 * 
 * @author Raphael
 *
 */
public class MailjetAdapter {
	private final static String LIVE_CONFIG_MAILJET_API_KEY = "mailjet_api_key";
	private final static String LIVE_CONFIG_MAILJET_SECRET_KEY = "mailjet_secret_key";
	
	private final static String LIVE_CONFIG_MAILJET_FROM_EMAIL = "mailjet_from_email";
	private final static String LIVE_CONFIG_MAILJET_FROM_NAME = "mailjet_from_name";
	private final static String LIVE_CONFIG_MAILJET_REPLY_TO = "mailjet_reply_to";
	
	private final static String MAILJET_API_PROTOCOL = "https";
	private final static String MAILJET_API_HOST = "api.mailjet.com";
	private final static String MAILJET_API_VERSION = "0.1";
	private final static String MAILJET_API_PORT = "443";

	private final static String MAILJET_API_OUTPUT_PARAM = "output";
	private final static String MAILJET_API_OUTPUT_PARAM_JSON_VALUE = "json";
	private final static String MAILJET_API_OUTPUT_PARAM_XML_VALUE = "xml";
	
	private static String defaultOutput = MAILJET_API_OUTPUT_PARAM_XML_VALUE;
	private static String currentOutput = defaultOutput;

	private static String apiKey = null;
	private static String apiSecretKey = null;
	private static String fromEmail = null;
	private static String fromName = null;
	private static String replyTo = null;
	
	private static String API_METHOD_MESSAGE_CREATE_CAMPAIGN = "messageCreatecampaign";
	private static String API_METHOD_MESSAGE_CREATE_CAMPAIGN_RESULT_CAMPAIGN = "campaign";
	private static String API_METHOD_MESSAGE_CREATE_CAMPAIGN_RESULT_CAMPAIGN_ID = "id";
	private static String API_METHOD_MESSAGE_CREATE_CAMPAIGN_RESULT_CAMPAIGN_URL = "url";
	private static String API_METHOD_MESSAGE_SET_HTML_CAMPAIGN = "messageSethtmlcampaign";
	private static String API_METHOD_MESSAGE_LIST = "messageList";
	private static String API_METHOD_MESSAGE_DUPLICATE_CAMPAIGN = "messageDuplicatecampaign";
	private static String API_METHOD_CONTACT_INFOS = "contactInfos";
	private static String API_METHOD_CONTACT_INFOS_PARAM_EMAIL = "contact";
	private static String API_METHOD_CONTACT_INFOS_RESULT_LISTS = "lists";
	private static String API_METHOD_CONTACT_INFOS_RESULT_LIST_ID = "list_id";
	private static String API_METHOD_LISTS_ADD_CONTACT = "listsAddcontact";
	private static String API_METHOD_LISTS_ADD_CONTACT_PARAM_LIST_ID = "id";
	private static String API_METHOD_LISTS_ADD_CONTACT_PARAM_EMAIL = "contact";
	private static String API_METHOD_LISTS_CREATE = "listsCreate";
	private static String API_METHOD_LISTS_CREATE_PARAM_LABEL = "label";
	private static String API_METHOD_LISTS_CREATE_PARAM_NAME = "name";
	private static String API_METHOD_LISTS_CREATE_RESPONSE_STATUS = "status";
	private static String API_METHOD_LISTS_CREATE_RESPONSE_LIST_ID = "list_id";
	
	public static Promise<String> wsCreateACampaignWithHtmlContent(String subject, String title, String email, String html, String text) throws Exception {
		return wsCreateACampaign(subject, title, email)
			.map(response -> {
				JsonNode rootNode = response.asJson();
				final String url = extractUrlFromCreateCampaign(rootNode);
				setHtmlToCampaign(extractCampaignIdFromCreateCampaign(rootNode),
														URLEncoder.encode(html, "UTF-8"), 
														URLEncoder.encode(text, "UTF-8"));
				return url;
				});
	}
	
	private static String extractCampaignIdFromCreateCampaign(JsonNode root) {
		return Long.toString(root.get(API_METHOD_MESSAGE_CREATE_CAMPAIGN_RESULT_CAMPAIGN)
								.get(API_METHOD_MESSAGE_CREATE_CAMPAIGN_RESULT_CAMPAIGN_ID).longValue());
	}
	
	private static String extractUrlFromCreateCampaign(JsonNode root) {
		return root.get(API_METHOD_MESSAGE_CREATE_CAMPAIGN_RESULT_CAMPAIGN)
								.get(API_METHOD_MESSAGE_CREATE_CAMPAIGN_RESULT_CAMPAIGN_URL).textValue();
	}
	
	public static Promise<WSResponse> wsCreateACampaign(String subject, String title, String email) throws Exception {
		return wsGetFirstListIdOfContactOrCreateIfNeeded(email)
				.flatMap(listId -> simplePostCallWithJsonAsOutput(API_METHOD_MESSAGE_CREATE_CAMPAIGN,
																	getPostParametersForCreateCampaign(subject, title, listId)));
	}
	
	private static String getPostParametersForCreateCampaign(String subject, String title, String listId) {
		return new Campaign(subject, fromEmail, fromName, title, fromEmail, Long.parseLong(listId)).getParametersAsPostBody();
	}
	
	private static Promise<String> wsCreateMailingList(final String email) throws Exception {
		return wsCreateMailingList(email, generateMailingListName(email), email);
	}

	private static Promise<String> wsGetFirstListIdOfContactOrCreateIfNeeded(final String email) throws Exception {
		Promise<String> listIdValue = wsGetContactInfos(email)
				.flatMap(response -> {
					String listId = extractFirstListIdFromContactInfos(response);
					if (listId != null)
						return Promise.pure(listId);
					return wsCreateMailingList(email);
				});
		
		return listIdValue;
	}

	private static Promise<WSResponse> wsGetContactInfos(String email) throws Exception {
		// As it seems that there is a bug in managing this call that may return an error
		// We use a custom call, trying not to use the same pool of client
		return simpleGetCallWithJsonAsOutputWithDedicatedClient(API_METHOD_CONTACT_INFOS, getContactInfosQueryString(email));
	}

	private static String extractFirstListIdFromContactInfos(WSResponse response) {
		try {
			Logger.debug("extracting first listId from response");
			JsonNode lists = response.asJson().get(API_METHOD_CONTACT_INFOS_RESULT_LISTS);
			Iterator<JsonNode> elements = lists.elements();
			if (elements.hasNext()) { 
				Logger.debug("it contains elements");
				return elements.next().get(API_METHOD_CONTACT_INFOS_RESULT_LIST_ID).textValue();
			} else {
				Logger.debug("it contains no element");
				return null;
			}
		} catch (Exception e) {
			Logger.debug("error : it contains no element");
			response.asByteArray();
			return null;
		}
	}
	
	private static String generateMailingListName(String email) {
		return Long.toString(Instant.now().getEpochSecond());
	}
	
	private static Promise<String> wsCreateMailingList(String label, String name, String email) throws Exception {
		return simplePostCallWithJsonAsOutput(API_METHOD_LISTS_CREATE, getListCreateQueryString(label, name))
			.map(response -> {
					final String listId = extractListIdFromListsCreateCall(response); 
					simplePostCallWithJsonAsOutput(API_METHOD_LISTS_ADD_CONTACT, getListsAddContactQueryString(listId, email));
					return listId;
				});
	}
	
	private static String getContactInfosQueryString(String email) {
		return getQueryString(build(API_METHOD_CONTACT_INFOS_PARAM_EMAIL, email));
	}
	
	private static String getListCreateQueryString(String label, String name) {
		return getQueryString(build(API_METHOD_LISTS_CREATE_PARAM_LABEL, label,
									API_METHOD_LISTS_CREATE_PARAM_NAME, name));
	}
	
	private static String getListsAddContactQueryString(String listId, String email) {
		return getQueryString(build(API_METHOD_LISTS_ADD_CONTACT_PARAM_LIST_ID, listId,
									API_METHOD_LISTS_ADD_CONTACT_PARAM_EMAIL, email));
	}
		
	private static String extractListIdFromListsCreateCall(WSResponse response) {
		return Long.toString(response.asJson().get(API_METHOD_LISTS_CREATE_RESPONSE_LIST_ID).asLong());
	}
	
	private static String extractTopLevelString(JsonNode root, String key) {
		return root.get(key).textValue();
	}
	
	public static Promise<WSResponse> setHtmlToCampaign(String campaignId, String html, String text) throws Exception {
		return simplePostCallWithJsonAsOutput(API_METHOD_MESSAGE_SET_HTML_CAMPAIGN,
												"id="+campaignId+"&html="+html+"&text="+text
												);
	}
	
	public static Promise<WSResponse> listEmails() throws Exception {
		return simpleGetCallWithJsonAsOutput(API_METHOD_MESSAGE_LIST);
	}
	
	private static Promise<WSResponse> simpleGetCallWithJsonAsOutput(String method) throws Exception {
		return simpleGetCallWithJsonAsOutput(method, null);
	}
	
	private static Promise<WSResponse> simpleGetCallWithJsonAsOutput(String method, String queryString) throws Exception {
		return doGetCallWithJsonAsOutput(buildUrl(method), queryString);
	}
	
	private static Promise<WSResponse> simpleGetCallWithJsonAsOutputWithDedicatedClient(String method, String queryString) throws Exception {
		return doGetCallWithJsonAsOutput(customURL(method), queryString);
	}
	
	private static Promise<WSResponse> doGetCallWithJsonAsOutput(WSRequestHolder holder, String queryString) throws Exception {
		prepareCallWithJsonAsOutput();
		Logger.info("about to call webservices ["+holder.getUrl()+"] through a GET call");
		Promise<WSResponse> response = holder.setQueryString(queryString).get().map(getResponse -> {
			logCallStatus(getResponse.getStatus());
			return getResponse;
		});
		
		response.onFailure(throwable -> Logger.error("call of webservice ["+holder.getUrl()+"] failed, error is : "+throwable.getMessage()));
		
		return response;
	}
	
	private static Promise<WSResponse> simplePostCallWithJsonAsOutput(String method, String body) throws Exception {
		return doPostCallWithJsonAsOutput(buildUrl(method), body);
	}
	
	private static Promise<WSResponse> doPostCallWithJsonAsOutput(WSRequestHolder holder, String body) throws Exception {
		prepareCallWithJsonAsOutput();
		Logger.info("about to call webservices ["+holder.getUrl()+"] through a POST call");
		Promise<WSResponse> response = holder.setContentType("application/x-www-form-urlencoded").post(body).map(getResponse -> {
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
	

	public static void setOutputModeToXML() {
		currentOutput = MAILJET_API_OUTPUT_PARAM_XML_VALUE;
	}
	
	public static void setOutputModeToJSON() {
		currentOutput = MAILJET_API_OUTPUT_PARAM_JSON_VALUE;
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
	
	private static void logErrorAndThrowException(String message) throws Exception {
		Logger.error(message);
		throw new Exception(message);
	}
	
	private static WSRequestHolder buildUrl(String method) {
		return WS.url(MAILJET_API_PROTOCOL+"://"+MAILJET_API_HOST+":"+MAILJET_API_PORT+"/"+MAILJET_API_VERSION+"/"+method).setAuth(apiKey, apiSecretKey).setQueryParameter(MAILJET_API_OUTPUT_PARAM, currentOutput);
	}
	
	private static WSRequestHolder customURL(String method) {
		com.ning.http.client.AsyncHttpClientConfig customConfig =
			    new com.ning.http.client.AsyncHttpClientConfig.Builder().build();
			WSClient customClient = new play.libs.ws.ning.NingWSClient(customConfig);

			return customClient.url(MAILJET_API_PROTOCOL+"://"+MAILJET_API_HOST+":"+MAILJET_API_PORT+"/"+MAILJET_API_VERSION+"/"+method).setAuth(apiKey, apiSecretKey).setQueryParameter(MAILJET_API_OUTPUT_PARAM, currentOutput);
	}
	
	private static String getQueryString(Map<String, String> parameters) {
		StringBuilder body = new StringBuilder();
		String delimiter = "";
		
		for (String key : parameters.keySet()) {
			body.append(delimiter);
			body.append(key+"="+parameters.get(key));
			delimiter = "&";
		}
		
		return body.toString();
	}
	
	public static HashMap<String, String> build(String... data){
	    HashMap<String, String> result = new HashMap<String, String>();

	    if(data.length % 2 != 0) 
	        throw new IllegalArgumentException("Odd number of arguments");      

	    String key = null;
	    Integer step = -1;

	    for(String value : data){
	        step++;
	        switch(step % 2){
	        case 0: 
	            if(value == null)
	                throw new IllegalArgumentException("Null key value"); 
	            key = value;
	            continue;
	        case 1:             
	            result.put(key, value);
	            break;
	        }
	    }

	    return result;
	}
}