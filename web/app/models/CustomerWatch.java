package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import play.db.ebean.Model.Finder;
import play.mvc.Http.Session;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import fr.hometime.utils.CustomerWatchHelper;
import fr.hometime.utils.DateHelper;
import fr.hometime.utils.FormHelper;
import fr.hometime.utils.PartnerAndCustomerHelper;
import fr.hometime.utils.Searcher;
import fr.hometime.utils.SecurityHelper;
import fr.hometime.utils.FormHelper.KeysAndValues;

/**
 * Definition of a Watch belonging to a customer
 */
@Entity 
public class CustomerWatch extends Model implements CrudReady<CustomerWatch, CustomerWatch>, Searchable {
	private static final long serialVersionUID = -1122053555762637015L;
	private static CustomerWatch singleton = null;
	
	public static CustomerWatch of() {
    	if (singleton == null)
    		singleton = new CustomerWatch();
    	return singleton;
    }
	
	public enum CustomerWatchStatus {
	    STORED_BY_WATCH_NEXT ("STORED_BY_WATCH_NEXT"),
	    STORED_BY_STH ("STORED_BY_STH"),
	    STORED_BY_BRAND ("STORED_BY_BRAND"),
	    STORED_BY_OTHER_PARTNER ("STORED_BY_OTHER_PARTNER"),
	    STORED_BY_A_REGISTERED_PARTNER ("STORED_BY_A_REGISTERED_PARTNER"),
	    BACK_TO_CUSTOMER ("BACK_TO_CUSTOMER"),
	    STORED_BY_WATCH_NEXT_OUTSIDE ("STORED_BY_WATCH_NEXT_OUTSIDE"),
	    STORED_BY_WATCH_NEXT_SECURED_1 ("STORED_BY_WATCH_NEXT_SECURED_1"),
	    STORED_BY_WATCH_NEXT_SECURED_2 ("STORED_BY_WATCH_NEXT_SECURED_2"),
	    STORED_BY_WATCH_NEXT_SECURED_3 ("STORED_BY_WATCH_NEXT_SECURED_3");
	    
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
	
