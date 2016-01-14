package views;

import java.util.ArrayList;
import java.util.List;

import play.i18n.Messages;
import models.AccountingDocument;
import models.AccountingLine;

public class AccountingSheet {
	private static final String DEFAULT_KEY = "admin.accounting.document";
	private static final String KEY_MODIFIER = ".VARIABLE";

	private AccountingDocument document;
	private Line header;
	private List<Line> lines;
	private String key;
	private Float alreadyPayed;
	
	public class Line {
		private String lineCssClass;
		private List<Cell> cells;
		private AccountingLine line = null;
		private Line previousLine = null;
		
		private Line(String lineCssClass) {
			this(lineCssClass, new ArrayList<AccountingSheet.Cell>());
		}
		
		private Line(String lineCssClass, List<Cell> cells) {
			this.lineCssClass = lineCssClass;
			this.cells = cells;
		}
		
		public Line(AccountingLine line, Line previousLine, String lineCssClass) {
			this(lineCssClass);
			this.line = line;
			this.previousLine = previousLine;
			this.addDescription();
			switch (line.type) {
				case WITH_VAT_BY_UNIT:
				case WITHOUT_VAT_BY_UNIT:
					this.addUnit();
					this.addUnitPrice();
					this.addPrice();
					break;
				case FREE_INCLUDED:
				case FREE_OFFERED:
					this.addUnit();
					this.addCell(getModifiedKeyValue(line.type.toString()), getModifiedKeyValue(line.type.toString()+".class"), 0);
					this.addPrice(0F, 0);
					break;
				case FREE_SPECIAL:
					this.addUnit();
					this.addLineInfo(0);
					this.addPrice(0F, 0);
					break;
				case SUB_TOTAL:
					this.addPrice(getPreviousLinesTotal(), 3);
					this.lineCssClass = getModifiedKeyValue(getModifiedKeyValue(line.type.toString()+".class"));
					break;
				default:
					this.addLineInfo(3);
					break;
			}
		}
		
		public void addCell(Cell newCell) {
			if (cells == null)
				cells = new ArrayList<Cell>();
			this.cells.add(newCell);
		}
		
		public void addCell(String value) {
			addCell(value, "", 0);
		}
		
		public void addCell(String value, String cssClass, int colSpan) {
			addCell(new Cell(value, cssClass, colSpan));
		}
		
		public void addCell(Long value) {
			if (value == null)
				value = 0L;
			addCell(value, "", 0);
		}
		
		public void addCell(Long value, String cssClass, int colSpan) {
			addCell(new Cell(value, cssClass, colSpan));
		}
		
		public void addCell(Float value) {
			if (value == null)
				value = 0F;
			addCell(value, "", 0);
		}
		
		public void addCell(Float value, String cssClass, int colSpan) {
			addCell(new Cell(value, cssClass, colSpan));
		}
		
		public List<Cell> getCells() {
			if (cells == null)
				cells = new ArrayList<Cell>();
			return cells;
		}
		
		public String getCssClass() {
			return lineCssClass;
		}
		
		private void addDescription() {
			this.addCell(line.description, getModifiedKeyValue("description.class"), 0);
		}
		
		private void addLineInfo(int colSpan) {
			this.addCell(line.info, getModifiedKeyValue("line.info.class"), colSpan);
		}
		
		private void addUnit() {
			this.addCell(line.unit, getModifiedKeyValue("unit.class"), 0);
		}
		
		private void addUnitPrice() {
			this.addCell(line.unitPrice, getModifiedKeyValue("unit.price.class"), 0);
		}
		
		private void addPrice() {
			addPrice(getPrice(), 0);
		}
		
		private void addPrice(Float price, int colSpan) {
			this.addCell(price, getModifiedKeyValue("price.class"), colSpan);
		}
		
		private Float getPrice() {
			if (line != null && line.unitPrice != null && line.unit != null)
				return line.unitPrice*line.unit;
			return 0F;
		}
		
		private Float getPreviousLinesTotal() {
			Line currentPreviousLine = this.previousLine;
			Float subTotal = 0F;
			while(currentPreviousLine != null) {
				subTotal+= currentPreviousLine.getPrice();
				if (AccountingLine.LineType.SUB_TOTAL.equals(currentPreviousLine.line.type))
					break;
				currentPreviousLine=currentPreviousLine.previousLine;
			}
			return subTotal;
		}
	}
	
	public class Cell {
		private String cssClass = "";
		private String valueString = "";
		private Long valueLong;
		private Float valueFloat;
		private int colSpan = 0;
		private String type;
		
		private Cell(String value) {
			this.valueString = value;
		}
		
		private Cell(Long value) {
			this.valueLong = value;
		}
		
		private Cell(Float value) {
			this.valueFloat = value;
		}
		
		private Cell(String value, String cssClass, int colSpan) {
			this(value);
			this.type = "String";
			this.cssClass = cssClass;
			this.colSpan = colSpan;
		}
		
		private Cell(Long value, String cssClass, int colSpan) {
			this(value);
			this.type = "Long";
			this.cssClass = cssClass;
			this.colSpan = colSpan;
		}
		
		private Cell(Float value, String cssClass, int colSpan) {
			this(value);
			this.type = "Float";
			this.cssClass = cssClass;
			this.colSpan = colSpan;
		}
		
		public String getStringValue() {
			return valueString;
		}
		
		public Long getLongValue() {
			return valueLong;
		}
		
		public Float getFloatValue() {
			return valueFloat;
		}
		
