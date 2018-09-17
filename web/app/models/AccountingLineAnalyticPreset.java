package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.data.validation.Constraints;
import com.avaje.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;

import controllers.CrudReady;

/**
 * Definition of an analytic information about an accounting line
 */
@Entity 
public class AccountingLineAnalyticPreset extends Model implements CrudReady<AccountingLineAnalyticPreset, AccountingLineAnalyticPreset> {
	private static final long serialVersionUID = -6542614203999532229L;

	@Id
	public Long id;
	
	@Constraints.Required
	@Column(unique=true)
	public Long metaAnalyticCode = 0L;

	public String description;
	
	public String remark;
	
	public boolean fullyFilled = false;
	
	public boolean active = true;
		
	@OneToMany(mappedBy="accountingLineAnalyticPreset", cascade = CascadeType.ALL)
	public List<AccountingLineAnalyticPresetItem> accountingLineAnalyticItems;
	
	
	private static AccountingLineAnalyticPreset singleton = null;
	
	public static AccountingLineAnalyticPreset of() {
    	if (singleton == null)
    		singleton = new AccountingLineAnalyticPreset();
    	return singleton;
    }
	
	public AccountingLineAnalyticPreset() {
	}
	
	public AccountingLineAnalyticPreset(Long metaAnalyticCode) {
		this.metaAnalyticCode = metaAnalyticCode;
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,AccountingLineAnalyticPreset> find = new Model.Finder(String.class, AccountingLineAnalyticPreset.class);
    
    public static List<AccountingLineAnalyticPreset> findAll() {
        return find.all();
    }
    
    public static AccountingLineAnalyticPreset findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static AccountingLineAnalyticPreset findByMetaCode(Long metaCode) {
        return find.where().eq("metaAnalyticCode", metaCode).findUnique();
    }
    
    
    public static List<String> getIdsByDescriptionAsc() {
    	List<String> ids = new ArrayList<String>();
    	for (AccountingLineAnalyticPreset preset : findAllByDescriptionAsc())
    		ids.add(preset.id.toString());
    	return ids;
    }
    
    public static List<String> getDescriptionsByDescriptionAsc() {
    	List<String> descriptions = new ArrayList<String>();
    	for (AccountingLineAnalyticPreset preset : findAllByDescriptionAsc())
    		descriptions.add(preset.description.toString());
    	return descriptions;
    }
    
    public static List<String> getDescriptionsWithCodeByDescriptionAsc() {
    	List<String> descriptions = new ArrayList<String>();
    	for (AccountingLineAnalyticPreset preset : findAllByDescriptionAsc())
    		descriptions.add(preset.metaAnalyticCode + " - " + preset.description.toString());
    	return descriptions;
    }
    
    public static List<AccountingLineAnalyticPreset> findAllByDescriptionAsc() {
    	return find.order("description ASC").findList();
    }

    public static PagedList<AccountingLineAnalyticPreset> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("cast (metaAnalyticCode as varchar)", "%" + filter + "%"), Expr.ilike("description", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagedList(page, pageSize);
                
    }
    
	@Override
	public Finder<String, AccountingLineAnalyticPreset> getFinder() {
		return find;
	}

	@Override
	public PagedList<AccountingLineAnalyticPreset> getPage(int page, int pageSize, String sortBy, String order,
			String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	
	public String toString() {
		return metaAnalyticCode.toString();
	}
}

