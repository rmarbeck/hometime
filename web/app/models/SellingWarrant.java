package models;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.Logger;
import play.db.ebean.Model;
import play.twirl.api.Html;
import play.twirl.api.Template1;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;

/**
 * Definition of a certificate after selling a watch
 */
@Entity 
public class SellingWarrant extends Model implements CrudReady<SellingWarrant, SellingWarrant> {
	private static final long serialVersionUID = -8483563752415369361L;

	private static SellingWarrant singleton = null;
	
	private Template1<SellingWarrant,Html> showTemplate = null;

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(name="document_date")
	public Date documentDate;
	
	@ManyToOne
	public WatchToSell watch;
	
	@Lob
	public byte[] documentData;
			
	public SellingWarrant() {
		documentDate = new Date();
	}
	
	public static SellingWarrant of() {
    	if (singleton == null)
    		singleton = new SellingWarrant();
    	return singleton;
    }
	
	public static SellingWarrant of(Template1<SellingWarrant,Html> showTemplate) {
    	of();
    	singleton.showTemplate = showTemplate;
    	return singleton;
    }
	

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,SellingWarrant> find = new Model.Finder(String.class, SellingWarrant.class);
    
    public static List<SellingWarrant> findAll() {
        return find.all();
    }
    
    public static SellingWarrant findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<SellingWarrant> findByCustomer(models.Customer customer) {
    	return find.where().eq("watch.customerThatSellsTheWatch.id", customer.id)
        			.orderBy("creationDate desc").findList();
    }

    public static Page<SellingWarrant> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.fetch("watch").where().or(Expr.ilike("watch.customerThatSellsTheWatch.name", "%" + filter + "%"), Expr.ilike("watch.brand.display_name", "%" + filter + "%"))
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
	public Model.Finder<String, SellingWarrant> getFinder() {
		return find;
	}

	@Override
	public Page<SellingWarrant> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private void checkBeforeSaving() {
		if (this.watch != null) {
			this.watch = WatchToSell.findById(this.watch.id);
		}
		
		if (singleton.showTemplate != null)
			documentData = singleton.showTemplate.render(this).body().getBytes(StandardCharsets.UTF_8);
	}
	
	public String getCustomerFullName() {
		return watch.customerThatSellsTheWatch.getFullName();
	}
	
	public String getBrand() {
		return watch.getBrandName();
	}
	
	public String getModel() {
		return watch.getModel();
	}
}

