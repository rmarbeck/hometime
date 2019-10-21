package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Page;

import controllers.CrudReady;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * Definition of an analytic information about an accounting line
 */
@SuppressWarnings("unused")
@Entity 
public class AccountingLineAnalytic extends Model implements CrudReady<AccountingLineAnalytic, AccountingLineAnalytic> {
	private static final long serialVersionUID = 160414590466772458L;

	@Id
	public Long id;
	
	@ManyToOne
	public AnalyticCode analyticCode;

	public Float price = -1f;
	
	public Float cost = -1f;
	
	public boolean checked = false;
	
	public boolean doubleChecked = false;
	
	@Column(length = 10000)
	public String remark;
	
	@Column(length = 10000)
	public String reserved;
	
	@ManyToOne
	public AccountingLine accountingLine;
	
	
	private static AccountingLineAnalytic singleton = null;
	
	public static AccountingLineAnalytic of() {
    	if (singleton == null)
    		singleton = new AccountingLineAnalytic();
    	return singleton;
    }
	
	public AccountingLineAnalytic() {
		
	}
	
	public AccountingLineAnalytic(AccountingLine documentLine) {
		this();
		this.accountingLine = documentLine;
	}
	
	public AccountingLineAnalytic(AccountingLine documentLine, AnalyticCode analyticCode) {
		this(documentLine, analyticCode, -1f);
	}
	
	public AccountingLineAnalytic(AccountingLine documentLine, AnalyticCode analyticCode, Float price) {
		this(documentLine);
		this.analyticCode = analyticCode;
		this.price = price;
		this.cost = 0f;
	}
	
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,AccountingLineAnalytic> find = new Model.Finder(String.class, AccountingLineAnalytic.class);
    
    public static List<AccountingLineAnalytic> findAll() {
        return find.all();
    }
    
    public static List<AccountingLineAnalytic> findAllForReporting() {
        return find.fetch("accountingLine.document").fetch("analyticCode").findList();
    }
    
    public static AccountingLineAnalytic findById(Long id) {
        return find.byId(id.toString());
    }

    public static Page<AccountingLineAnalytic> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().ilike("cast (analyticCode.analyticCode as varchar)", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
    
	@Override
	public Finder<String, AccountingLineAnalytic> getFinder() {
		return find;
	}

	@Override
	public Page<AccountingLineAnalytic> getPage(int page, int pageSize, String sortBy, String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
}

