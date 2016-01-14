package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.Logger;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Model;
import play.i18n.Messages;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

/**
 * Definition of a Customer
 */
@Entity 
public class Customer extends Model {
	private static final long serialVersionUID = -7161230172856280985L;
	
	public enum CustomerStatus {
	    PROSPECT ("PROSPECT"),
	    ALMOST_CUSTOMER ("ALMOST_CUSTOMER"),
	    REAL_CUSTOMER ("REAL_CUSTOMER");
	    
		private String name = "";
		    
		CustomerStatus(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static CustomerStatus fromString(String name) {
	        for (CustomerStatus status : CustomerStatus.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum CustomerCivility {
	    MONSIEUR ("MONSIEUR"),
	    MADAME ("MADAME"),
	    MISTER ("MISTER"),
	    MISS ("MISS");
	    
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

	@Id
	public Long id;

	@Constraints.Required
	@Column(unique=true)
	public String email;

	public String alternativeEmail;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	public Date lastCommunicationDate;
	
	public String phoneNumber;
	
	public String alternativePhoneNumber;
	
	public String firstname;
	
	public String name;

	@Column(length = 10000)
	public String billingAddress;
	
	@Column(length = 10000)
	public String pickupAddress;
	
	@Column(length = 10000)
	public String returnAddress;
	
	@Column(length = 10000)
	public String sharedInfos;
	
	@Column(length = 10000)
	public String privateInfos;
	
	@Constraints.Required
	@Column(name="customer_status", length = 40)
	@Enumerated(EnumType.STRING)
	public CustomerStatus status;
	
	@Constraints.Required
	@Column(name="customer_civility", length = 40)
	@Enumerated(EnumType.STRING)
	public CustomerCivility civility = CustomerCivility.MONSIEUR;
	
	public Long value = 0L;
	
	public Long potentiality = 0L;
	
	@Column(name="is_topic_open")
	public Boolean isTopicOpen = false;
	
	@OneToMany(mappedBy="customer", cascade = CascadeType.ALL)
	public List<Order> orders;

	@OneToMany(mappedBy="customer", cascade = CascadeType.ALL)
	public List<CustomerWatch> watches;
	
	public Customer() {
	}
	
	private Customer(String email) {
		this.email = email;
		this.creationDate = new Date();
		this.status = CustomerStatus.PROSPECT;
		this.civility = CustomerCivility.MONSIEUR;
	}
	
	private Customer(OrderRequest request) {
		this(request.email);
		this.phoneNumber = request.phoneNumber;
		this.name = request.nameOfCustomer;
		this.billingAddress = request.city;
		this.sharedInfos = request.remark;
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Customer> find = new Model.Finder(String.class, Customer.class);
    
    public static List<Customer> findAll() {
        return find.all();
    }
    
    public static List<Customer> findAllByAscId() {
        return find.orderBy("id ASC").findList();
    }

    public static Customer findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Customer findByEmail(String email) {
    	return find.where().eq("email", email).findUnique();
    }
    
    public static Customer findByPhoneNumber(String phoneNumber) {
    	return find.where().eq("phoneNumber", phoneNumber).findUnique();
    }
    
    public static List<Customer> findByName(String name) {
    	return find.where().eq("name", name).findList();
    }
    
    public static List<Customer> findWithOpenTopic() {
    	return find.where().eq("is_topic_open", true).findList();
    }
    
    public static Customer getOrCreateCustomerFromOrderRequest(OrderRequest request) {
    	Customer existingCustomer = findByEmail(request.email);
    	if (existingCustomer == null)
    		return new Customer(request);
    	return existingCustomer;
    }
    
    public static Customer getOrCreateCustomerForEmail(String email) {
    	Customer existingCustomer = findByEmail(email);
    	if (existingCustomer == null)
    		return new Customer(email);
    	return existingCustomer;
    }
    
    public static Page<Customer> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().or(Expr.ilike("email", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }

	@Override
	public void save() {
		this.creationDate = new Date();
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
    	if (isTopicOpen == null)
    		isTopicOpen = false;
        if (lastCommunicationDate != null) {
        	Logger.info("Date received : "+lastCommunicationDate.toString());
        }
        return errors.isEmpty() ? null : errors;
    }
    
    public String toString() {
    	return email;
    }
}

