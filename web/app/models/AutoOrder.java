package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import play.db.ebean.Model;

/**
 * Definition of a AutoOrder
 */
@Entity 
public class AutoOrder extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4157972081094691712L;

	public enum MovementTypes {
	    MANUAL ("MANUAL"),
	    AUTO ("AUTO"),
		QUARTZ ("QUARTZ"),
		UNKNOWN ("UNKNOWN");
	    
		private String name = "";
		    
		MovementTypes(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static MovementTypes fromString(String name) {
	        for (MovementTypes type : MovementTypes.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
		
	public enum MovementComplexity {
	    TWO_OR_THREE_HANDS ("TWO_OR_THREE_HANDS"),
	    CHRONO ("CHRONO"),
	    GMT ("GMT"),
	    OTHER ("OTHER"),
	    UNKNOWN ("UNKNOWN");
	    
		private String name = "";
		    
		MovementComplexity(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static MovementComplexity fromString(String name) {
	        for (MovementComplexity complexity : MovementComplexity.values()) {
	            if (complexity.name.equals(name)) {
	                return complexity;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum EmergencyLevel {
	    NORMAL ("NORMAL"),
	    HIGH ("HIGH"),
	    VERY_HIGH ("VERY_HIGH"),
	    LOW ("LOW");
	    
		private String name = "";
		    
		EmergencyLevel(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static EmergencyLevel fromString(String name) {
	        for (EmergencyLevel emergency : EmergencyLevel.values()) {
	            if (emergency.name.equals(name)) {
	                return emergency;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum WorkingCondition {
	    WORKING ("WORKING"),
	    NOT_WORKING ("NOT_WORKING"),
	    BROKEN ("BROKEN");
	    
		private String name = "";
		    
		WorkingCondition(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static WorkingCondition fromString(String name) {
	        for (WorkingCondition condition : WorkingCondition.values()) {
	            if (condition.name.equals(name)) {
	                return condition;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	@Id
	public Long id;
	
	public Date requestDate;

	public Brand brand;
	
	public MovementTypes movementType;
	
	public MovementComplexity movementComplexity;
	
	public EmergencyLevel emergencyLevel;
	
	public WorkingCondition workingCondition;
			
	public AutoOrder() {
		super();
		requestDate = new Date();
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,AutoOrder> find = new Model.Finder(String.class, AutoOrder.class);
    
    public static List<AutoOrder> findAll() {
        return find.all();
    }

    public static AutoOrder findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Page<AutoOrder> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().ilike("brand", "%" + filter + "%")
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
   		content.append("for : "+brand+",");
    	content.append(", MovementTypes is : " + this.movementType);
    	content.append(", MovementComplexity is : " + this.movementComplexity);
    	content.append("]");
    	return content.toString();
    }
}

