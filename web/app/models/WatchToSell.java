package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.i18n.Messages;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import controllers.CrudReady;
import fr.hometime.utils.FormHelper;
import fr.hometime.utils.Searcher;
import fr.hometime.utils.FormHelper.KeysAndValues;

/**
 * Definition of a Watch to be sold
 */
@Entity
@JsonSerialize(using = fr.hometime.utils.WatchToSellSerializer.class)
public class WatchToSell extends Model implements CrudReady<WatchToSell, WatchToSell>, Searchable {
	private static final long serialVersionUID = 5289876388952107742L;
	private static WatchToSell singleton = null;

	public enum WatchToSellOwnerStatus {
	    OWNED_BY_US ("OWNED_BY_US"),
	    OWNED_BY_CUSTOMER ("OWNED_BY_CUSTOMER"),
	    OWNED_BY_PARTNER ("OWNED_BY_PARTNER"),
	    OWNED_AND_STORED_BY_CUSTOMER ("OWNED_AND_STORED_BY_CUSTOMER"),
	    RESERVED_2 ("RESERVED_2");
	    
		private String name = "";
		    
		WatchToSellOwnerStatus(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static WatchToSellOwnerStatus fromString(String name) {
	        for (WatchToSellOwnerStatus status : WatchToSellOwnerStatus.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum WatchToSellStatus {
	    TO_SELL ("TO_SELL"),
	    RESERVED_FOR_A_CUSTOMER ("RESERVED_FOR_A_CUSTOMER"),
	    SOLD ("SOLD"),
	    SELLING_CANCELED ("SELLING_CANCELED"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2");
	    
		private String name = "";
		    
		WatchToSellStatus(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static WatchToSellStatus fromString(String name) {
	        for (WatchToSellStatus status : WatchToSellStatus.values()) {
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
	
	@Column(name="pruchase_date")
	public Date purchaseDate;
	
	@Column(name="start_selling_date")
	public Date startSellingDate;
	
	@Column(name="selling_date")
	public Date sellingDate;
	
	@ManyToOne
	public Brand brand;
	
	public String model;

	public String additionnalModelInfos;
	
	public String reference;
	
	public String serial;
	
	public String serial2;
	
	public String movement;
	
	public String strap;
	
	public String seller;
	
	@ManyToOne
	public Customer customerThatSellsTheWatch;
	
	@Column(length = 10000)
	public String additionnalInfos;
	
	@Column(length = 10000)
	public String privateInfos;
	
	public String year;
	
	public Boolean hasBox;
	
	public Boolean hasPapers;
	
	public Boolean isNew;
	
	@ManyToOne
	public Customer customerThatBoughtTheWatch;

	@Constraints.Required
	@Enumerated(EnumType.STRING)
	public WatchToSellOwnerStatus ownerStatus;
	
	@Constraints.Required
	@Enumerated(EnumType.STRING)
	public WatchToSellStatus status;
	
	public Boolean purchaseInvoiceAvailable;
	
	public long sellingPrice = 0;
	
	public long purchasingPrice = 0;
	
	@OneToOne
	public ExternalDocument purchaseInvoice;
	
	public Boolean shouldBeInRegistry = true;
	
	public Boolean isInRegistry = false;
	
	public String reportingInfos;
	
	public WatchToSell() {
		
	}
	
	public WatchToSellOwnerStatus getOwnerStatus() {
		return ownerStatus;
	}
	
	public void setOwnerStatus(String name) {
		ownerStatus = WatchToSellOwnerStatus.fromString(name);
	}
	
	public WatchToSellStatus getStatus() {
		return status;
	}
	
	public void setStatus(String name) {
		status = WatchToSellStatus.fromString(name);
	}
	
	public static WatchToSell of() {
    	if (singleton == null)
    		singleton = new WatchToSell();
    	return singleton;
    }
	
	public static WatchToSell duplicate(Long id) {
		WatchToSell foundWatch = findById(id);
    	if (foundWatch != null)
    		return duplicateWatch(foundWatch);
    	return new WatchToSell();
    }

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,WatchToSell> find = new Model.Finder(String.class, WatchToSell.class);
    
    public static List<WatchToSell> findAll() {
        return find.all();
    }
    
    public static WatchToSell findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<WatchToSell> findAllBySerialAsc() {
        return find.where().orderBy("serial ASC").findList();
    }
    
    public static List<WatchToSell> findAllByPurchaseDateAsc() {
        return find.where().orderBy("purchaseDate ASC").findList();
    }
    
    public static List<WatchToSell> findAllByCustomerAndBrandAsc() {
        return find.fetch("customerThatBoughtTheWatch").fetch("brand").where().orderBy("customerThatBoughtTheWatch.name ASC, brand.display_name ASC").findList();
    }
    
    public static List<WatchToSell> findAllBySellingCustomerAndBrandAsc() {
        return find.where().isNotNull("customerThatSellsTheWatch").orderBy("customerThatSellsTheWatch.name ASC, brand.display_name ASC").findList();
    }
    
    public static List<String> getSerialsBySerialAsc() {
    	List<WatchToSell> watches = findAllBySerialAsc();
    	List<String> serials = new ArrayList<String>();
    	if (watches != null) {
    		for (WatchToSell watch : watches)
    			serials.add(watch.readSerial());
    	}
    	return serials;
    }
    
    public static List<String> getIdsByCustomersAsc() {
    	List<WatchToSell> watches = findAllByCustomerAndBrandAsc();
    	List<String> watchesByCustomers = new ArrayList<String>();
    	if (watches != null) {
    		for (WatchToSell watch : watches)
    			watchesByCustomers.add(watch.id.toString());
    	}
    	return watchesByCustomers;
    }
    
    private static String displayWatchByCustomer(WatchToSell watch) {
    	StringBuilder result = new StringBuilder();
    	if (watch.customerThatBoughtTheWatch != null) {
    		result.append(watch.customerThatBoughtTheWatch.getFullNameInversed());
    	} else {
    		result.append("unknown");
    	}
    	result.append(" -> ");
    	result.append(watch.brand.display_name);
    	result.append(" ");
    	result.append(watch.model);
    	
    	return result.toString();
    }
    
    private static String displayWatchByCustomerThatSellsTheWatch(WatchToSell watch) {
    	StringBuilder result = new StringBuilder();
    	if (watch.customerThatSellsTheWatch != null) {
    		result.append(watch.customerThatSellsTheWatch.getFullNameInversed());
    	} else {
    		result.append("unknown");
    	}
    	result.append(" -> ");
    	result.append(watch.brand.display_name);
    	result.append(" ");
    	result.append(watch.model);
    	
    	return result.toString();
    }
    
    public static List<String> getWatchesByCustomersAsc() {
    	List<WatchToSell> watches = findAllByCustomerAndBrandAsc();
    	List<String> watchesByCustomers = new ArrayList<String>();
    	if (watches != null) {
    		for (WatchToSell watch : watches)
    			watchesByCustomers.add(displayWatchByCustomer(watch));
    	}
    	return watchesByCustomers;
    }
    
    public static List<String> getIdsBySellingCustomersAsc() {
    	List<WatchToSell> watches = findAllBySellingCustomerAndBrandAsc();
    	List<String> watchesByCustomers = new ArrayList<String>();
    	if (watches != null) {
    		for (WatchToSell watch : watches)
    			watchesByCustomers.add(watch.id.toString());
    	}
    	return watchesByCustomers;
    }
    
    public static List<String> getWatchesBySellingCustomersAsc() {
    	List<WatchToSell> watches = findAllBySellingCustomerAndBrandAsc();
    	List<String> watchesByCustomers = new ArrayList<String>();
    	if (watches != null) {
    		for (WatchToSell watch : watches)
    			watchesByCustomers.add(displayWatchByCustomerThatSellsTheWatch(watch));
    	}
    	return watchesByCustomers;
    }
    
    public static FormHelper.KeysAndValues getIdsAndWatchesByACustomerAsc() {
    	FormHelper.KeysAndValues result = new FormHelper().new KeysAndValues();
    	for (WatchToSell w : findAllByCustomerAndBrandAsc())
    		result.add(w.id.toString(), displayWatchByCustomer(w));
    	return result;
    }
    
    public static List<WatchToSell> findByCustomer(models.Customer customer) {
    	return find.where().eq("customerThatBoughtTheWatch.id", customer.id)
        			.orderBy("creationDate desc").findList();
    }
    
    public static WatchToSell findBySerial(String serial) {
    	return find.where().eq("serial", serial).findUnique();
    }

    public static Page<WatchToSell> page(int page, int pageSize, String sortBy, String order, String filter) {
    	if (sortBy == null || sortBy.equals("")) {
    		sortBy = "brand.display_name";
    		order = "ASC";
    	}
        return 
            find.fetch("brand").where().disjunction().add(Expr.ilike("model", "%" + filter + "%"))
            										 .add(Expr.ilike("reference", "%" + filter + "%"))
            										 .add(Expr.ilike("serial", "%" + filter + "%"))
            										 .add(Expr.ilike("brand.display_name", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
    
    public static Page<WatchToSell> page(int page, int pageSize, String sortBy, String order, String filter, String status) {
    	if (status == null || "".equals(status))
    		return page(page, pageSize, sortBy, order, filter);
        
    	return 
            find.where().and(
            		Expr.or(
            				Expr.ilike("model", "%" + filter + "%"),
            				Expr.or(
            						Expr.ilike("reference", "%" + filter + "%"),
            						Expr.ilike("brand.display_name", "%" + filter + "%"))
            				)
            		, Expr.eq("customer_watch_status", status))
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
	
	private boolean previousStatusWasDifferent() {
		WatchToSell inDB = WatchToSell.findById(this.id);
		return !inDB.status.equals(this.status);
	}

	@Override
	public Model.Finder<String, WatchToSell> getFinder() {
		return find;
	}

	@Override
	public Page<WatchToSell> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private void checkBeforeSaving() {
		Logger.debug("-------------------> "+this.brand);
		if (this.brand != null)
			this.brand = Brand.findByInternalName(this.brand.internal_name);
		if (this.customerThatBoughtTheWatch != null)
			this.customerThatBoughtTheWatch = Customer.findByEmail(this.customerThatBoughtTheWatch.email);
		if (this.customerThatSellsTheWatch != null)
			this.customerThatSellsTheWatch = Customer.findByEmail(this.customerThatSellsTheWatch.email);
		if (this.purchaseInvoice != null)
			this.purchaseInvoice = ExternalDocument.findByName(this.purchaseInvoice.name);
	}
	
	private static WatchToSell duplicateWatch(WatchToSell watchToDuplicate) {
		WatchToSell duplicatedWatch = new WatchToSell();
		duplicatedWatch.brand = watchToDuplicate.brand;
		duplicatedWatch.model = watchToDuplicate.model;
		duplicatedWatch.reference = watchToDuplicate.reference;
		duplicatedWatch.movement = watchToDuplicate.movement;
		duplicatedWatch.strap = watchToDuplicate.strap;
		duplicatedWatch.additionnalModelInfos = watchToDuplicate.additionnalModelInfos;
		return duplicatedWatch;
	}
	
    public String getBrandDisplayName() {
    	if (brand != null)
    		return brand.display_name;
    	return "unknown";
    }
    
    public String getFullNameWithCivility() {
    	if (customerThatBoughtTheWatch != null)
    		return customerThatBoughtTheWatch.getFullNameWithCivility();
    	return null;
    }
    
    public String getBrandName() {
		return getBrandDisplayName();
	}
    
    public Brand getBrand() {
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
	
	public String getStrap() {
		return strap;
	}

	@Override
	public List<? extends Searchable> findMatching(String pattern) {
		if (pattern != null) {
			List<WatchToSell> results = find.where().disjunction()
					.ilike("brand.display_name", "%" + pattern + "%")
					.ilike("model", "%" + pattern + "%")
					.ilike("additionnalModelInfos", "%" + pattern + "%")
					.ilike("reference", "%" + pattern + "%")
					.ilike("serial", "%" + pattern + "%")
					.ilike("movement", "%" + pattern + "%")
					.ilike("seller", "%" + pattern + "%")
					.ilike("additionnalInfos", "%" + pattern + "%")
					.ilike("privateInfos", "%" + pattern + "%")
					.findList();
			if (results != null && results.size() != 0)
				return results;
		}
		
		return null;
	}
	
	@Override
	public String getDetails() {
		List<String> values = Arrays.asList(brand.display_name, model, additionnalModelInfos, reference, serial, movement, seller, additionnalInfos, privateInfos);
		return Searcher.generateDetails(values);
	}

	@Override
	public Long retrieveId() {
		return id;
	}

	@Override
	public String getDisplayName() {
		return brand.display_name+" "+model;
	}
	
	public boolean isVirtualWarrant() {
		 return ownerStatus.equals(WatchToSellOwnerStatus.OWNED_AND_STORED_BY_CUSTOMER);
	}
	
}

