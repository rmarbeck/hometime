package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

/**
 * Definition of a ContactRequest
 */
@Entity 
public class ContactRequest extends Model {
	private static final long serialVersionUID = 6924304473085290802L;

	@Id
	public Long id;
	
	public String title;
	
	@Column(length = 1000)
	public String message;
	
	public String name;

	public String email;
	
	public Date requestDate;
	
	public ContactRequest() {
		super();
		requestDate = new Date();
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,ContactRequest> find = new Model.Finder(String.class, ContactRequest.class);
    
    public static List<ContactRequest> findAll() {
        return find.all();
    }

    public static ContactRequest findById(Long id) {
        return find.byId(id.toString());
    }
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	content.append(" Title is : " + this.title);
    	content.append(", Message is : " + this.message);
    	content.append(", from : " + this.name + ((this.email==null || "".equals(this.email))?(" (none)"):(" ("+this.email+")")));
    	content.append("]");
    	return content.toString();
    }
}

