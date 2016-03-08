package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

/**
 * Definition of a Watch belonging to a customer
 */
@Entity 
public class CustomerWatch extends Model {
	private static final long serialVersionUID = -1122053555762637015L;
	
	public enum CustomerWatchStatus {
	    STORED_BY_WATCH_NEXT ("STORED_BY_WATCH_NEXT"),
	    STORED_BY_STH ("STORED_BY_STH"),
	    STORED_BY_BRAND ("STORED_BY_BRAND"),
	    STORED_BY_OTHER_PARTNER ("STORED_BY_OTHER_PARTNER"),
	    BACK_TO_CUSTOMER ("BACK_TO_CUSTOMER");
	    
		private String name = "";
		    
		CustomerWatchStatus(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static CustomerWatchStatus fromString(String name) {
	        for (CustomerWatchStatus status : CustomerWatchStatus.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	public String brand;
	
	public String model;

	public String additionnalModelInfos;
	
	public String reference;
	
	public String serial;
	
	public String serial2;
	
	public String movement;
	
	public String picturesUrl;
	
	public Date endOfMainWaranty;
	
	public Date endOfWaterWaranty;
	
	public Date nextService;
	
	@Column(name="next_partial_service")
	public Date nextPartialService;
	
	public Date lastStatusUpdate;
	
	@Column(length = 10000)
	public String serviceInfos;
	
	@Column(length = 10000)
	public String otherInfos;
	
	@ManyToOne
	public Customer customer;

	@Constraints.Required
	@Column(name="customer_watch_status", length = 40)
	@Enumerated(EnumType.STRING)
	public CustomerWatchStatus status;
	
	public CustomerWatch() {
		
	}
	
	public CustomerWatch(Customer customer) {
		this();
		this.customer = customer;
	}
	
	public CustomerWatch(Order order) {
		this(order.customer);
		this.brand = order.brand;
		this.model = order.model;
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,CustomerWatch> find = new Model.Finder(String.class, CustomerWatch.class);
    
    public static List<CustomerWatch> findAll() {
        return find.all();
    }
    
    public static List<CustomerWatch> findAllBySerialAsc() {
        return find.where().orderBy("serial ASC").findList();
    }
    
    public static List<String> getSerialsBySerialAsc() {
    	List<CustomerWatch> watches = findAllBySerialAsc();
    	List<String> serials = new ArrayList<String>();
    	if (watches != null) {
    		for (CustomerWatch watch : watches)
    			serials.add(watch.readSerial());
    	}
    	return serials;
    }
    
    public static List<CustomerWatch> findAllByCustomerAndBrandAsc() {
        return find.where().orderBy("customer.firstname ASC, brand ASC").findList();
    }

    
    public static List<String> getIdsByCustomersAsc() {
    	List<CustomerWatch> watches = findAllByCustomerAndBrandAsc();
    	List<String> watchesByCustomers = new ArrayList<String>();
    	if (watches != null) {
    		for (CustomerWatch watch : watches)
    			watchesByCustomers.add(watch.id.toString());
    	}
    	return watchesByCustomers;
    }
    
    private static String displayWatchByCustomer(CustomerWatch watch) {
    	StringBuilder result = new StringBuilder();
    	if (watch.customer != null) {
    		result.append(watch.customer.getFullName());
    	} else {
    		result.append("unknown");
    	}
    	result.append(" -> ");
    	result.append(watch.brand);
    	result.append(" ");
    	result.append(watch.model);
    	
    	return result.toString();
    }
    
    public static List<String> getWatchesByCustomersAsc() {
    	List<CustomerWatch> watches = findAllByCustomerAndBrandAsc();
    	List<String> watchesByCustomers = new ArrayList<String>();
    	if (watches != null) {
    		for (CustomerWatch watch : watches)
    			watchesByCustomers.add(displayWatchByCustomer(watch));
    	}
    	return watchesByCustomers;
    }
    
    
    public static CustomerWatch findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<CustomerWatch> findByCustomer(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
        			.orderBy("next_partial_service desc").findList();
    }
    
    public static CustomerWatch findBySerial(String serial) {
    	return find.where().eq("serial", serial).findUnique();
    }

    public static Page<CustomerWatch> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("model", "%" + filter + "%"), Expr.ilike("brand", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
    
    public static Page<CustomerWatch> page(int page, int pageSize, String sortBy, String order, String filter, String status) {
    	if (status == null || "".equals(status))
    		return page(page, pageSize, sortBy, order, filter);
        
    	return 
            find.where().and(Expr.or(Expr.ilike("model", "%" + filter + "%"), Expr.ilike("brand", "%" + filter + "%")), Expr.eq("customer_watch_status", status))
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
			if (previousStatusWasDifferent())
				this.lastStatusUpdate = new Date();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		super.update();
	}
	
	private boolean previousStatusWasDifferent() {
		CustomerWatch inDB = CustomerWatch.findById(this.id);
		return !inDB.status.equals(this.status);
	}
	
	public String getBrand() {
		return brand;
	}
	
	public String getMovement() {
		return movement;
	}
	
	public String getModel() {
		return model;
	}
	
	public String readSerial() {
		return serial;
	}
	
	public String getReference() {
		return reference;
	}
	
	public String getSerial2() {
		return serial2;
	}
	
	public String toString() {
		return customer.name + " -> " + brand + " " + model;
	}
}

