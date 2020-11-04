package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import models.Customer.CustomerCivility;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Definition of a Quick Service Watch pre registering
 */
@Entity 
public class QuickServiceWatch extends Model implements CrudReady<QuickServiceWatch, QuickServiceWatch> {
	private static final long serialVersionUID = -1122053555762637015L;
	private static QuickServiceWatch singleton = null;
	
	public static QuickServiceWatch of() {
    	if (singleton == null)
    		singleton = new QuickServiceWatch();
    	return singleton;
    }
	
	@Id
	public Long id;
	
	
	public enum CustomerCivility {
	    MONSIEUR ("MONSIEUR"),
	    MADAME ("MADAME"),
	    MISTER ("MISTER"),
	    MISS ("MISS"),
	    EMPTY ("EMPTY");
	    
		private String name = "";
		    
		CustomerCivility(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static CustomerCivility fromString(String name) {
	        for (CustomerCivility civility : CustomerCivility.values()) {
	            if (civility.name.equals(name)) {
	                return civility;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public String customerPhoneNumber;
	@Column(name="customer_civility", length = 40)
	@Enumerated(EnumType.STRING)
	public CustomerCivility civility = CustomerCivility.MONSIEUR;
	
	public String customerName;
	public String customerFirstName;
	public String customerEmail;
	public String customerAddress;
	public String customerInfo;
	
	public String brand;
	public String model;
	public String reference;
	public String serial;
	public String toDo;
	public String remarks;
	public String privateInfos;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	public QuickServiceWatch() {
		this.creationDate = new Date();
	}
		
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,QuickServiceWatch> find = new Model.Finder(String.class, QuickServiceWatch.class);
    
    public static List<QuickServiceWatch> findAll() {
        return find.all();
    }
    
    public static QuickServiceWatch findById(Long id) {
        return find.byId(id.toString());
    }
    
	@Override
	public void save() {
		this.creationDate = new Date();
		super.save();
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public Finder<String, QuickServiceWatch> getFinder() {
		return find;
	}

    public static Page<QuickServiceWatch> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("model", "%" + filter + "%"), Expr.ilike("brand", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
	
	@Override
	public Page<QuickServiceWatch> getPage(int page, int pageSize, String sortBy, String order, String filter) {
		// TODO Auto-generated method stub
		return page(page, pageSize, sortBy, order, filter);
	}	
}

