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
 * Links
 */
@Entity
public class UsefullLink extends Model {

	
	public enum LinkType {
	    PARTNER ("PARTNER"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2"),
	    RESERVED_3 ("RESERVED_3");
	    
		private String name = "";
		    
		LinkType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static LinkType fromString(String name) {
	        for (LinkType type : LinkType.values()) {
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
	
	@Column(length = 50000)
	public String description;
	
	@Column(length = 1000)
	public String name;
	
	public String url;
	
	@Constraints.Required
	@Column(name="link_type", length = 40)
	@Enumerated(EnumType.STRING)
	public LinkType typeOfLink;
	
	@Column(name="should_display")
	public Boolean shouldDisplay = true;
	
	public UsefullLink() {
		super();
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,UsefullLink> find = new Model.Finder(String.class, UsefullLink.class);
    
    public static List<UsefullLink> findAll() {
        return find.all();
    }
    
    public static UsefullLink findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<UsefullLink> findDisplayable() {
        return find.where().eq("should_display", true)
        		.orderBy("creation_date DESC").findList();
    }
    
    public static Page<UsefullLink> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().ilike("name", "%" + filter + "%")
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }
    
}


