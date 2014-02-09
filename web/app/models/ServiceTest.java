package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

/**
 * Definition of a ServiceTest
 */
@Entity 
public class ServiceTest extends Model {
	private static final long serialVersionUID = -1636841749980061395L;

	public enum MovementTypes {
	    MANUAL ("1"),
	    AUTO ("2");
	    
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
	
	public enum BuildPeriod {
	    THIS_YEAR ("0"),
	    THIS_YEAR_MINUS_1 ("1"),
	    THIS_YEAR_MINUS_2 ("2"),
	    THIS_YEAR_MINUS_3 ("3"),
	    THIS_YEAR_MINUS_4 ("4"),
	    THIS_YEAR_MINUS_5 ("5"),
	    THIS_YEAR_MINUS_6 ("6"),
	    THIS_YEAR_MINUS_7 ("7"),
	    THIS_YEAR_MINUS_8 ("8"),
	    THIS_YEAR_MINUS_9 ("9"),
	    THIS_YEAR_MINUS_10 ("10"),
	    THIS_YEAR_MINUS_11_20 ("11"),
	    THIS_YEAR_MINUS_21_30 ("12"),
	    THIS_YEAR_MINUS_31_40 ("13"),
	    THIS_YEAR_MINUS_41_50 ("14"),
	    THIS_YEAR_MINUS_51_60 ("15"),
	    THIS_YEAR_MINUS_61_100 ("16");
	    
		private String name = "";
		    
		BuildPeriod(String name){
		    this.name = name;
		}

		public String toString() {
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static BuildPeriod fromString(String name) {
	        for (BuildPeriod type : BuildPeriod.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum LastServiceYear {
	    THIS_YEAR ("0"),
	    THIS_YEAR_MINUS_1 ("1"),
	    THIS_YEAR_MINUS_2 ("2"),
	    THIS_YEAR_MINUS_3 ("3"),
	    THIS_YEAR_MINUS_4 ("4"),
	    THIS_YEAR_MINUS_5 ("5"),
	    THIS_YEAR_MINUS_6 ("6"),
	    THIS_YEAR_MINUS_7 ("7"),
	    THIS_YEAR_MINUS_8 ("8"),
	    THIS_YEAR_MINUS_9 ("9"),
	    THIS_YEAR_MINUS_10 ("10"),
	    THIS_YEAR_MINUS_11_and_more ("11"),
	    NONE_AFTER_BUYING ("12"),
	    UNKNOWN ("13");
	    
		private String name = "";
		    
		LastServiceYear(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static LastServiceYear fromString(String name) {
	        for (LastServiceYear type : LastServiceYear.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum UsageFrequency {
	    EVERY_DAY ("0"),
	    MOST_OF_TIME ("1"),
	    RARELY ("2"),
	    NEVER ("3");
	    
		private String name = "";
		    
		UsageFrequency(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static UsageFrequency fromString(String name) {
	        for (UsageFrequency type : UsageFrequency.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum MovementComplexity {
	    THREE_HANDS ("0"),
	    CHRONO ("1"),
	    OTHER ("2");
	    
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
	
	public enum TestResult {
	    IN_MORE_THAN_5_YEARS ("0"),
	    IN_5_YEARS ("1"),
	    IN_4_YEARS ("2"),
	    IN_2_TO_3_YEARS ("3"),
	    NEXT_YEAR ("4"),
	    THIS_YEAR ("5"),
	    NOW_FOR_SOFT_SERVICE ("6"),
	    NOW_FOR_FULL_SERVICE ("7");
	    
		public int intValue() {
			return Integer.valueOf(name);
		}
	    
		private String name = "";
		    
		TestResult(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static TestResult fromString(String name) {
	        for (TestResult type : TestResult.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	public Date requestDate;

	public MovementTypes movementType;
	
	public MovementComplexity movementComplexity;
	
	public BuildPeriod buildPeriod;
	
	public LastServiceYear lastServiceYear;
	
	public boolean performanceIssue;
	
	public boolean powerReserveIssue;
	
	public boolean waterIssue;
	
	public boolean doingSport;
	
	public UsageFrequency usageFrequency;

	@Column(length = 1000)
	public String model;

	public String nameOfCustomer;

	public String email;
		
	public ServiceTest() {
		super();
		requestDate = new Date();
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,ServiceTest> find = new Model.Finder(String.class, ServiceTest.class);
    
    public static List<ServiceTest> findAll() {
        return find.all();
    }

    public static ServiceTest findById(Long id) {
        return find.byId(id.toString());
    }
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	if (email!= null && !"".equals(email))
    		content.append("for : "+email+",");
    	content.append(" BuildPeriod is : " + this.buildPeriod.name());
    	content.append(", LastServiceYear is : " + this.lastServiceYear.name());
    	content.append(", usageFrequency is : " + this.usageFrequency.name());
    	content.append(", MovementTypes is : " + this.movementType);
    	content.append(", MovementComplexity is : " + this.movementComplexity);
    	content.append(", PerformanceIssue is : " + this.performanceIssue);
    	content.append(", PowerReserveIssue is : " + this.powerReserveIssue);
    	content.append(", WaterIssue is : " + this.waterIssue);
    	content.append(", DoingSport is : " + this.doingSport);
    	content.append("]");
    	return content.toString();
    }
}