	public enum CustomerWatchType {
	    MECA_SIMPLE ("MECA_SIMPLE"),
	    MECA_CHRONO ("MECA_CHRONO"),
	    MECA_COMPLEX ("MECA_COMPLEX"),
	    AUTO_SIMPLE ("AUTO_SIMPLE"),
	    AUTO_CHRONO ("AUTO_CHRONO"),
	    AUTO_COMPLEX ("AUTO_COMPLEX"),
	    QUARTZ_SIMPLE ("QUARTZ_SIMPLE"),
	    QUARTZ_COMPLEX ("QUARTZ_COMPLEX"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2"),
	    RESERVED_3 ("RESERVED_3"),
	    OTHER ("OTHER");
	    
		private String name = "";
		    
		CustomerWatchType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static CustomerWatchType fromString(String name) {
	        for (CustomerWatchType type : CustomerWatchType.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@ManyToOne
	public Partner partner;
	
	public String b2bId;
	
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
	
	@Column(name="collecting_date")
	public Date collectingDate;
	
	public Date lastStatusUpdate;
	
	@Column(length = 10000)
	public String serviceInfos;
	
	@Column(length = 10000)
	public String otherInfos;
	
	@Column(length = 10000)
	public String partnerFromInfos;
	
	@Column(length = 10000)
	public String partnerToInfos;
	
	@Column(length = 10000)
	public String finalCustomerWatchInfos;
	
	@Column(length = 10000)
	public String finalCustomerFromInfos;
	
	@Column(length = 10000)
	public String finalCustomerToInfos;
	
	@Column(length = 10000)
	public String finalCustomerServicePriceAcceptedInfos;
	
	@Column(name="expected_service_end_date")
	public Date expectedServiceEndDate;
	
	@Column(name="first_entry_in_partner_workshop_date")
	public Date firstEntryInPartnerWorkshopDate;
	
	@Column(name="service_due_date")
	public Date serviceDueDate;
	
	@Column(name="service_due_date_must_have")
	public Date serviceDueDateMustHave;
	
	public boolean customerHasConstraint = false;
	
	public boolean customerHasCalledForDelay = false;
	
	@Column(name="last_customer_date")
	public Date lastCustomerCallDate;
	
	@Column(name="last_due_date_communicated")
	public Date lastDueDateCommunicated;
	
	public String lastCustomerCallInformation;
	
	@Constraints.Required
	public Long serviceStatus = 0L;
	
	public Long emergencyLevel = 0L;
	
	@Constraints.Required
	public Long servicePrice = 0L;
	
	public boolean servicePriceAccepted = false;
	
	public boolean serviceDefinitivelyRefused = false;
	
	public boolean newServicePriceNeeded = false;
	
	public boolean servicePaid = false;
	
	@Constraints.Required
	public Long finalCustomerServicePrice = 0L;
	
	public boolean finalCustomerQuotationSent = false;
	
	public boolean finalCustomerServicePriceAccepted = false;
	
	@Column(name="final_customer_acceptance_date")
	public Date finalCustomerServicePriceAcceptedDate;
	
	public boolean finalCustomerServicePaid = false;
	
	public Long finalCustomerEmergencyLevel = 0L;
	
	@Constraints.Required
	public Long finalCustomerServiceStatus = 0L;
	
	@Column(name="back_to_customer_date")
	public Date backToCustomerDate;
	
	public boolean serviceNeeded = true;
	
	public boolean serviceOnHold = false;
	
	public boolean serviceInvoiced = false;
	
	public Long quotation = 1L;
	
	public String testerEmail;
	
	public boolean picturesDoneOnCollect = true;
	
	@Column(length = 10000)
	public String workingConditionsOnCollect;
	
	@Column(length = 10000)
	public String boxConditionsOnCollect;
	
	@Column(length = 10000)
	public String handsConditionsOnCollect;
	
	@Column(length = 10000)
	public String crownAndPushersConditionsOnCollect;
	
	@Column(length = 10000)
	public String backConditionsOnCollect;
	
	@Column(length = 10000)
	public String braceletOrStrapConditionsOnCollect;
	
	@Column(length = 10000)
	public String wateringConditionsOnCollect;
	
	@Column(length = 10000)
	public String glassConditionsOnCollect;
	
	@Column(length = 10000)
	public String otherConditionsRemarksOnCollect;
	
    @ManyToOne
	public Customer customer;
    
    public boolean authenticationNeeded = false;
    
    public boolean isAuthentic = true;
    
    public String authenticationWatchDetails;
    
    @Column(length = 10000)
    public String authenticationInformations;
    
    @Column(length = 10000)
    public String authenticationPrivateInformations;

	@Constraints.Required
	@Column(name="customer_watch_status", length = 40)
	@Enumerated(EnumType.STRING)
	public CustomerWatchStatus status;
	
	@Column(name="customer_watch_type", length = 40)
	@Enumerated(EnumType.STRING)
	public CustomerWatchType type = CustomerWatchType.OTHER;
	
	public CustomerWatch() {
		this.creationDate = new Date();
		this.lastStatusUpdate = creationDate;
		this.collectingDate = creationDate;
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
    
    public static List<CustomerWatch> findForLoggedInPartner(Session session) {
    	return CustomerWatchHelper.findForLoggedInPartner(session);
    }
    
    public static List<CustomerWatch> findForLoggedInPartnerInWorkshop(Session session) {
    	return CustomerWatchHelper.findForLoggedInPartnerInWorkshop(session);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomer(Session session) {
    	return CustomerWatchHelper.findForLoggedInCustomer(session);
    }
    

    public static List<CustomerWatch> findForLoggedInCustomerWaitingForQuotation(Session session) {
    	return CustomerWatchHelper.findForLoggedInCustomerWaitingForQuotation(session);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWaitingForQuotationApproval(Session session) {
    	return CustomerWatchHelper.findForLoggedInCustomerWaitingForQuotationApproval(session);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWorkingOnIt(Session session) {
    	return CustomerWatchHelper.findForLoggedInCustomerWorkingOnIt(session);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWorkDone(Session session) {
    	return CustomerWatchHelper.findForLoggedInCustomerWorkDone(session);
    }
    
    public static List<CustomerWatch> findForLoggedInCustomerWaitingForPayment(Session session) {
    	return CustomerWatchHelper.findForLoggedInCustomerWaitingForPayment(session);
    }
    
    public static List<CustomerWatch> findAllBySerialAsc() {
        return find.where().orderBy("serial ASC").findList();
    }
    
    public static List<CustomerWatch> findAllUnderOurResponsability() {
        return find.where().ne("status", "BACK_TO_CUSTOMER").findList();
    }
    
    public static List<CustomerWatch> findAllUnderOurResponsabilityOrderedByID() {
        return find.fetch("customer").where().ne("status", "BACK_TO_CUSTOMER").orderBy("id desc").findList();
    }
    
    public static List<CustomerWatch> findAllUnderOurResponsabilityOrderedByPriority() {
        return CustomerWatchHelper.sortByPriorityWatches(findAllUnderOurResponsabilityOrderedByID());
    }
    

    public Date getFirstKnownDate() {
    	Date result = DateHelper.getFirstDate(serviceDueDateMustHave, lastDueDateCommunicated);

    	return DateHelper.getFirstDate(result, serviceDueDate);
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
        return find.where().orderBy("customer.name ASC, brand ASC").findList();
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
    	return displayWatchByCustomer(watch, true);
    }
    
    private static String displayWatchByCustomer(CustomerWatch watch, boolean withId) {
    	StringBuilder result = new StringBuilder();
    	if (watch.customer != null) {
    		result.append(watch.customer.getFullNameInversed());
    	} else {
    		result.append("unknown");
    	}
    	result.append(" -> ");
    	result.append(watch.brand);
    	result.append(" ");
    	result.append(watch.model);
    	if (withId)
    		result.append(" [#"+watch.id+"]");
    	
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
    
    public static List<CustomerWatch> findByCustomerBackToCustomer(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
    				.eq("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
        			.orderBy("lastStatusUpdate desc").findList();
    }
    
    public static List<CustomerWatch> findByAuthenticationAskedDateAsc() {
    	return find.where().eq("authenticationNeeded", true)
        			.orderBy("creationDate desc").findList();
    }
    
    
    public static List<CustomerWatch> findForLoggedInCustomerWaitingToBeCollected(Session session) {
    	return CustomerWatchHelper.findForLoggedInCustomerWaitingToBeCollected(session);
    }
    
    public static List<CustomerWatch> findByCustomerWaitingForQuotation(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
    				.eq("finalCustomerServicePrice", 0)
    				.eq("serviceNeeded", true)
    				.ne("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
        			.orderBy("lastStatusUpdate desc, creationDate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerWaitingForQuotationApproval(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
    				.ne("finalCustomerServicePrice", 0)
    				.eq("finalCustomerServicePriceAccepted", false)
    				.eq("serviceNeeded", true)
    				.ne("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
        			.orderBy("lastStatusUpdate desc, creationDate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerWorkingOnIt(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
				.eq("finalCustomerServicePriceAccepted", true)
				.eq("serviceNeeded", true)
    			.orderBy("lastStatusUpdate desc, creationDate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerWorkDone(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
				.eq("serviceNeeded", false)
				.ne("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
    			.orderBy("lastStatusUpdate desc, creationDate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerWaitingForPayment(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
				.eq("serviceNeeded", false)
				.eq("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
				.eq("finalCustomerServicePaid", false)
    			.orderBy("lastStatusUpdate desc, creationDate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerStoredByRegisteredPartner(models.Customer customer) {
    	return find.where().conjunction().eq("customer.id", customer.id)
    				.disjunction()
    				.eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER)
    				.eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_STH)
    				.endJunction()
        			.orderBy("firstEntryInPartnerWorkshopDate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerStoredByUs(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
    				.eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_WATCH_NEXT)
        			.orderBy("lastStatusUpdate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerOut(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
    				.ne("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
        			.orderBy("lastStatusUpdate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerWaitingToBeCollected(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
    				.eq("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
    				.eq("serviceNeeded", true)
        			.orderBy("lastStatusUpdate desc, creationDate desc").findList();
    }
    
    public static List<CustomerWatch> findByCustomerAllButWaitingToBeCollected(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
    				.ne("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
    				.eq("serviceNeeded", true)
        			.orderBy("lastStatusUpdate desc, creationDate desc").findList();
    }
    
    
    
    public static List<CustomerWatch> findByCustomerOtherLocation(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
    				.ne("status", CustomerWatch.CustomerWatchStatus.STORED_BY_WATCH_NEXT)
    				.ne("status", CustomerWatch.CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER)
    				.ne("status", CustomerWatch.CustomerWatchStatus.STORED_BY_STH)
    				.ne("status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
        			.orderBy("next_partial_service desc").findList();
    }
    
    public static List<CustomerWatch> findByPartnerWaitingAcceptation(models.Partner partner) {
    	return find.where().eq("partner.id", partner.id)
    				.eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_WATCH_NEXT)
    				.eq("serviceNeeded", true)
        			.orderBy("creationDate desc").findList();
    }
    
    public static List<CustomerWatch> findByPartnerWaitingServicePrice(models.Partner partner) {
    	return find.where().eq("partner.id", partner.id)
    				.eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER)
    				.eq("serviceNeeded", true)
    				.eq("servicePrice", 0)
        			.orderBy("firstEntryInPartnerWorkshopDate desc").findList();
    }
    
    
    public static List<CustomerWatch> findByPartnerWaitingServicePriceAcceptation(models.Partner partner) {
    	return find.where().eq("partner.id", partner.id)
    				.eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER)
    				.eq("serviceNeeded", true)
    				.ne("servicePrice", 0)
    				.eq("servicePriceAccepted", false)
        			.orderBy("firstEntryInPartnerWorkshopDate desc").findList();
    }
    
    public static List<CustomerWatch> findByPartnerWorkInProgress(models.Partner partner) {
    	return find.where().eq("partner.id", partner.id)
    				.eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER)
    				.eq("serviceNeeded", true)
    				.ne("servicePrice", 0)
    				.eq("servicePriceAccepted", true)
        			.orderBy("firstEntryInPartnerWorkshopDate desc").findList();
    }
    
    public static List<CustomerWatch> findByPartnerServiceFinishedWaitingToCollect(models.Partner partner) {
    	return find.where().eq("partner.id", partner.id)
    				.eq("status", CustomerWatch.CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER)
    				.eq("serviceNeeded", false)
        			.orderBy("lastStatusUpdate desc").findList();
    }
    
    public static CustomerWatch findBySerial(String serial) {
    	return find.where().eq("serial", serial).findUnique();
    }
    
   
    public static FormHelper.KeysAndValues getIdsAndWatchesByAuthenticationAskedDateAsc() {
    	FormHelper.KeysAndValues result = new FormHelper().new KeysAndValues();
    	for (CustomerWatch w : findByAuthenticationAskedDateAsc())
    		result.add(w.id.toString(), w.getCustomerFullName()+" - "+w.getDisplayName());
    	return result;
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

    public static Page<CustomerWatch> pageForWatchmaker(int page, int pageSize, String sortBy, String order, String filter, String status, Session session) {
   		ExpressionList<CustomerWatch> commonQuery = getCommonQueryForWatchmaker(filter, status, session);
    		
   		return commonQuery.eq("serviceNeeded", true).eq("servicePriceAccepted", true)
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
        			.getPage(page);
    }
    
    public static Page<CustomerWatch> pageForWatchmakerWaitingQuotation(int page, int pageSize, String sortBy, String order, String filter, String status, Session session) {
		ExpressionList<CustomerWatch> commonQuery = getCommonQueryForWatchmaker(filter, status, session);
		
		return commonQuery
				.eq("serviceNeeded", true).eq("servicePriceAccepted", false)
				.orderBy(sortBy + " " + order)
				.findPagingList(pageSize)
    			.getPage(page);
    }
    
    public static Page<CustomerWatch> pageForWatchmakerWorkInProgress(int page, int pageSize, String sortBy, String order, String filter, String status, Session session) {
   		ExpressionList<CustomerWatch> commonQuery = getCommonQueryForWatchmaker(filter, status, session);
    		
   		return commonQuery
   					.eq("serviceNeeded", true).eq("servicePriceAccepted", true)
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
        			.getPage(page);

    }
    
    private static ExpressionList<CustomerWatch> getCommonQueryForWatchmaker(String filter, String status, Session session) {
    	ExpressionList<CustomerWatch> query = find.where().conjunction()
    			.disjunction().ilike("model", "%" + filter + "%").ilike("brand", "%" + filter + "%").ilike("CAST(id AS varchar(10))", "%" + filter + "%")
    			.endJunction()
    			.conjunction().ne("customer_watch_status", CustomerWatch.CustomerWatchStatus.BACK_TO_CUSTOMER)
					  .ne("customer_watch_status", CustomerWatch.CustomerWatchStatus.STORED_BY_BRAND)
					  .ne("customer_watch_status", CustomerWatch.CustomerWatchStatus.STORED_BY_OTHER_PARTNER)
					  .ne("customer_watch_status", CustomerWatch.CustomerWatchStatus.STORED_BY_STH)
				.endJunction();
    	
    	if (status != null && ! "".equals(status))
			query = query.eq("customer_watch_status", status);
    	
    	return query;
    }
    
    public static Page<CustomerWatch> pageForPartner(int page, int pageSize, String sortBy, String order, String filter, String status, Session session) {
    	if (PartnerAndCustomerHelper.isLoggedInUserAPartner(session)) {
    		ExpressionList<CustomerWatch> commonQuery = getCommonQueryForPartner(filter, status, session);
    		
    		return commonQuery.eq("status", "STORED_BY_A_REGISTERED_PARTNER").eq("serviceNeeded", true).eq("servicePriceAccepted", true)
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
        			.getPage(page);
    	}
        
    	return emptyPage();
    }
    
    public static Page<CustomerWatch> pageForPartnerWaitingAcceptation(int page, int pageSize, String sortBy, String order, String filter, String status, Session session) {
    	if (PartnerAndCustomerHelper.isLoggedInUserAPartner(session)) {
    		ExpressionList<CustomerWatch> commonQuery = getCommonQueryForPartner(filter, status, session);

    		return commonQuery
    				.or(Expr.eq("status", "STORED_BY_WATCH_NEXT"), Expr.eq("status", "STORED_BY_WATCH_NEXT_OUTSIDE")).eq("serviceNeeded", true)
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
        			.getPage(page);
    	}
        
    	return emptyPage();
    }
    
    public static Page<CustomerWatch> pageForPartnerWaitingQuotation(int page, int pageSize, String sortBy, String order, String filter, String status, Session session) {
    	if (PartnerAndCustomerHelper.isLoggedInUserAPartner(session)) {
    		ExpressionList<CustomerWatch> commonQuery = getCommonQueryForPartner(filter, status, session);
    		
    		return commonQuery
    				.eq("status", "STORED_BY_A_REGISTERED_PARTNER").eq("servicePriceAccepted", false)
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
        			.getPage(page);
    	}
        
    	return emptyPage();
    }
    
    public static Page<CustomerWatch> pageForPartnerWorkInProgress(int page, int pageSize, String sortBy, String order, String filter, String status, Session session) {
    	if (PartnerAndCustomerHelper.isLoggedInUserAPartner(session)) {
    		ExpressionList<CustomerWatch> commonQuery = getCommonQueryForPartner(filter, status, session);
    		
    		return commonQuery
    				.eq("status", "STORED_BY_A_REGISTERED_PARTNER").eq("servicePriceAccepted", true)
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
        			.getPage(page);
    	}
        
    	return emptyPage();
    }
    
    private static ExpressionList<CustomerWatch> getCommonQueryForPartner(String filter, String status, Session session) {
    	ExpressionList<CustomerWatch> query = find.where().conjunction()
    			.disjunction().ilike("model", "%" + filter + "%").ilike("brand", "%" + filter + "%").ilike("CAST(id AS varchar(10))", "%" + filter + "%")
    			.endJunction()
    			.eq("partner.id", PartnerAndCustomerHelper.getLoggedInPartnerID(session));
    	
    	if (status != null && ! "".equals(status))
			query = query.eq("customer_watch_status", status);
    	
    	return query;
    }
    
    private static Page<CustomerWatch> emptyPage() {
    	return find.where().eq("id", "-1").findPagingList(10).getPage(0);
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
	
	public String getWatchDetails() {
		return this.model + " - " + this.reference + " - " + this.serial;
	}
	
	public String getServicePriceStatus() {
		if (newServicePriceNeeded)
			return "service.pricing.to.re.do";
		if (servicePrice == 0)
			return "service.pricing.to.do";
		if (servicePriceAccepted == false)
			return "service.pricing.to.be.validated";
		return "service.pricing.validated";
	}
	
	public String getQuotationClass() {
		try {
			if (newServicePriceNeeded)
				return "toRedo";
			if (servicePrice == 0)
				return "toDo";
			if (servicePriceAccepted == false)
				return "toBeValidated";
			return "standard";
		} catch (RuntimeException re) {
			return "standard";	
		}
	}
	
	public String getWatchStatusClass() {
		return CustomerWatchHelper.getStatusName(this);
	}
	
	public String getFullId() {
		if (b2bId != null && ! "".equals(b2bId))
			return "#"+id+" ("+b2bId+")";
		return "#"+id.toString();
	}
	
	@Override
	public List<? extends Searchable> findMatching(String pattern) {
		if (pattern != null) {
			List<CustomerWatch> results = find.where().disjunction()
					.ilike("brand", "%" + pattern + "%")
					.ilike("model", "%" + pattern + "%")
					.ilike("additionnalModelInfos", "%" + pattern + "%")
					.ilike("reference", "%" + pattern + "%")
					.ilike("serial", "%" + pattern + "%")
					.ilike("movement", "%" + pattern + "%")
					.ilike("otherInfos", "%" + pattern + "%")
					.findList();
			if (results != null && results.size() != 0)
				return results;
		}
		
		return null;
	}

	@Override
	public Long retrieveId() {
		return id;
	}

	@Override
	public String getDisplayName() {
		return brand+" "+model;
	}
	
	public String getCustomerFullName() {
		return customer.getFullName();
	}

	@Override
	public String getDetails() {
		List<String> values = Arrays.asList(brand, model, additionnalModelInfos, reference, serial, movement, otherInfos);
		return Searcher.generateDetails(values);
	}
    
	public String getStatusClass() {
		if (serviceStatus == 100)
			return "finished";
		if (serviceStatus >= 70)
			return "almostFinished";
		if (serviceStatus >= 1)
			return "started";
		return "toStart";
	}

	@Override
	public Finder<String, CustomerWatch> getFinder() {
		return find;
	}

	@Override
	public Page<CustomerWatch> getPage(int page, int pageSize, String sortBy, String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	public String getStatusForCustomer() {
		return CustomerWatchHelper.getStatusForCustomer(this);
	}
	
	public String getNextStepForCustomer() {
		return CustomerWatchHelper.getNextStepForCustomer(this);
	}
	
	public String getFinalCustomerServicePriceAcceptedAsMessageKey() {
		return "admin.customer.watch."+finalCustomerServicePriceAccepted;
	}
	
	public String getCollectingDateAsShortDate() {
		return DateHelper.asShortDate(collectingDate);
	}
	
	public String getFinalCustomerServicePriceAcceptedDateAsShortDate() {
		return DateHelper.asShortDate(finalCustomerServicePriceAcceptedDate);
	}
	
	public CustomerWatchType getTypeOfWatch() {
		return type;
	}
	
}

