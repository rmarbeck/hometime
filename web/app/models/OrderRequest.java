package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.hometime.utils.DateHelper;
import fr.hometime.utils.Jsonable;
import fr.hometime.utils.ListenableModel;
import fr.hometime.utils.SecurityHelper;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Http.Session;

/**
 * Definition of an OrderMy
 */
@Entity 
public class OrderRequest extends ListenableModel {
	private static final long serialVersionUID = 4099194457099968204L;

	public enum OrderTypes {
	    SERVICE ("1"),
	    REPAIR ("2"),
	    INTERMEDIATE ("3"),
	    SETTINGUP ("4"),
	    WATER ("5"),
	    CUSTOMIZATION ("6");
	    
		private String name = "";
		    
		OrderTypes(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static OrderTypes fromString(String name) {
	        for (OrderTypes type : OrderTypes.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum MethodTypes {
		BRAND ("1"),
		OUTLET ("2"),
		BOTH ("3"),;

		private String name = "";

		MethodTypes(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static MethodTypes fromString(String name) {
	        for (MethodTypes method : MethodTypes.values()) {
	            if (method.name.equals(name)) {
	                return method;
	            }
	        }
	        throw new IllegalArgumentException("Illegal method name: " + name);
	    }
	}

	@Id
	public Long id;
	
	public Date requestDate;

	public OrderTypes orderType;

	@ManyToOne
	public Brand brand;
	
	@Column(length = 1000)
	public String model;
	
	public MethodTypes method;
	
	@Column(length = 1000)
	public String remark;
	
	@ManyToOne
	public User managedBy = null;

	public String nameOfCustomer;

	public String email;
	
	public String phoneNumber;
	
	public String city;
	
	public Boolean replied = false;
	
	@Column(name="waiting_for_customer")
	public Boolean waitingForCustomer = false;
	
	public Boolean closed = false;
	
	@Column(name="feedback_asked")
	public Boolean feedbackAsked = false;
	
	@Column(length = 10000)
	public String privateInfos;

	@ManyToOne
	public Watch watchChosen = null;
	
	public OrderRequest() {
		super();
		requestDate = new Date();
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,OrderRequest> find = new Model.Finder(String.class, OrderRequest.class);
    
    public static List<OrderRequest> findAll() {
        return find.all();
    }
    
    public static List<OrderRequest> findAllLatestFirst() {
        return find.orderBy("requestDate DESC").findList();
    }
    
    public static List<OrderRequest> findAllUnReplied() {
        return find.where().conjunction().eq("replied", false)
        								 .eq("waiting_for_customer", false)
        								 .eq("closed", false)
        				   .endJunction()
        		.orderBy("requestDate DESC").findList();
    }
    
    
    public static List<OrderRequest> findAllUnManaged() {
        return find.where().conjunction().eq("replied", false)
										 .eq("waiting_for_customer", false)
										 .eq("feedback_asked", false)
										 .eq("closed", false)
				 		   .endJunction()
        		.orderBy("requestDate DESC").findList();
    }


    public static OrderRequest findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Page<OrderRequest> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().or(Expr.ilike("email", "%" + filter + "%"), Expr.ilike("nameOfCustomer", "%" + filter + "%"))
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }
    
    public OrderRequest close() {
    	this.closed = true;
    	return this;
    }
    
    public OrderRequest replied() {
    	this.replied = true;
    	return this;
    }
    
    public OrderRequest changeFeedbackAsked() {
    	this.feedbackAsked = !this.feedbackAsked;
    	return this;
    }
    
    public OrderRequest setManaged(User user) {
    	this.managedBy = user;
    	return this;
    }
    
    public OrderRequest setUnManaged() {
    	this.managedBy = null;
    	return this;
    }
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	content.append(" Type is : " + this.orderType.name());
    	content.append(", Brand is : " + this.brand.display_name);
    	content.append(", for model : " + this.model);
    	content.append(". Watch chosen : " + ((this.watchChosen==null)?("none"):(this.watchChosen.short_name)));
    	content.append(", for : " + this.email);
    	content.append(", in : " + this.city);
    	content.append(", phonenumber given ? : " + ((this.phoneNumber==null || "".equals(this.phoneNumber))?("no"):("yes")));
    	content.append(", precisions : " + ((this.remark==null)?("none"):(this.remark)));
    	content.append("]");
    	return content.toString();
    }
    
    public String toNotificationSumUp() {
    	StringBuilder content = new StringBuilder();
    	content.append("Type of service is : " + this.orderType.name());
    	content.append("\n");
    	content.append("Where : " + this.method.name());
    	content.append("\n");
    	content.append("Brand is : " + this.brand.display_name);
    	content.append("\n");
    	content.append("Model is : " + this.model);
    	content.append("\n");
    	content.append("\n");
    	content.append("Watch chosen : " + ((this.watchChosen==null)?("none"):(this.watchChosen.short_name)));
    	content.append("\n");
    	content.append("\n");
    	content.append("Customer's email is : " + this.email);
    	content.append("\n");
    	content.append("Living in : " + this.city);
    	content.append("\n");
    	content.append("Has he gave his phonenumber ? : " + ((this.phoneNumber==null || "".equals(this.phoneNumber))?("no"):("yes")));
    	content.append("\n");
    	content.append("\n");
    	content.append("Additional informations : " + ((this.remark==null)?("none"):(this.remark)));
    	return content.toString();
    }
    
    public static int numberOfFeedbacksWaited() {
    	return find.where().conjunction().eq("replied", false)
										 .eq("waiting_for_customer", false)
										 .eq("feedback_asked", true)
										 .eq("closed", false)
		 .endJunction().findRowCount();
    }
    
    public String getNameIfManaged() {
    	if (managedBy != null) {
    		return managedBy.firstname;
    	}
    	return "";
    }
    
    public boolean isManagedByCurrentLogin(Session currentSession) {
    	if (managedBy != null) {
    		return SecurityHelper.getLoggedInUser(currentSession).get().equals(managedBy);
    	}
    	return false;
    }
    
    public boolean isManagedByNobody() {
    	 return managedBy == null;
    }
    
    @Override
    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("id", id)
        	.put("date", DateHelper.asShortDate(requestDate))
        	.put("brand", brand.toString())
        	.put("model", model)
        	.put("nameOfCustomer", nameOfCustomer)
        	.put("city", city)
        	.put("managedBy", managedBy!=null?managedBy.firstname:"");

        return json;
    }
}

