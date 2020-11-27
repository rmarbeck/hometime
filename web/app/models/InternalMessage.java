package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import controllers.CrudReady;
import play.db.ebean.Model;

/**
 * Definition of an Internal Message
 */
@Entity 
public class InternalMessage extends Model implements CrudReady<InternalMessage, InternalMessage> {
	private static final long serialVersionUID = 7745524066076193537L;

	private static InternalMessage singleton = null;
	
	public enum InternalMessageType {
	    ALERT ("ALERT"),
	    WARNING ("WARNING"),
	    TODO ("TODO"),
	    INFO ("INFO"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2");
	    
		private String name = "";
		    
		InternalMessageType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static InternalMessageType fromString(String name) {
	        for (InternalMessageType type : InternalMessageType.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@Column(length = 10000)
	public String body;
	
	@Column(name="internal_message_type", length = 40)
	@Enumerated(EnumType.STRING)
	public InternalMessageType type;
	
	public Date creationDate;
	
	public Date deadLine;
	
	public Date autoEndDate;
	
	public Date lastDisablingDate;

	public String readMoreUrl;
	
	public boolean active = true;
	
	public boolean shouldDisplayOnDash = true;
	
	public String privateInfos;
	
	public InternalMessage() {
		creationDate = new Date();
	}
	
	public InternalMessage(String body) {
		this();
		this.body = body;
	}
	
	public static InternalMessage of() {
    	if (singleton == null)
    		singleton = new InternalMessage();
    	return singleton;
    }
    
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,InternalMessage> find = new Model.Finder(String.class, InternalMessage.class);
    
    public static List<InternalMessage> findAll() {
        return find.all();
    }
    
    public static List<InternalMessage> findAllActive() {
        return find.where().eq("active", true).orderBy("creationDate DESC").findList();
    }
    
    public static List<InternalMessage> findAllActiveForDash() {
        return find.where().eq("active", true).eq("shouldDisplayOnDash", true).orderBy("creationDate DESC").findList();
    }

    public static InternalMessage findById(Long id) {
        return find.byId(id.toString());
    }

	@Override
    public Page<InternalMessage> getPage(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("body", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

	@Override
	public Finder<String, InternalMessage> getFinder() {
		return find;
	}
}

