package models;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import play.Logger;
import play.db.ebean.Model;
import play.twirl.api.Html;
import play.twirl.api.Template1;
import views.html.admin.accounting.authentication;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;

/**
 * Definition of a certificate after selling a watch
 */
@Entity 
public class Authentication extends Model implements CrudReady<Authentication, Authentication> {
	private static final long serialVersionUID = -9082516370819260940L;

	private static Authentication singleton = null;
	
	@Transient
	private Template1<Authentication,Html> showTemplate = null;

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(name="document_date")
	public Date documentDate;
	
	@ManyToOne
	public CustomerWatch watch;
	
	@Lob
	public byte[] documentData;
			
	public Authentication() {
		documentDate = new Date();
	}
	
	public static Authentication of() {
    	if (singleton == null)
    		singleton = new Authentication();
    	return singleton;
    }
	
	public static Authentication of(Template1<Authentication,Html> showTemplate) {
    	of();
    	singleton.showTemplate = showTemplate;
    	return singleton;
    }
	

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Authentication> find = new Model.Finder(String.class, Authentication.class);
    
    public static List<Authentication> findAll() {
        return find.all();
    }
    
    public static Authentication findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Authentication findEagerById(Long id) {
        return find.fetch("watch").fetch("watch.customer").where().eq("id", id).findUnique();
    }
    
    public static List<Authentication> findByCustomer(models.Customer customer) {
    	return find.where().eq("watch.customer.id", customer.id)
        			.orderBy("creationDate desc").findList();
    }

    public static Page<Authentication> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("watch.customer.name", "%" + filter + "%"), Expr.ilike("watch.brand", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
    
	@Override
	public void save() {
		this.creationDate = new Date();
		checkBeforeSaving();
		super.save();
		// As we need an ID to have the documentData generated
		this.update();
	}

	@Override
	public void update() {
		checkBeforeSaving();
		super.update();
	}

	@Override
	public Model.Finder<String, Authentication> getFinder() {
		return find;
	}

	@Override
	public Page<Authentication> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private void checkBeforeSaving() {
		if (this.watch != null) {
			this.watch = CustomerWatch.findById(this.watch.id);
		}
		
		if (singleton.showTemplate != null)
			documentData = singleton.showTemplate.render(this).body().getBytes(StandardCharsets.UTF_8);
	}
		
	public String getCustomerFullName() {
		return watch.customer.getFullName();
	}
	
	public String getBrand() {
		return watch.getBrand();
	}
	
	public String getModel() {
		return watch.getModel();
	}
}

