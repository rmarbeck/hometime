package models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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

/**
 * Definition of a formal order document
 */
@Entity 
public class OrderDocument extends Model {
	private static final long serialVersionUID = 110270838160216086L;

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
	public String detailedInfos;
	
	@Column(length = 10000)
	public String delay;
	
	@Constraints.Required
	@Column(unique=true)
	public String uniqueAccountingNumber;

	@Column(name="invoice_type")
	public Invoice.InvoiceType type;
	
	@Column(name="valid_until_date")
	public Date validUntilDate;
	
	public OrderDocument() {
		this.document = new AccountingDocument();
		this.validUntilDate = new Date(Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli());
	}
	
	public OrderDocument(Customer customer) {
		this.document = new AccountingDocument(customer);
	}
	
	public Invoice.InvoiceType getType() {
		return type;
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,OrderDocument> find = new Model.Finder(String.class, OrderDocument.class);
    
    public static List<OrderDocument> findAll() {
        return find.all();
    }
    
    public static OrderDocument findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<OrderDocument> findByCustomer(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
        			.orderBy("next_partial_service desc").findList();
    }

    public static Page<OrderDocument> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("document.customer", "%" + filter + "%"), Expr.ilike("document.customer", "%" + filter + "%"))
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
}

