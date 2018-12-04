package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import play.db.ebean.Model;

/**
 * Definition of a SMS Sending
 */
@Entity
public class SMS extends Model {
	private static final long serialVersionUID = -6710460401901064563L;

	@Id
	public Long id;

	public String phoneNumber;
	
	public String sender;
	
	public Date sendingDate;
	
	public String message;
	
	public String status = "empty";
	
	public int smsCount = 0;
	
	public String messageId = null;
	
	public SMS(String sender, String phoneNumber, String message) {
		this.sender = sender;
		this.phoneNumber = phoneNumber;
		this.message = message;
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,SMS> find = new Model.Finder(String.class, SMS.class);
    
    public static List<SMS> findAll() {
        return find.all();
    }
    
    public static List<SMS> findAllByAscId() {
        return find.orderBy("id ASC").findList();
    }

    public static SMS findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<SMS> findByPhoneNumber(String phoneNumber) {
        return find.where().eq("phoneNumber", phoneNumber).findList();
    }
    
        
    public static Page<SMS> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().ilike("phoneNumber", "%" + filter + "%")
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }
    
    public SMS updateWithSendingStatus(Date sendingDate, String status, int smsCount, String messageId) {
    	this.sendingDate = sendingDate;
    	this.status = status;
    	this.smsCount = smsCount;
    	this.messageId = messageId;
    	this.save();
    	return this;
    }
    
    public SMS updateWithSendingStatusKO(String errorMessage) {
    	return this.updateWithSendingStatus(new Date(), errorMessage, 0, null);
    }

}

