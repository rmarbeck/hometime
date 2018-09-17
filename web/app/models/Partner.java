package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.Logger;
import play.data.validation.ValidationError;
import com.avaje.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;

import controllers.CrudReady;

/**
 * Definition of a Partner
 */
@Entity 
public class Partner extends Model implements CrudReady<Partner, Partner> {
	private static final long serialVersionUID = 7683920524305106015L;
	private static Partner singleton = null;

	@Id
	public Long id;

	public String email;

	public String alternativeEmail;

	public String phoneNumber;
	
	public String alternativePhoneNumber;
	
	public String name;
	
	public String internalName;
	
	@Column(length = 10000)
	public String address;
	
	@Column(length = 10000)
	public String privateInfos;

	@OneToMany(mappedBy="partner", cascade = CascadeType.ALL)
	public List<CustomerWatch> watches;
	
	@OneToMany(mappedBy="partner", cascade = CascadeType.ALL)
	public List<User> users;
	
	public boolean active = true;
	
	public static Partner of() {
    	if (singleton == null)
    		singleton = new Partner();
    	return singleton;
    }
	
	public Partner() {
	}
	
	private Partner(String email) {
		this.email = email;
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Partner> find = new Model.Finder(String.class, Partner.class);
    
    public static List<Partner> findAll() {
        return find.all();
    }
    
    public static List<Partner> findAllByAscId() {
        return find.orderBy("id ASC").findList();
    }
    
    public static List<Partner> findAllByDescId() {
        return find.orderBy("id DESC").findList();
    }
    
    public static List<Partner> findAllByEmailAsc() {
        return find.orderBy("email ASC").findList();
    }

    public static Partner findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Partner findByEmail(String email) {
    	return find.where().eq("email", email).findUnique();
    }
    
    public static Partner findByPhoneNumber(String phoneNumber) {
    	return find.where().eq("phoneNumber", phoneNumber).findUnique();
    }
    
    public static Partner findByInternalName(String internalName) {
    	return find.where().eq("internalName", internalName).findUnique();
    }
    
    public static List<Partner> findByName(String name) {
    	return find.where().eq("name", name).findList();
    }
    
    public static List<Partner> findByNameAsc() {
    	return find.where().orderBy("name ASC").findList();
    }
    
    public static List<String> getEmailsByNameAsc() {
    	List<String> emails = new ArrayList<String>();
    	for (Partner c : findByNameAsc())
    		emails.add(c.email);
    	return emails;
    }
    
    public static PagedList<Partner> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().or(Expr.ilike("email", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
	            .orderBy(sortBy + " " + order)
	            .findPagedList(page, pageSize);
	            
    }

	@Override
	public void save() {
		super.save();
	}


	@Override
	public void update() {
		try {
			Logger.debug("Updating "+this.getClass().getName());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		super.update();
	}
	
    public List<ValidationError> validate() {
    	List<ValidationError> errors = new ArrayList<ValidationError>();
        return errors.isEmpty() ? null : errors;
    }
    
    public String toString() {
    	return email;
    }

	@Override
	public Finder<String, Partner> getFinder() {
		return find;
	}

	@Override
	public PagedList<Partner> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
}