		public String getCssClass() {
			return cssClass;
		}
		
		public int getColSpan() {
			return colSpan;
		}
		
		public String getType() {
			return type;
		}
	}
	
	private AccountingSheet(AccountingDocument document, String key, Float alreadyPayedAmount) {
		this.document = document;
		this.key = key;
		this.alreadyPayed = alreadyPayedAmount;
		this.computeLines();
	}
	
	public static AccountingSheet of(AccountingDocument document) {
		return new AccountingSheet(document, DEFAULT_KEY, 0F);
	}
	
	public static AccountingSheet of(AccountingDocument document, String key) {
		return new AccountingSheet(document, key, 0F);
	}
	
	public static AccountingSheet of(AccountingDocument document, String key, Float alreadyPayedAmount) {
		return new AccountingSheet(document, key, alreadyPayedAmount);
	}
	
	public List<Line> getLines() {
		return lines;
	}
	
	public Line getHeader() {
		return header;
	}
	
	private void computeLines() {
		lines = new ArrayList<Line>();
		addHeader();
		Line previousLine = null;
		for (AccountingLine line : document.lines)
			previousLine = addLine(line, previousLine);
		addFooter();
	}
	
	private void addHeader() {
		header = headerLineFactory();
	}
	
	private Line addLine(AccountingLine line, Line previousLine) {
		Line newLine = new Line(line, previousLine, getModifiedKeyValue("line.class")); 
		lines.add(newLine);
		return newLine;
	}
	
	private void addFooter() {
		lines.addAll(footerLinesFactory());
	}
	
	private Line headerLineFactory() {
		Line newLine = new Line(getModifiedKeyValue("header.line.class"));
		newLine.addCell(createHeaderCell("description"));
		newLine.addCell(createHeaderCell("quantity"));
		if(hasOnlyVAT()) {
			newLine.addCell(createHeaderCell("unit.price.ht"));
		} else {
			newLine.addCell(createHeaderCell("unit.price"));
		}
		newLine.addCell(createHeaderCell("total"));
		
		return newLine;
	}
	
	private List<Line> footerLinesFactory() {
		List<Line> lines = new ArrayList<Line>();
		if (hasVAT()) {
			lines.add(createTotalLine(totalHTAmount(), "ht.total"));
			lines.add(createTotalLine(vatAmount(), "vat"));
			lines.add(createTotalLine(totalAmount(), "vat.total"));
		} else {
			lines.add(createTotalLine(totalHTAmount(), "total"));
		}
		if (alreadyPayed != 0) {
			lines.add(createTotalLine(alreadyPayed, "already.payed"));
			lines.add(createTotalLine(totalToPay(), "to.pay.total"));
		}
		return lines;
	}
	
	private Cell createHeaderCell(String key) {
		return new Cell(getModifiedKeyValue("header."+key), getModifiedKeyValue("header"+key+".class"), 0);
	}
	
	private Line createTotalLine(Float value, String key) {
		Line newLine = new Line(getModifiedKeyValue(key+".line.class"));
		newLine.addCell(new Cell(getModifiedKeyValue(key+".line.description"), getModifiedKeyValue(key+".line.description.class"), 0));
		newLine.addCell(value,  getModifiedKeyValue(key+".line.value.class"), 3);
		return newLine;
	}
	
	private boolean hasVAT() {
		for (AccountingLine line : document.lines)
			if (AccountingLine.LineType.WITH_VAT_BY_UNIT.equals(line.type))
				return true;
		return false;
	}
	
	private boolean hasOnlyVAT() {
		for (AccountingLine line : document.lines)
			if (AccountingLine.LineType.WITHOUT_VAT_BY_UNIT.equals(line.type))
				return false;
		return true;
	}
	
	private Float vatAmount() {
		Float vatAmmount = 0F;
		for (AccountingLine line : document.lines)
			if (AccountingLine.LineType.WITH_VAT_BY_UNIT.equals(line.type))
				if (line.unit != null && line.unitPrice != null)
					vatAmmount+= line.unit*line.unitPrice*0.20F;
		return vatAmmount;
	}
	
	private Float totalHTAmount() {
		Float totalHTAmmount = 0F;
		for (AccountingLine line : document.lines)
			switch (line.type) {
			case WITH_VAT_BY_UNIT:
			case WITHOUT_VAT_BY_UNIT:
				if (line.unit != null && line.unitPrice != null)
					totalHTAmmount+=line.unit*line.unitPrice;
				break;
			}
		return totalHTAmmount;
	}
	
	private Float totalAmount() {
		Float totalAmmount = 0F;
		for (AccountingLine line : document.lines)
			switch (line.type) {
			case WITH_VAT_BY_UNIT:
				if (line.unit != null && line.unitPrice != null)
					totalAmmount+=line.unit*line.unitPrice*1.2F;
				break;
			case WITHOUT_VAT_BY_UNIT:
				if (line.unit != null && line.unitPrice != null)
					totalAmmount+=line.unit*line.unitPrice;
				break;
			}
		return totalAmmount;
	}
	
	private Float totalToPay() {
		return totalAmount() - alreadyPayed;
	}
	
	protected String getModifiedKeyValue(String replacement) {
		return Messages.get(getModifiedKey(replacement));
	}
	
	private String getModifiedKey(String replacement) {
		return buildKey().replaceFirst("VARIABLE", replacement).toLowerCase().replace("_", ".");
	}
	
	private String buildKey() {
		return key+KEY_MODIFIER;
	}
}
