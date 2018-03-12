package models;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import fr.hometime.utils.SecurityHelper;
import play.Logger;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * Definition of a Brand
 */
@Entity
@Table(name = "user_table")
public class User extends Model implements CrudReady<User, User> {
	private static final long serialVersionUID = -6051070381002940159L;
	private static User singleton = null;
	
	private static final String ADMIN_QUICK_EMAIL = "admin_quick@hometime.fr";
	public static final int ADMIN_QUICK_MAX_ATTEMPT = 3;

	public enum Role {
	    ADMIN ("1"),
	    CUSTOMER ("2"),
	    COLLABORATOR ("3"),
	    PARTNER ("4");
	    
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
	
	public int numberOfBadPasswords = 0;
	
	@ManyToOne
	public Partner partner;
	
	@ManyToOne
	public Customer customer;
	
	public static User of() {
    	if (singleton == null)
    		singleton = new User();
    	return singleton;
    }
	
	private User() {
	}
	
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
    
    public static User findQuickAdmin() {
        return findByEmail(ADMIN_QUICK_EMAIL);
    }
    
    public static List<User> findQuickLogins() {
        return find.where().contains("email", "_quick").findList();
    }
    
    public static String authenticate(String email, String password) {
    	if (isPasswordMatching(email, password))
    		return email;
    	return null;
    }
    
    public static String quickAuthenticateAdmin(String password) {
    	return SecurityHelper.quickAuthenticateAdmin(password);
    }
    
    public static boolean isPasswordMatching(String email, String password) {
    	User loggedInUser = User.findByEmail(email);
    	if (loggedInUser == null)
    		return false;
    	if (loggedInUser.isPasswordCorrect(password)) {
        	return true;
    	} else if (loggedInUser.isPasswordToChangeAtFirstUse()) {
    		loggedInUser.password = password;
    		loggedInUser.encryptPasswordInDB();
    		return true;
    	} else {
   			incrementNumberOfBadPasswords(loggedInUser);
        	return false;
    	}
    }
    
    public static void resetNumberOfBadPasswords(User user) {
    	user.numberOfBadPasswords = 0;
    	user.update();
    }
    
    private static void incrementNumberOfBadPasswords(User user) {
    	user.numberOfBadPasswords++;
    	user.update();
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
    
    private boolean isPasswordToChangeAtFirstUse() {
    	return "tochangeatfirstuse".equals(password);
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
    
    public String toString() {
    	return this.email;
    }
    
    public static Page<User> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().or(Expr.ilike("email", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }
    

	@Override
	public Finder<String, User> getFinder() {
		return find;
	}

	@Override
	public Page<User> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
}

