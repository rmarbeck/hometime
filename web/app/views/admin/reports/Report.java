package views.admin.reports;

import static fr.hometime.utils.ReflectionHelper.getStringValue;
import static fr.hometime.utils.ReflectionHelper.getValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Definition of Report in order to be displayed
 */
public class Report<T> {
	public class Col {
		public String fieldAccessorMethod;
		public Optional<String> labelKey;
		public Optional<String> renderingOption;
		public Optional<String> cssClass;
		public boolean hideSmall;
		
		protected Col(String labelKey, String fieldAccessorMethod, boolean hideSmall, String renderingOption, String cssClass) {
			this.fieldAccessorMethod = fieldAccessorMethod;
			this.labelKey = Optional.ofNullable(labelKey);
			this.hideSmall = hideSmall;
			this.cssClass = Optional.ofNullable(cssClass);
			this.renderingOption = Optional.ofNullable(renderingOption);
		}

		protected Col(String labelKey, String fieldAccessorMethod, boolean hideSmall) {
			this(labelKey, fieldAccessorMethod, false, null, null);
		}
		
		protected Col(String labelKey, String fieldAccessorMethod) {
			this(labelKey, fieldAccessorMethod, false);
		}
		
		public String getKey() {
			return this.labelKey.orElse("undefined");
		}
		
		public String getCssClass() {
			return this.cssClass.orElse("");
		}
		
		public String getRenderingOption() {
			return this.renderingOption.orElse("");
		}
		
		public String getFieldAccessorMethod() {
			return fieldAccessorMethod;
		}
		
		public boolean shouldHideSmall() {
			return hideSmall;
		}
	}
	
	private Optional<List<T>> lines;
	private List<Col> cols;
	public Optional<String> labelKey;
	public String classGenerator;
	
	public Report(List<T> lines) {
		this(lines, null);
	}
	
	public Report(List<T> lines, String key) {
		this.lines = Optional.ofNullable(lines);
		this.labelKey = Optional.ofNullable(key);
	}
	
	public void addCol(String key, String fieldAccessorMethod, boolean hideSmall, String renderingOption, String cssClass) {
		if (cols == null) {
			cols = new ArrayList<Report<T>.Col>();
		}
		cols.add(new Col(key, fieldAccessorMethod, hideSmall, renderingOption, cssClass));
	}
	
	public void addCol(String key, String fieldAccessorMethod, boolean hideSmall) {
		addCol(key, fieldAccessorMethod, hideSmall, null, null);
	}
	
	public void setClass(String classGenerator) {
		this.classGenerator = classGenerator;
	}
	
	public boolean hasClass() {
		return classGenerator != null;
	}
	
	public void setKey(String labelKey) {
		this.labelKey = Optional.ofNullable(labelKey);
	}
	
	public String getKey() {
		return this.labelKey.orElse("undefined");
	}
	
	public List<T> getLines() {
		return lines.get();
	}
	
	public List<Col> getCols() {
		return cols;
	}
	
	public List<Col> getHeaders() {
		return getCols();
	}
	
	public String getClassByIndex(int index) {
		Object value = getObjectByIndex(index, classGenerator);
		if (value instanceof String)
			return (String) value;

		return "";
	}
	
	private Object getObjectByIndex(int index, String method) {
		T line = lines.get().get(index);
		return getValue(line, method).get();
	}
	
	public String display(Object object, String fieldAccessorMethod) {
		if (object == null)
			return "null";
		
		return getStringValue(object, fieldAccessorMethod).orElse("null");
	}
	
	public boolean isEmpty() {
		return lines.orElse(new ArrayList<T>()).size() == 0;
	}

}
