package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.Logger;
import play.db.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;

/**
 * Definition of a certificate after selling a watch
 */
@Entity 
public class PostSellingCertificate extends Model implements CrudReady<PostSellingCertificate, PostSellingCertificate> {
	private static final long serialVersionUID = 267435145706481776L;

	private static PostSellingCertificate singleton = null;

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(name="document_date")
	public Date documentDate;
	
	@ManyToOne
	public Customer owner;
	
	@ManyToOne
	public WatchToSell watch;
	
	@Column(length = 10000)
	public String privateInfos;
	
	public String testResult;
	
	public Boolean waterproofWaranted;
	
	@Column(name="waterproof_waranty_date")
	public Date waterproofWarantyDate;
	
	public Boolean brandWaranted;
	
	@Column(name="brand_waranty_date")
	public Date brandWarantyDate;
	
	public Boolean sellerWaranted;
	
	@Column(name="seller_waranty_date")
	public Date sellerWarantyDate;
	
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
		
	public PostSellingCertificate() {
		documentDate = new Date();
	}
	
	public static PostSellingCertificate of() {
    	if (singleton == null)
    		singleton = new PostSellingCertificate();
    	return singleton;
    }

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,PostSellingCertificate> find = new Model.Finder(String.class, PostSellingCertificate.class);
    
    public static List<PostSellingCertificate> findAll() {
        return find.all();
    }
    
    public static PostSellingCertificate findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<PostSellingCertificate> findByCustomer(models.Customer customer) {
    	return find.where().eq("owner.id", customer.id)
        			.orderBy("creationDate desc").findList();
    }

    public static Page<PostSellingCertificate> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.fetch("watch").where().or(Expr.ilike("owner.name", "%" + filter + "%"), Expr.ilike("watch.brand.display_name", "%" + filter + "%"))
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
	public Model.Finder<String, PostSellingCertificate> getFinder() {
		return find;
	}

	@Override
	public Page<PostSellingCertificate> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private void checkBeforeSaving() {
		if (this.watch != null) {
			this.watch = WatchToSell.findById(this.watch.id);
			Long customerId = this.watch.customerThatBoughtTheWatch.id;
			this.owner = Customer.findById(customerId);
		}
	}
	
	public String getCustomerFullName() {
		return owner.getFullName();
	}
	
	public String getBrand() {
		return watch.getBrandName();
	}
	
	public String getModel() {
		return watch.getModel();
	}
}

