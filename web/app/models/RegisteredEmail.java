package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import fr.hometime.utils.DateHelper;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Definition of an email registered for getting information
 */
@Entity 
public class RegisteredEmail extends Model {
	private static final long serialVersionUID = 664376055837959504L;

	@Id
	public Long id;

	@Constraints.Required
	@Column(unique=true)
	public String email;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	public boolean active = true;
	
	@Column(name="modification_date")
	public Date modificationDate;
	
	public RegisteredEmail(String email) {
		this.email = email;
		this.creationDate = new Date();
		this.modificationDate = this.creationDate;
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,RegisteredEmail> find = new Model.Finder(String.class, RegisteredEmail.class);
    
    public static List<RegisteredEmail> findAll() {
        return find.all();
    }
    
    public static List<RegisteredEmail> findAllRegisteredToday() {
        return find.where().gt("creationDate", DateHelper.toDate(DateHelper.beginningOfTheDay(new Date()))).findList();
    }
    
    public static RegisteredEmail findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }
}

