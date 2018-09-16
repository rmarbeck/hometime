package views;

import static fr.hometime.utils.ReflectionHelper.getStringValue;
import static fr.hometime.utils.ReflectionHelper.getValue;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.PagedList;

/**
 * Definition of Page in order to be displayed
 */
public class PageDisplay<T> {
	public class TableField {
		public String fieldAccessorMethod;
		public String dbField;
		public String labelKey;
		public String renderingOption;
		public boolean hideSmall;
		
		protected TableField(String dbField, String labelKey, String fieldAccessorMethod, boolean hideSmall, String renderingOption) {
			this.dbField = dbField;
			this.fieldAccessorMethod = fieldAccessorMethod;
			this.labelKey = labelKey;
			this.hideSmall = hideSmall;
			this.renderingOption = renderingOption;
		}

		protected TableField(String dbField, String labelKey, String fieldAccessorMethod, boolean hideSmall) {
			this(dbField, labelKey, fieldAccessorMethod, false, null);
		}
		
		protected TableField(String dbField, String labelKey, String fieldAccessorMethod) {
			this(dbField, labelKey, fieldAccessorMethod, false);
		}
		
	}
	
	private PagedList<T> page;
	private List<TableField> tableFields;
	public String defaultSortingField;
	public String filterByLabel;
	public String labelKey;
	public String classGenerator;
	
	public PageDisplay(PagedList<T> page) {
		this(page, "to define");
	}
	
	public PageDisplay(PagedList<T> page, String filterByLabel) {
		this(page, filterByLabel, "admin");
	}
	
	public PageDisplay(PagedList<T> page, String filterByLabel, String labelKey) {
		this.page = page;
		this.filterByLabel = filterByLabel;
		this.labelKey = labelKey;
	}
	
	public void addTableField(String dbField, String labelKey, String fieldAccessorMethod, boolean hideSmall, String renderingOption) {
		if (tableFields == null) {
			tableFields = new ArrayList<PageDisplay<T>.TableField>();
			defaultSortingField = dbField;
		}
		tableFields.add(new TableField(dbField, labelKey, fieldAccessorMethod, hideSmall, renderingOption));
	}
	
	public void addTableField(String dbField, String labelKey, String fieldAccessorMethod, boolean hideSmall) {
		addTableField(dbField, labelKey, fieldAccessorMethod, hideSmall, null);
	}
	
	public void setClass(String classGenerator) {
		this.classGenerator = classGenerator;
	}
	
	public boolean hasClass() {
		return classGenerator != null;
	}
	
	public void setDefaultSortingField(String sortingField) {
		this.defaultSortingField = sortingField;
	}
	
	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
	
	public String getLabelKey() {
		return this.labelKey;
	}
	
	public PagedList<T> getPage() {
		return page;
	}
	
	public List<TableField> getTableFields() {
		return tableFields;
	}
	
	public Long getIdByIndex(int index) {
		Object value = getObjectByIndex(index, "getId");
		if (value instanceof Long)
			return (Long) value;

		return Long.valueOf(-1);
	}
	
	public String getClassByIndex(int index) {
		Object value = getObjectByIndex(index, classGenerator);
		if (value instanceof String)
			return (String) value;

		return "";
	}
	
	private Object getObjectByIndex(int index, String method) {
		T line = page.getList().get(index);
		return getValue(line, method).get();
	}
	
	public String display(Object object, String fieldAccessorMethod) {
		if (object == null)
			return "null";
		
		return getStringValue(object, fieldAccessorMethod).orElse("null");
	}

}
