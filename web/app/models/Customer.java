package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import play.db.ebean.Model.Finder;
import play.i18n.Messages;
import play.mvc.Http.Session;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import fr.hometime.utils.CustomerWatchHelper;
import fr.hometime.utils.PartnerAndCustomerHelper;
import fr.hometime.utils.PhoneHelper;
import fr.hometime.utils.Searcher;

/**
 * Definition of a Customer
 */
@Entity 
public class Customer extends Model implements CrudReady<Customer, Customer>, Searchable {
	private static final long serialVersionUID = -7161230172856280985L;
	private static Customer singleton = null;
	
	public static Customer of() {
    	if (singleton == null)
    		singleton = new Customer();
    	return singleton;
    }
	
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
	public CustomerStatus status = CustomerStatus.REAL_CUSTOMER;
	
	@Constraints.Required
	@Column(name="customer_civility", length = 40)
	@Enumerated(EnumType.STRING)
	public CustomerCivility civility = CustomerCivility.MONSIEUR;
	
	public Long value = 0L;
	
	public Long potentiality = 0L;
	
	@Column(name="is_topic_open")
	public Boolean isTopicOpen = false;
	
	@Column(name="should_print_address")
	public Boolean shouldPrintAddress = true;

	@OneToMany(mappedBy="customer", cascade = CascadeType.ALL)
	public List<Order> orders;

	@OneToMany(mappedBy="customer", cascade = CascadeType.ALL)
	public List<CustomerWatch> watches;
	
	@OneToMany(mappedBy="customer", cascade = CascadeType.ALL)
	public List<User> users;
	
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
    
    public static List<Customer> findAllByDescId() {
        return find.orderBy("id DESC").findList();
    }
    
    public static List<Customer> findPrintableByDescId() {
        return find.where().eq("should_print_address", true).orderBy("id DESC").findList();
    }
    
    public static List<Customer> findAllByEmailAsc() {
        return find.orderBy("email ASC").findList();
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
    
    public static List<Customer> findByNameAsc() {
    	return find.where().orderBy("name ASC").findList();
    }
    
    public static List<Customer> findByFirstNameAndNameAsc() {
    	return find.where().orderBy("firstname, name ASC").findList();
    }
    
    public static List<Customer> findWithOpenTopic() {
    	return find.where().eq("is_topic_open", true).findList();
    }
    
    public static List<Customer> findWithOpenTopic(int maxRows) {
    	return find.where().eq("is_topic_open", true).setMaxRows(maxRows).findList();
    }

    public static List<String> getIdsByNameAsc() {
    	List<String> ids = new ArrayList<String>();
    	for (Customer c : findByNameAsc())
    		ids.add(c.id.toString());
    	return ids;
    }
    
    public static List<String> getEmailsByNameAsc() {
    	List<String> emails = new ArrayList<String>();
    	for (Customer c : findByNameAsc())
    		emails.add(c.email);
    	return emails;
    }
    
    public static List<String> getFullnamesByNameAsc() {
    	List<String> fullnames = new ArrayList<String>();
    	for (Customer c : findByNameAsc())
    		fullnames.add(c.getFullNameInversed());
    	return fullnames;
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
    	if (sortBy != null && sortBy.equals("")) {
    		sortBy = "creationDate";
    		order = "desc";
    	}

        return
	        find.where().or(Expr.ilike("email", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }
    
    public static Page<Customer> pageOfSpecialCustomers(int page, int pageSize, String sortBy, String order, String filter) {
    	if (sortBy != null && sortBy.equals("")) {
    		sortBy = "creationDate";
    		order = "desc";
    	}

        return
	        find.fetch("users", "active").where().or(Expr.ilike("email", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
	        	.eq("users.active", true)
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }
    
    
    private static ExpressionList<Customer> getCommonQueryForPartner(String filter, String status, Session session) {
    	ExpressionList<Customer> query = find.where().conjunction()
    			.disjunction().ilike("name", "%" + filter + "%")
    			.endJunction();
    	
    	if (status != null && ! "".equals(status))
			query = query.eq("customer_status", status);
    	
    	return query;
    }
    
    public String getPhoneNumberReadable() {
    	return PhoneHelper.toReadableFormat(this.phoneNumber);
    }
    
    private static Page<Customer> emptyPage() {
    	return find.where().eq("id", "-1").findPagingList(10).getPage(0);
    }
    
    public static Page<Customer> pageForPartner(int page, int pageSize, String sortBy, String order, String filter, String status, Session session) {
    	if (PartnerAndCustomerHelper.isLoggedInUserAPartner(session)) {
    		ExpressionList<Customer> commonQuery = getCommonQueryForPartner(filter, status, session);
    		
    		return commonQuery
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
        			.getPage(page);
    	}
        
    	return emptyPage();
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
        if (lastCommunicationDate != null) {
        	Logger.info("Date received : "+lastCommunicationDate.toString());
        }
        return errors.isEmpty() ? null : errors;
    }
    
    public String toString() {
    	return email;
    }
    
    public String getFullNameWithCivility() {
    	return Messages.get("admin.customer.text.civility." + this.civility.toString().toLowerCase()) + " " + this.getFullName();
    }
    
    public String getFullName() {
    	return this.firstname + " " + this.name;
    }
    
    public String getFullNameInversed() {
    	return this.name + " " + this.firstname;
    }

	@Override
	public Optional<List<? extends Searchable>> findMatching(String pattern) {
		if (pattern != null) {
			List<Customer> results = find.where().disjunction()
					.ilike("firstname", "%" + pattern + "%")
					.ilike("name", "%" + pattern + "%")
					.ilike("email", "%" + pattern + "%")
					.ilike("alternativeEmail", "%" + pattern + "%")
					.ilike("phoneNumber", "%" + pattern + "%")
					.ilike("alternativePhoneNumber", "%" + pattern + "%")
					.findList();
			if (results != null && results.size() != 0)
				return Optional.of(results);
		}
		
		return Optional.empty();
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getDisplayName() {
		return getFullNameInversed();
	}

	@Override
	public String getDetails() {
		List<String> values = Arrays.asList(email, phoneNumber, alternativeEmail, alternativePhoneNumber);
		return Searcher.generateDetails(values);
	}

	@Override
	public Long retrieveId() {
		return id;
	}
	
	@Override
	public Finder<String, Customer> getFinder() {
		return find;
	}

	@Override
	public Page<Customer> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	public boolean isACustomerWithActiveUsers() {
		return CustomerWatchHelper.findByCustomer(this).isPresent();
	}
	
	public boolean isAPro() {
		return isACustomerWithActiveUsers();
	}
}

