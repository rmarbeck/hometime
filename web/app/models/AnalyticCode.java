package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Page;

import controllers.CrudReady;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * Definition of an analytic information about an accounting line
 */
@SuppressWarnings("unused")
@Entity 
public class AnalyticCode extends Model implements CrudReady<AnalyticCode, AnalyticCode> {
	private static final long serialVersionUID = 403537072207335098L;

	@Id
	public Long id;

	@Constraints.Required
	@Column(unique=true)
	public Long analyticCode = 0L;

	public String lbl;
	
	public String description;
	
	public boolean active = true;
		
	@ManyToOne
	public AccountingLineAnalyticPreset accountingLineAnalyticPreset;
	
	public AnalyticCode() {
		
	}
	
	private static AnalyticCode singleton = null;
	
	public static AnalyticCode of() {
    	if (singleton == null)
    		singleton = new AnalyticCode();
    	return singleton;
    }
	
	
	public AnalyticCode(AccountingLineAnalyticPreset accountingLineAnalyticPreset) {
		this();
		this.accountingLineAnalyticPreset = accountingLineAnalyticPreset;
	}
	
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,AnalyticCode> find = new Model.Finder(String.class, AnalyticCode.class);
    
    public static List<AnalyticCode> findAll() {
        return find.all();
    }
    
    public static AnalyticCode findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<String> getIdsByDescriptionAsc() {
    	List<String> ids = new ArrayList<String>();
    	for (AnalyticCode code : findAllByDescriptionAsc())
    		ids.add(code.id.toString());
    	return ids;
    }
    
    public static List<String> getDescriptionsByDescriptionAsc() {
    	List<String> descriptions = new ArrayList<String>();
    	for (AnalyticCode code : findAllByDescriptionAsc())
    		descriptions.add(code.description.toString());
    	return descriptions;
    	
    }
    
    public static List<String> getDescriptionsWithCodeByDescriptionAsc() {
    	List<String> descriptions = new ArrayList<String>();
    	for (AnalyticCode code : findAllByDescriptionAsc())
    		descriptions.add(code.analyticCode + " - " + code.description.toString());
    	return descriptions;
    }
    
    public static List<AnalyticCode> findAllByDescriptionAsc() {
    	return find.order("description ASC").findList();
    }

    public static Page<AnalyticCode> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().disjunction().ilike("cast (analyticCode as varchar)", "%" + filter + "%").ilike("lbl", "%" + filter + "%").ilike("description", "%" + filter + "%")
            	.endJunction()
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }


	@Override
	public Finder<String, AnalyticCode> getFinder() {
		return find;
	}


	@Override
	public Page<AnalyticCode> getPage(int page, int pageSize, String sortBy, String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	public String toString() {
		return analyticCode.toString();
	}
}

