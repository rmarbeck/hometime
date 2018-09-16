package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.Logger;
import play.data.validation.Constraints;
import com.avaje.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;

import controllers.CrudReady;

/**
 * Definition of a payment
 */
@Entity
public class Payment extends Model implements CrudReady<Payment, Payment> {

	private static Payment singleton = null;

	public enum PaymentMethod {
	    CB ("CB"),
	    WIRETRANSFER ("WIRETRANSFER"),
	    CHECK ("CHECK"),
	    CASH ("CASH"),
	    PAYPAL ("PAYPAL"),
	    TRUSTEDCHECKOUT ("TRUSTEDCHECKOUT"),
	    OTHER1 ("OTHER1"),
	    OTHER2 ("OTHER2");
	    
		private String name = "";
		    
		PaymentMethod(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static PaymentMethod fromString(String name) {
	        for (PaymentMethod status : PaymentMethod.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(name="paymentDate")
	public Date paymentDate;
	
	@Column(name="in_bank_date")
	public Date inBankDate;
	
	@Column(name="amount_in_euros")
	public float amountInEuros;
	
	@Column(length = 255)
	public String description;
	
	@Column(length = 255)
	public String description2;
	
	@Constraints.Required
	@Enumerated(EnumType.STRING)
	@Column(name="payment_method")
	public PaymentMethod paymentMethod;
	
	@ManyToOne
	public Invoice invoice;
	
	public Payment() {
		
	}
	
	public static Payment of() {
    	if (singleton == null)
    		singleton = new Payment();
    	return singleton;
    }

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Payment> find = new Model.Finder(String.class, Payment.class);
    
    public static List<Payment> findAll() {
        return find.all();
    }
    
    public static Payment findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<Payment> findAllByInBankDateAsc() {
    	return findAllByInBankDate("ASC");
    }
    
    public static List<Payment> findAllByInBankDateDesc() {
        return findAllByInBankDate("DESC");
    }
    
    public static List<Payment> findAllByInBankDate(String orderDirection) {
        return find.where().orderBy("inBankDate "+orderDirection).findList();
    }
    
    public static Page<Payment> page(int page, int pageSize, String sortBy, String order, String filter) {
    	if (sortBy == null || sortBy.equals("")) {
    		sortBy = "paymentDate";
    		order = "DESC";
    	}
        return 
            find.where().or(Expr.ilike("description", "%" + filter + "%"), Expr.ilike("invoice.uniqueAccountingNumber", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

    
	@Override
	public void save() {
		this.creationDate = new Date();
		checkBeforeSaving();
		super.save();
	}

	@Override
	public void update() {
		checkBeforeSaving();
		super.update();
	}

	@Override
	public Model.Finder<String, Payment> getFinder() {
		return find;
	}

	@Override
	public Page<Payment> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private void checkBeforeSaving() {
		Logger.debug("INVOICE -> "+invoice+" and invoice.id="+invoice.id);
		if (this.invoice != null)
			this.invoice = Invoice.findById(this.invoice.id);
	}
	
	public String getCustomerFullName() {
		if (invoice != null)
			return invoice.document.customer.getFullName();
		return "";
	}
	
	public String getInvoiceUAN() {
		if (invoice != null)
			return invoice.uniqueAccountingNumber;
		return "";
	}
}

