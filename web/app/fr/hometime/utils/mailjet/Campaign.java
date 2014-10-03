package fr.hometime.utils.mailjet;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.libs.ws.WSRequestHolder;

public class Campaign {
	private final String EDITION_TYPE_FULL = "full";
	private final String EDITION_TYPE_LIGHT = "light";
	private final String EDITION_TYPE_ULIGHT = "ulight";
	private final String EDITION_MODE_TOOL = "tool";
	private final String EDITION_MODE_HTML = "html";
	private final String FOOTER_TYPE_DISPLAY = "default";
	private final String FOOTER_TYPE_HIDE = "none";
	private final String PERMALINK_TYPE_DISPLAY = "default";
	private final String PERMALINK_TYPE_HIDE = "none";
	private final String LANGUAGE_FRENCH = "fr";
	private final String LANGUAGE_ENGLISH = "en";
	
	private String subject;
	private String inAPITitle;
	private Long listId;
	private String language = LANGUAGE_FRENCH;
	private String fromEmail;
	private String fromName;
	private String replyTo;
	private String footer = FOOTER_TYPE_DISPLAY;
	private String permalink = PERMALINK_TYPE_DISPLAY;
	private String editionMode = EDITION_MODE_HTML;
	private String editionType = EDITION_TYPE_FULL;
	//NOT IMPLEMENTED YET
	//callback, sending_date, template_id, token
	
	private final String subjectAPIParam = "subject";
	private final String inAPITitleAPIParam = "title";
	private final String listIdAPIParam = "list_id";
	private final String languageAPIParam = "lang";
	private final String fromEmailAPIParam = "from";
	private final String fromNameAPIParam = "from_name";
	private final String replyToAPIParam = "reply_to";
	private final String footerAPIParam = "footer";
	private final String permalinkAPIParam = "permalink";
	private final String editionModeAPIParam = "edition_mode";
	private final String editionTypeAPIParam = "edition_type";
	
	public Campaign(String subject, String fromEmail, String fromName) {
		this(subject, fromEmail, fromName, subject, fromEmail, null);
	}
	
	public Campaign(String subject, String fromEmail, String fromName, String title) {
		this(subject, fromEmail, fromName, title, fromEmail, null);
	}

	public Campaign(String subject, String fromEmail, String fromName, String title, String replyTo) {
		this(subject, fromEmail, fromName, title, replyTo, null);
	}
	
	public Campaign(String subject, String fromEmail, String fromName, String title, String replyTo, Long listId) {
		this.subject = subject;
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.inAPITitle = title;
		this.replyTo = replyTo;
		this.listId = listId;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTitle(String title) {
		this.inAPITitle = title;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

	public void setLanguageToFrench() {
		this.language = LANGUAGE_FRENCH;
	}

	public void setLanguageToEnglish() {
		this.language = LANGUAGE_ENGLISH;
	}
	
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public void setFooterToDisplay() {
		this.footer = FOOTER_TYPE_DISPLAY;
	}

	public void setFooterToHide() {
		this.footer = FOOTER_TYPE_HIDE;
	}

	public void setPermalinkToDisplay() {
		this.permalink = PERMALINK_TYPE_DISPLAY;
	}

	public void setPermalinkToHide() {
		this.permalink = PERMALINK_TYPE_HIDE;
	}

	public void setEditionModeToHtml() {
		this.editionMode = EDITION_MODE_HTML;
	}

	public void setEditionModeToTool() {
		this.editionMode = EDITION_MODE_TOOL;
	}

	public void setEditionTypeToFull() {
		this.editionType = EDITION_TYPE_FULL;
	}

	public void setEditionTypeToLight() {
		this.editionType = EDITION_TYPE_LIGHT;
	}

	public void setEditionTypeToULight() {
		this.editionType = EDITION_TYPE_ULIGHT;
	}
	
	public void appendQueryParameters(WSRequestHolder request) {
		Map<String, String> params = getParamsAsAMap();
		
		for (String key : params.keySet()) {
			request.setQueryParameter(key, params.get(key));
		}
	}

	public ObjectNode getParametersAsJson() {
		Map<String, String> params = getParamsAsAMap();
		ObjectNode json = Json.newObject();
		
		for (String key : params.keySet())
			json.put(key, params.get(key));
		
		return json;
	}
	
	public String getParametersAsPostBody() {
		Map<String, String> params = getParamsAsAMap();
		StringBuilder body = new StringBuilder();
		String delimiter = "";
		
		for (String key : params.keySet()) {
			body.append(delimiter);
			body.append(key+"="+params.get(key));
			delimiter = "&";
		}
		
		return body.toString();
	}
		
	private Map<String, String> getParamsAsAMap() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(subjectAPIParam, subject);
		params.put(inAPITitleAPIParam, inAPITitle);
		if (listId != null)
			params.put(listIdAPIParam, listId.toString());
		params.put(languageAPIParam, language);
		params.put(fromEmailAPIParam, fromEmail);
		params.put(fromNameAPIParam, fromName);
		params.put(replyToAPIParam, replyTo);
		params.put(footerAPIParam, footer);
		params.put(permalinkAPIParam, permalink);
		params.put(editionModeAPIParam, editionMode);
		params.put(editionTypeAPIParam, editionType);

		return params;
	}
}
