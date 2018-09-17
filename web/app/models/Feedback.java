package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;

import play.data.validation.Constraints;
import com.avaje.ebean.Model;

/**
 * Definition of a Customer Feedback
 */
@Entity
public class Feedback extends Model {
	private static final long serialVersionUID = 9184286567164867544L;
	
	public enum FeedbackType {
	    FACEBOOK ("FACEBOOK"),
	    LINKED_IN ("LINKED_IN"),
	    INTERNAL ("INTERNAL"),
	    OTHER ("OTHER");
	    
		private String name = "";
		    
		FeedbackType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static FeedbackType fromString(String name) {
	        for (FeedbackType type : FeedbackType.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(name="display_date")
	public Date displayDate;
	
	@Column(length = 50000)
	public String body;
	
	@Column(length = 1000)
	public String author;
	
	public String pictureUrl;
	
	@Constraints.Required
	@Column(name="feeedback_type", length = 40)
	@Enumerated(EnumType.STRING)
	public FeedbackType typeOfFeedback;
	
	@Column(name="should_display")
	public Boolean shouldDisplay = true;
	
	@Column(name="may_be_emphasized")
	public Boolean mayBeEmphasized = true;
	
	public Feedback() {
		super();
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Feedback> find = new Model.Finder(String.class, Feedback.class);
    
    public static List<Feedback> findAll() {
        return find.all();
    }
    
    public static Feedback findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<Feedback> findDisplayable() {
        return find.where().eq("should_display", true)
        		.orderBy("display_date DESC").findList();
    }
    
    public static List<Feedback> findEmphasizable() {
        return find.where().and(
        						Expr.eq("should_display", true),
        						Expr.eq("may_be_emphasized", true))
        		.orderBy("display_date DESC").findList();
    }

    public static PagedList<Feedback> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().ilike("author", "%" + filter + "%")
	            .orderBy(sortBy + " " + order)
	            .findPagedList(page, pageSize);
	            
    }
    
}


