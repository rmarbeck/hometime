package views;


import java.util.ArrayList;
import java.util.List;

import static fr.hometime.utils.ReflectionHelper.guessPossibleAccessorMethodsForAField;
import static fr.hometime.utils.ReflectionHelper.getStringValue;
import play.db.ebean.Model;
import play.twirl.api.Html;

/**
 * Definition of Model in order to be displayed
 */
public class ModelDisplay {
	private static final String MAIN_KEY = "admin.MODELNAME.FIELDNAME.display.label";
	
	public class Field {
		public String fieldName;
		public String fieldAccessorMethod;
		public String labelKey;
		public String renderingOption;
		public Html content;
		public boolean hideSmall;

		protected Field(String fieldName, String fieldAccessorMethod, boolean hideSmall, String labelKey, String renderingOption, Html content) {
			this.fieldName = fieldName;
			this.fieldAccessorMethod = fieldAccessorMethod;
			this.labelKey = labelKey;
			this.hideSmall = hideSmall;
			this.renderingOption = renderingOption;
			this.content = content;
		}
		
		protected Field(String fieldName, String fieldAccessorMethod, boolean hideSmall, String labelKey, String renderingOption) {
			this(fieldName, fieldAccessorMethod, false, labelKey, renderingOption, null);
		}

		protected Field(String fieldName, String fieldAccessorMethod, boolean hideSmall, String labelKey) {
			this(fieldName, fieldAccessorMethod, false, labelKey, null);
		}
		
		protected Field(String fieldName, String fieldAccessorMethod, boolean hideSmall) {
			this(fieldName, fieldAccessorMethod, false, null);
		}
		
		protected Field(String fieldName, String fieldAccessorMethod) {
			this(fieldName, fieldAccessorMethod, false);
		}
		
		protected Field(String fieldName) {
			this(fieldName, "", false);
		}
		
		public String display() {
			return ModelDisplay.display(model, fieldAccessorMethod, fieldName);
		}
		
		public String getFieldName() {
			return fieldName;
		}
		
		public String getRenderingOption() {
			return renderingOption;
		}
		
		public String getKey() {
			if (labelKey == null || labelKey.equals(""))
				return MAIN_KEY.replaceFirst("MODELNAME", getModelName().toLowerCase()).replaceFirst("FIELDNAME", getFieldName().toLowerCase()).replace("_", ".");
			return labelKey;
		}
	}
	
	private Model model;
	private List<Field> fields;
	public String labelKey;
	
	public ModelDisplay(Model model) {
		this(model, "admin");
	}
	
	public ModelDisplay(Model model, String labelKey) {
		this.model = model;
		this.labelKey = labelKey;
	}
	
	public void addField(String fieldName, String fieldAccessorMethod, boolean hideSmall, String labelKey, String renderingOption, Html content) {
		if (fields == null)
			fields = new ArrayList<ModelDisplay.Field>();
		fields.add(new Field(fieldName, fieldAccessorMethod, hideSmall, labelKey, renderingOption, content));
	}
	
	public void addField(String fieldName, String fieldAccessorMethod, boolean hideSmall, String labelKey, String renderingOption) {
		addField(fieldName, fieldAccessorMethod, hideSmall, labelKey, renderingOption, null);
	}
	
	public void addField(String fieldName, String fieldAccessorMethod, boolean hideSmall, String labelKey) {
		addField(fieldName, fieldAccessorMethod, hideSmall, labelKey, null);
	}
	
	public void addField(String fieldName, String fieldAccessorMethod, boolean hideSmall) {
		addField(fieldName, fieldAccessorMethod, hideSmall, null);
	}
	
	public void addField(String fieldName, String fieldAccessorMethod) {
		addField(fieldName, fieldAccessorMethod, false, null);
	}
	
	public void addField(String fieldName) {
		addField(fieldName, null, false, null);
	}
	
	public void addSeparator() {
		addField("-separator-", null, false, null, "-separator-");
	}
	
	public void addTitle(String titleKey) {
		addField(titleKey, null, false, labelKey+"."+titleKey, "-title-");
	}
	
	public void addHtml(Html content) {
		addField("-html-", null, false, null, "-html-", content);
	}
	
	public Model getModel() {
		return model;
	}
	
	protected String getModelName() {
		return model.getClass().getSimpleName();
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public static String display(Object object, String fieldAccessorMethod, String fieldName) {
		if (object == null)
			return "null";
		
		List<String> possibleAccessorMethods = new ArrayList<String>();
		
		if (fieldAccessorMethod == null || "".equals(fieldAccessorMethod)) {
			possibleAccessorMethods = guessPossibleAccessorMethodsForAField(fieldName);
		} else {
			possibleAccessorMethods.add(fieldAccessorMethod);
		}
		
		
		return getStringValue(object, possibleAccessorMethods).orElse("null");
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

}
