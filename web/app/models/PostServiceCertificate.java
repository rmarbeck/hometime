package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.Logger;
import com.avaje.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;

import controllers.CrudReady;

/**
 * Definition of a certificate after a service
 */
@Entity 
public class PostServiceCertificate extends Model implements CrudReady<PostServiceCertificate, PostServiceCertificate> {
	private static final long serialVersionUID = -2761549515686171737L;

	private static PostServiceCertificate singleton = null;

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(name="document_date")
	public Date documentDate;
	
	@ManyToOne
	public Customer owner;
	
	@ManyToOne
	public CustomerWatch watch;
	
	@Column(length = 10000)
	public String workDone;
	
	@Column(length = 10000)
	public String privateInfos;
	
	public String mechanicalTestResult;
	
	public String quartzTestResult;
	
	public String waterproofingTestResult;
	
	public Boolean waterproofWaranted;
	
	@Column(name="waterproof_waranty_date")
	public Date waterproofWarantyDate;
	
	public Boolean workingWaranted;
	
	@Column(name="working_waranty_date")
	public Date workingWarantyDate;
	
	public int nextServiceRecommendedYear;
	
	public int nextWaterproofingRecommendedYear;
	
	@Column(length = 10000)
	public String usageTips;
	
	public Boolean displayQuickDateTip;
	public Boolean displayWaterLeakTip;
	public Boolean displayLowWaterResistanceTip;
	public Boolean displayWindingTip;
	public Boolean displayScrewingDownCrownTip;
	public Boolean displayPushingCrownTip;
	public Boolean displayClosingCrownTip;
	public Boolean displayReadManualTip;
		
	public PostServiceCertificate() {
		documentDate = new Date();
	}
	
	public static PostServiceCertificate of() {
    	if (singleton == null)
    		singleton = new PostServiceCertificate();
    	return singleton;
    }

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,PostServiceCertificate> find = new Model.Finder(String.class, PostServiceCertificate.class);
    
    public static List<PostServiceCertificate> findAll() {
        return find.all();
    }
    
    public static PostServiceCertificate findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<PostServiceCertificate> findByCustomer(models.Customer customer) {
    	return find.where().eq("owner.id", customer.id)
        			.orderBy("creationDate desc").findList();
    }

    public static Page<PostServiceCertificate> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.fetch("watch").where().or(Expr.ilike("owner.name", "%" + filter + "%"), Expr.ilike("watch.brand", "%" + filter + "%"))
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
	public Model.Finder<String, PostServiceCertificate> getFinder() {
		return find;
	}

	@Override
	public Page<PostServiceCertificate> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private void checkBeforeSaving() {
		if (this.watch != null) {
			this.watch = CustomerWatch.findById(this.watch.id);
			Long customerId = this.watch.customer.id;
			this.owner = Customer.findById(customerId);
		}
	}
	
	public String getCustomerFullName() {
		return owner.getFullName();
	}
	
	public String getBrand() {
		return watch.getBrand();
	}
	
	public String getModel() {
		return watch.getModel();
	}
}

