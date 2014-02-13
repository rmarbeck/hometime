package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import fr.hometime.utils.SecurityHelper;
import play.Logger;
import play.db.ebean.Model;

/**
 * Definition of a Brand
 */
@Entity 
public class User extends Model {
	private static final long serialVersionUID = -6051070381002940159L;

	public enum Role {
	    ADMIN ("1"),
	    CUSTOMER ("2"),
	    RESERVED_1 ("3"),
	    RESERVED_2 ("4");
	    
		private String name = "";
		    
		Role(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static Role fromString(String name) {
	        for (Role type : Role.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;

	@Column(unique=true)
	public String email;
	
	public String password;
	
	public Role role;

	public String firstname;
	
	public String name;
	
	public boolean active = true;
	
	public User(String email) {
		this.email = email;
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,User> find = new Model.Finder(String.class, User.class);
    
    public static List<User> findAll() {
        return find.all();
    }
    
    public static List<User> findAllByAscId() {
        return find.orderBy("id ASC").findList();
    }

    public static User findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }
    
    public static String authenticate(String email, String password) {
    	if (isPasswordMatching(email, password))
    		return email;
    	return null;
    }
    
    private static boolean isPasswordMatching(String email, String password) {
    	User loggedInUser = User.findByEmail(email);
    	if (loggedInUser == null)
    		return false;
    	return loggedInUser.isPasswordCorrect(password);
    }
    
    private boolean isPasswordCorrect(String password) {
    	Logger.debug("Checking password");
    	if (isInactive())
    		return false;
    	if (doesEncryptedPasswordMatch(password))
    		return true;
    	if (doesPasswordMatch(password))
    		return encryptPasswordInDB();
    	return false;
    }
    
    private boolean doesEncryptedPasswordMatch(String password) {
    	Logger.debug("Checking encrypted password");
    	return this.password.equals(SecurityHelper.toMD5(password));
    }
    
    private boolean doesPasswordMatch(String password) {
    	Logger.debug("Checking clear password");
    	return this.password.equals(password);
    }
    
    private boolean encryptPasswordInDB() {
    	Logger.debug("Encrypting password");
    	this.password = SecurityHelper.toMD5(password);
    	this.update();
    	return true;
    }
    
    private boolean isInactive() {
    	return !this.active;
    }
}

