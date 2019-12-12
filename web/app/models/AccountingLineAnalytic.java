package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

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
	
	@Transient
	public String uan;
	
	
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
    	//String sql = "select t0.id c0, t0.price c1, t0.cost c2, t0.checked c3, t0.double_checked c4, t0.remark c5, t0.reserved c6, t3.id c7, t3.analytic_code c8, t3.lbl c9, t3.description c10, t3.active c11, t3.accounting_line_analytic_preset_id c12, t1.id c13, t2.id c14, t2.creation_date c15, t2.customer_id c16 from accounting_line_analytic t0 left outer join analytic_code t3 on t3.id = t0.analytic_code_id  left outer join accounting_line t1 on t1.id = t0.accounting_line_id  left outer join accounting_document t2 on t2.id = t1.document_id";
    	String sql = "select t4.unique_accounting_number uan, t0.id, t0.price, t0.cost, t0.checked, t0.double_checked, t0.remark, t0.reserved, t3.id , t3.analytic_code from accounting_line_analytic t0 left outer join analytic_code t3 on t3.id = t0.analytic_code_id  left outer join accounting_line t1 on t1.id = t0.accounting_line_id  left outer join accounting_document t2 on t2.id = t1.document_id left outer join invoice t4 on t4.id = t1.document_id";
    	RawSql raw = RawSqlBuilder.parse(sql)
    					.columnMapping("t3.id", "analyticCode.id")
    					.columnMapping("t3.analytic_code", "analyticCode.analyticCode")
    					.create();
    	return find.setRawSql(raw).findList();
        //return find.fetch("accountingLine.document").fetch("analyticCode").findList();
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

