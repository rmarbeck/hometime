package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import fr.hometime.utils.UniqueAccountingNumber;

/**
 * Definition of an invoice
 */
@Entity 
public class Invoice extends Model {
	private static final long serialVersionUID = -6697685682606242671L;
	
	public enum InvoiceType {
	    VAT ("VAT"),
	    MARGIN_VAT ("MARGIN_VAT"),
	    NO_VAT ("NO_VAT"),
	    MIXED_VAT ("MIXED_VAT"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2");
	    
		private String name = "";
		    
		InvoiceType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static InvoiceType fromString(String name) {
	        for (InvoiceType type : InvoiceType.values()) {
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
	
	@OneToOne
	public AccountingDocument document;
	
	@Column(length = 10000)
	public String paymentConditions;
	
	@Column(length = 10000)
	public String supportedPaymentMethods;
	
	@Column(length = 10000)
	public String paymentMethodUsed;
	
	public float alreadyPayed = 0;
	
	@Constraints.Required
	@Column(name="unique_accounting_number", unique=true)
	public String uniqueAccountingNumber;
	
	@Column(name="invoice_type")
	public InvoiceType type;
	
	@Column(name="from_date")
	public Date fromDate;
	
	@Column(name="to_date")
	public Date toDate;
	
	public Invoice() {
		this.document = new AccountingDocument();
	}
	
	public Invoice(Customer customer) {
		this.document = new AccountingDocument(customer);
	}
	
	public InvoiceType getType() {
		return type;
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Invoice> find = new Model.Finder(String.class, Invoice.class);
    
    public static List<Invoice> findAll() {
        return find.all();
    }
    
    public static Invoice findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<Invoice> findByCustomer(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
        			.orderBy("next_partial_service desc").findList();
    }

    public static Page<Invoice> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.like("document.customer", "%" + filter + "%"), Expr.like("document.customer", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

	@Override
	public void save() {
		this.document.save();
		super.save();
	}

	@Override
	public void update() {
		this.document.update();
		super.update();
	}

	public String getUniqueAccountingNumber() {
		return uniqueAccountingNumber;
	}
	
	public void setUniqueAccountingNumber(String uan) {
		this.uniqueAccountingNumber = uan;
	}
}

