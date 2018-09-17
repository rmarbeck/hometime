package models;

import static fr.hometime.utils.SecurityHelper.doesFieldContainSPAM;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import com.avaje.ebean.Model;
import play.i18n.Messages;

/**
 * Definition of an Buy request
 */
@Entity 
public class BuyRequest extends Model {
	private static final long serialVersionUID = 2815614900483807359L;

	public enum WatchStory {
	    NEW_ONLY ("NEW_ONLY"),
	    WORN_ONLY ("WORN_ONLY"),
	    BOTH ("BOTH");
	    
		private String name = "";
		    
		WatchStory(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}

		public static WatchStory fromString(String name) {
	        for (WatchStory story : WatchStory.values()) {
	            if (story.name.equals(name)) {
	                return story;
	            }
	        }
	        throw new IllegalArgumentException("Illegal story name: " + name);
	    }
	}
	
	public enum Package {
		FULL_SET_ONLY ("FULL_SET_ONLY"),
		WATCH_ONLY ("WATCH_ONLY"),
		BOTH ("BOTH"),;

		private String name = "";

		Package(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static Package fromString(String name) {
	        for (Package pack : Package.values()) {
	            if (pack.name.equals(name)) {
	                return pack;
	            }
	        }
	        throw new IllegalArgumentException("Illegal package name: " + name);
	    }
	}

	public enum Timeframe {
		EMERGENCY ("EMERGENCY"),
		WITHIN_MONTH ("WITHIN_MONTH"),
		WITHIN_3_MONTHS ("WITHIN_3_MONTHS"),
		OPPORTUNITY ("OPPORTUNITY");

		private String name = "";

		Timeframe(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static Timeframe fromString(String name) {
	        for (Timeframe timeframe : Timeframe.values()) {
	            if (timeframe.name.equals(name)) {
	                return timeframe;
	            }
	        }
	        throw new IllegalArgumentException("Illegal timeframe name: " + name);
	    }
	}
	
	@Id
	public Long id;
	
	public Date requestDate;
	
	@Constraints.Required
	@Column(name="watch_story", length = 40)
	@Enumerated(EnumType.STRING)
	public WatchStory story;
	
	@Constraints.Required
	@Column(name="watch_package", length = 40)
	@Enumerated(EnumType.STRING)
	public Package pack;
	
	@Constraints.Required
	@Column(name="request_timeframe", length = 40)
	@Enumerated(EnumType.STRING)
	public Timeframe timeframe;

	@Constraints.Required
	@ManyToOne
	public Brand brand;
	
	@Column(length = 1000)
	public String model;

	@Column(length = 10000)
	public String criteria;
	
	@Column(length = 10000)
	public String remark;

	public String expectedPrice;
	
	public String priceHigherBound;
	
	@Constraints.Required
	public String nameOfCustomer;

	@Constraints.Required
	public String email;
	
	public String phoneNumber;
	
	@Constraints.Required
	public String city;
	
	public Boolean replied = false;
	
	@Column(name="waiting_for_customer")
	public Boolean waitingForCustomer = false;
	
	public Boolean closed = false;
	
	@Column(name="feedback_asked")
	public Boolean feedbackAsked = false;
	
	public BuyRequest() {
		super();
		requestDate = new Date();
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,BuyRequest> find = new Model.Finder(String.class, BuyRequest.class);
    
    public static List<BuyRequest> findAll() {
        return find.all();
    }
    
    public static List<BuyRequest> findAllLatestFirst() {
        return find.orderBy("requestDate DESC").findList();
    }
    
    public static List<BuyRequest> findAllUnReplied() {
        return find.where()
        		.and(
        				Expr.and(
        						Expr.eq("replied", false),
        						Expr.eq("waiting_for_customer", false)
        						)
        						,Expr.eq("closed", false))
        		.orderBy("requestDate DESC").findList();
    }
    
    public static List<BuyRequest> findAllUnReplied(int maxRows) {
        return find.where()
        		.and(
        				Expr.and(
        						Expr.eq("replied", false),
        						Expr.eq("waiting_for_customer", false)
        						)
        						,Expr.eq("closed", false))
        		.setMaxRows(maxRows)
        		.orderBy("requestDate DESC").findList();
    }

    public static BuyRequest findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static PagedList<BuyRequest> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().or(Expr.ilike("email", "%" + filter + "%"), Expr.ilike("nameOfCustomer", "%" + filter + "%"))
	            .orderBy(sortBy + " " + order)
	            .findPagedList(page, pageSize);
	            
    }
    
    public BuyRequest close() {
    	this.closed = true;
    	return this;
    }
    
    public BuyRequest replied() {
    	this.replied = true;
    	return this;
    }
    
    
    public List<ValidationError> validate() {
    	List<ValidationError> errors = new ArrayList<ValidationError>();
        if (doesFieldContainSPAM(criteria))
    		errors.add(new ValidationError("criteria", Messages.get("buy.request.validation.error.criteria.spam.detected")));
        if (doesFieldContainSPAM(remark))
    		errors.add(new ValidationError("remark", Messages.get("buy.request.validation.error.remark.spam.detected")));
        if (doesFieldContainSPAM(model))
    		errors.add(new ValidationError("model", Messages.get("buy.request.validation.error.model.spam.detected")));
        return errors.isEmpty() ? null : errors;
    }
}

