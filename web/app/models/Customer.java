package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import models.Order.OrderStatus;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import play.db.ebean.Model;

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
		
		public int intValue() {
			return Integer.valueOf(name);
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

	@Id
	public Long id;

	@Column(unique=true)
	public String email;

	public String alternativeEmail;
	
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
	public String returnAdress;
	
	@Column(length = 10000)
	public String sharedInfos;
	
	@Column(length = 10000)
	public String privateInfos;
	
	@Column(name="customer_status", length = 40)
	@Enumerated(EnumType.STRING)
	public CustomerStatus status;
	
	public Long value = 0L;
	
	public Long potentiality = 0L;
	
	@OneToMany(mappedBy="customer", cascade = CascadeType.ALL)
	public List<Order> orders;
	
	private Customer(String email) {
		this.email = email;
		this.creationDate = new Date();
		this.status = CustomerStatus.PROSPECT;
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
    
    public static Customer getOrCreateCurstomerForOrderRequest(OrderRequest request) {
    	Customer existingCustomer = findByEmail(request.email);
    	if (existingCustomer == null)
    		return new Customer(request);
    	return existingCustomer;
    }
    
    public static Customer getOrCreateCurstomerForEmail(String email) {
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
}

