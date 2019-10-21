package views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import controllers.CrudHelper;
import fr.hometime.utils.FormHelper;
import fr.hometime.utils.FormHelper.*;
import play.data.Form;
import play.data.Form.Field;
import scala.Symbol;
import scala.Tuple2;

/**
 * Definition of Form in order to be displayed
 */
@SuppressWarnings("unchecked")
public class FormDisplay {
	private static final String MAIN_KEY = "admin.MODELNAME";
	private static final String ACTION_KEY = MAIN_KEY+".action";
	private static final String FORM_FIELD_KEY = MAIN_KEY+".form.FIELD.VARIABLE.name";
	
	private static final String SELECT_TYPE = "select";
	
	public class FormField {
		private Form<?> form;
		public String fieldName;
		public String fieldKey;
		public String type;
		public List<?> possibleKeys;
		public List<?> possibleValues;
		public List<Tuple2<Symbol, String>> options;
		public boolean hideSmall;
		
		protected FormField(Form<?> form, String fieldName, String fieldKey, boolean hideSmall, List<?> possibleKeys, List<?> possibleValues, Tuple2<Symbol, String>... tuple) {
			this(form, fieldName, fieldKey, hideSmall, SELECT_TYPE);
			this.options = new ArrayList<Tuple2<Symbol,String>>(Arrays.asList(tuple));
			this.possibleKeys = possibleKeys;
			this.possibleValues = possibleValues;
		}
		
		protected FormField(Form<?> form, String fieldName, String fieldKey, boolean hideSmall, List<?> possibleValues, Tuple2<Symbol, String>... tuple) {
			this(form, fieldName, fieldKey, hideSmall, possibleValues, possibleValues, tuple);
		}
		
		protected FormField(Form<?> form, String fieldName, String fieldKey, boolean hideSmall, String type, Tuple2<Symbol, String>... tuple) {
			this(form, fieldName, fieldKey, hideSmall, type);
			this.options = new ArrayList<Tuple2<Symbol,String>>(Arrays.asList(tuple));
		}
		
		protected FormField(Form<?> form, String fieldName, String fieldKey, boolean hideSmall, String type) {
			this.form = form;
			this.fieldName = fieldName;
			this.fieldKey = fieldKey;
			this.hideSmall = hideSmall;
			this.type = type;
			this.options = new ArrayList<Tuple2<Symbol,String>>();
			this.possibleKeys = new ArrayList();
			this.possibleValues = new ArrayList();
		}

		protected FormField(Form<?> form, String fieldName, String fieldKey, boolean hideSmall) {
			this(form, fieldName, fieldKey, false, null);
		}
		
		protected FormField(Form<?> form, String fieldName, String fieldKey) {
			this(form, fieldName, fieldKey, false);
		}
		
		public Field getField() {
			return form.field(this.fieldName);
		}
		
		public String getType() {
			return type;
		}
		
		public String getName() {
			return fieldName;
		}
		
		public List<?> getPossibleKeys() {
			return possibleKeys;
		}
		
		public List<?> getPossibleValues() {
			return possibleValues;
		}
		
		public String getKey() {
			if (fieldKey == null || fieldKey.equals(""))
				return FORM_FIELD_KEY.replaceFirst("FIELD", getName().toLowerCase()).replaceFirst("MODELNAME", getModelName().toLowerCase()).replace("_", ".");
			return fieldKey;
		}
	}
	
	private Form<?> form;
	private Optional<String> underneathModel;
	private List<FormField> formFields;
	public String labelKey;
	
	public FormDisplay(Form<?> form) {
		this(form, null);
	}
	
	public FormDisplay(Form<?> form, String underneathModel) {
		this(form, underneathModel, "admin");
	}
	
	public FormDisplay(Form<?> form, String underneathModel, String labelKey) {
		this.form = form;
		this.underneathModel = Optional.ofNullable(underneathModel);
		this.labelKey = labelKey;
	}

	public void addFormField(String fieldName, String fieldKey, boolean hideSmall, String type, Tuple2<Symbol, String>... tuple) {
		if (formFields == null)
			formFields = new ArrayList<FormDisplay.FormField>();
		formFields.add(new FormField(form, fieldName, fieldKey, hideSmall, type, tuple));
	}
	
	public void addFormField(String fieldName, boolean hideSmall, String type, Tuple2<Symbol, String>... tuple) {
		if (formFields == null)
			formFields = new ArrayList<FormDisplay.FormField>();
		formFields.add(new FormField(form, fieldName, null, hideSmall, type, tuple));
	}

	public void addFormField(String fieldName, String type, Tuple2<Symbol, String>... tuple) {
		addFormField(fieldName, null, false, type, tuple);
	}
	
	public void addFormFieldForSelect(String fieldName, boolean hideSmall, List<?> possibleKeys, List<?> possibleValues, Tuple2<Symbol, String>... tuple) {
		if (formFields == null)
			formFields = new ArrayList<FormDisplay.FormField>();
		formFields.add(new FormField(form, fieldName, null, hideSmall, possibleKeys, possibleValues, tuple));
	}
	
	public void addFormFieldForSelect(String fieldName, boolean hideSmall, FormHelper.KeysAndValues possibleKeysAndValues, Tuple2<Symbol, String>... tuple) {
		if (formFields == null)
			formFields = new ArrayList<FormDisplay.FormField>();
		formFields.add(new FormField(form, fieldName, null, hideSmall, possibleKeysAndValues.getKeys(), possibleKeysAndValues.getValues(), tuple));
	}
	
	public void addFormFieldForSelect(String fieldName, boolean hideSmall, List<?> possibleValues, Tuple2<Symbol, String>... tuple) {
		if (formFields == null)
			formFields = new ArrayList<FormDisplay.FormField>();
		formFields.add(new FormField(form, fieldName, null, hideSmall, possibleValues, tuple));
	}
	
	public void addFormFieldForSelect(String fieldName, List<?> possibleValues, Tuple2<Symbol, String>... tuple) {
		addFormFieldForSelect(fieldName, false, possibleValues, tuple);
	}
	
	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
	
	public String getLabelKey() {
		return this.labelKey;
	}
	
	public Form<?> getForm() {
		return form;
	}
	
	public List<FormField> getFormFields() {
		return formFields;
	}
	
	public String getActionKey() {
		return ACTION_KEY.replaceFirst("MODELNAME", getModelName().toLowerCase());
	}
	
	public String getModelName() {
		return underneathModel.orElse(guessModelName());
	}
	
	public String getControllerName() {
		return CrudHelper.guessControllerNameByModelName(underneathModel.orElse(guessModelName())).get();
	}
	
	private String guessModelName() {
		if (form.value().isEmpty())
			return "ERROR";
		String innerClassName = form.get().getClass().getSimpleName();
		return innerClassName;
	}
}
