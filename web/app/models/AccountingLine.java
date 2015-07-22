package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

/**
 * Definition of a Line in accounting document
 */
@Entity 
public class AccountingLine extends Model {
	private static final long serialVersionUID = -160414590466772915L;

	public enum LineType {
	    WITH_VAT_BY_UNIT ("WITH_VAT_BY_UNIT"),
	    WITHOUT_VAT_BY_UNIT ("WITHOUT_VAT_BY_UNIT"),
	    FREE_INCLUDED ("FREE_INCLUDED"),
	    FREE_OFFERED ("FREE_OFFERED"),
	    FREE_SPECIAL ("FREE_SPECIAL"),
	    INFO_LINE ("INFO_LINE");
	    
		private String name = "";
		    
		LineType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static LineType fromString(String name) {
	        for (LineType type : LineType.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@Column(length = 10000)
	public String description;
	
	public Long unit;

	public Float unitPrice;
	
	@Column(length = 10000)
	public String info;
	
	@ManyToOne
	public AccountingDocument document;

	@Constraints.Required
	@Column(name="line_type", length = 40)
	@Enumerated(EnumType.STRING)
	public LineType type = LineType.WITH_VAT_BY_UNIT;
	
	public AccountingLine() {
		
	}
	
	public AccountingLine(AccountingDocument document) {
		this();
		this.document = document;
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,AccountingLine> find = new Model.Finder(String.class, AccountingLine.class);
    
    public static List<AccountingLine> findAll() {
        return find.all();
    }
    
    public static AccountingLine findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<AccountingLine> findByCustomer(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
        			.orderBy("next_partial_service desc").findList();
    }

    public static Page<AccountingLine> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("model", "%" + filter + "%"), Expr.ilike("brand", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
}

