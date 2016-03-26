package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import models.AccountingLine.LineType;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

/**
 * Definition of a selling document
 */
@Entity 
public class SellingDocument extends Model {
	private static final long serialVersionUID = -5067043690233573324L;
	
	@Id
	public Long id;
	
	@Column(length = 10000)
	public String description;
	
	@OneToOne
	public AccountingDocument document;
	
	@Column(length = 10000)
	public String paymentMethodUsed;
	
	@Constraints.Required
	@Column(name="unique_accounting_number", unique=true)
	public String uniqueAccountingNumber;

	@Column(name="purchase_date")
	public Date date;
	
	public SellingDocument() {
		this.document = new AccountingDocument();
	}
	
	public SellingDocument(Customer customer) {
		this.document = new AccountingDocument(customer);
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,SellingDocument> find = new Model.Finder(String.class, SellingDocument.class);
    
    public static List<SellingDocument> findAll() {
        return find.all();
    }
    
    public static SellingDocument findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static SellingDocument findByAccountingDocument(AccountingDocument document) {
    	return find.where().eq("document.id", document.id).findUnique();
    }
    
    public static List<SellingDocument> findByCustomer(models.Customer customer) {
    	return find.where().eq("document.customer.id", customer.id).findList();
    }

    public static Page<SellingDocument> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
        	find.where().or(Expr.ilike("uniqueAccountingNumber", "%" + filter + "%"), Expr.ilike("document.customer.name", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
    
    public void  addLine(LineType type, String description, Long unit, Float unitPrice, int order) {
    	this.document.addLine(type, description, unit, unitPrice);
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

