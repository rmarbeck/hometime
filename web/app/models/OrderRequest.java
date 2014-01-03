package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

/**
 * Definition of an OrderMy
 */
@Entity 
public class OrderRequest extends Model {
	private static final long serialVersionUID = 4099194457099968204L;

	public enum OrderTypes {
	    SERVICE ("1"),
	    REPAIR ("2");
	    
		private String name = "";
		    
		OrderTypes(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
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
		OUTLET ("2");

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

	public OrderTypes type;

	@ManyToOne
	public Brand brand;
	
	@Column(length = 1000)
	public String model;
	
	public MethodTypes method;
	
	@Column(length = 1000)
	public String remark;
	
	@ManyToOne
	public Watch watchChosen = null;

	public String nameOfCustomer;

	public String email;
	
	public String phoneNumber;
	
	public String city;
	
	public OrderRequest() {
		super();
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,OrderRequest> find = new Model.Finder(String.class, OrderRequest.class);
    
    public static List<OrderRequest> findAll() {
        return find.all();
    }

    public static OrderRequest findById(Long id) {
        return find.byId(id.toString());
    }
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	content.append(" Type is : " + this.type.name());
    	content.append(", Brand is : " + this.brand.display_name);
    	content.append(", for model : " + this.model);
    	content.append(". Watch chosen : " + ((this.watchChosen==null)?("none"):(this.watchChosen.short_name)));
    	content.append(", for : " + this.email);
    	content.append("]");
    	return content.toString();
    }
}

