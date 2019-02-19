package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import play.db.ebean.Model;

/**
 * Definition of an incoming call
 */
@Entity 
public class IncomingCall extends Model {
	private static final long serialVersionUID = -4454731712414476877L;

	@Id
	public Long id;

	public String phone_number;
	
	public boolean missed;
	
	public Date call_date;

	
	public IncomingCall(String phone_number) {
		this(phone_number, false);
	}
	
	
	public IncomingCall(String phone_number, boolean missed) {
		this.phone_number = phone_number;
		this.missed = missed;
		this.call_date = new Date();
	}
    
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,IncomingCall> find = new Model.Finder(String.class, IncomingCall.class);
    
    public static List<IncomingCall> findAll() {
        return find.all();
    }

    public static IncomingCall findById(Long id) {
        return find.byId(id.toString());
    }

    public static Page<IncomingCall> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("phone_number", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
}

