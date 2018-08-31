package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Definition of an analytic information about an accounting line
 */
@Entity
@Table(name = "accounting_table")
public class AccountingLineAnalyticPresetItem extends Model implements CrudReady<AccountingLineAnalyticPresetItem, AccountingLineAnalyticPresetItem> {
	private static final long serialVersionUID = 7924622628794002310L;

	@Id
	public Long id;
	
	@ManyToOne
	public AnalyticCode analyticCode;

	public Float fixedPrice = -1f;
	
	public Float proportionalPrice = -1f;
	
	public Float fixedCost = -1f;
	
	public Float proportionalCost = -1f;
	
	public boolean oneTimeCost = false;
	
	@Column(name="order_number")
	public Integer orderNumber;
		
	@ManyToOne
	public AccountingLineAnalyticPreset accountingLineAnalyticPreset;
	
	private static AccountingLineAnalyticPresetItem singleton = null;
	
	public static AccountingLineAnalyticPresetItem of() {
    	if (singleton == null)
    		singleton = new AccountingLineAnalyticPresetItem();
    	return singleton;
    }
	
	public AccountingLineAnalyticPresetItem() {
		
	}
	
	public AccountingLineAnalyticPresetItem(AccountingLineAnalyticPreset accountingLineAnalyticPreset) {
		this();
		this.accountingLineAnalyticPreset = accountingLineAnalyticPreset;
	}
	
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,AccountingLineAnalyticPresetItem> find = new Model.Finder(String.class, AccountingLineAnalyticPresetItem.class);
    
    public static List<AccountingLineAnalyticPresetItem> findAll() {
        return find.all();
    }
    
    public static AccountingLineAnalyticPresetItem findById(Long id) {
        return find.byId(id.toString());
    }

    public static Page<AccountingLineAnalyticPresetItem> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().ilike("analyticCode", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

	@Override
	public Finder<String, AccountingLineAnalyticPresetItem> getFinder() {
		return find;
	}

	@Override
	public Page<AccountingLineAnalyticPresetItem> getPage(int page, int pageSize, String sortBy, String order,
			String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
}

