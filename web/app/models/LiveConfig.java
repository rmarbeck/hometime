package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.Formats;
import play.data.validation.Constraints;
import com.avaje.ebean.Model;

import com.avaje.ebean.PagedList;

/**
 * Definition of a config in database
 */
@Entity 
public class LiveConfig extends Model {
	private static final long serialVersionUID = -7890890184566507495L;

	@Id
    @Constraints.Required
    @Formats.NonEmpty
    public String key;
	
    public String valuestring;
    public boolean valueboolean = false;
    public Long valuelong;
    @Formats.DateTime(pattern="dd/MM/yyyy - hh:mm:ss")
    public Date valuedate;
    
    // -- Queries
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Model.Finder<String,LiveConfig> find = new Model.Finder(String.class, LiveConfig.class);
    
    /**
     * Retrieve all LiveConfig.
     */
    public static List<LiveConfig> findAll() {
        return find.all();
    }

    /**
     * Retrieve a LiveConfig by key
     */
    public static List<LiveConfig> findByKey(String key) {
        return find.where().eq("key", key).findList();
    }

    /**
     * Save in database
     */  
    public static LiveConfig add(LiveConfig newConfig) {
    	if (find.byId(newConfig.key) == null) {
    		newConfig.save();
    	}
    	return newConfig;
    }
    
    /**
     * Update in database
     */  
    public static LiveConfig updateValues(LiveConfig configToUpdate) {
    	if (find.byId(configToUpdate.key) != null) {
    		configToUpdate.update();
    	}
    	return configToUpdate;
    }

    /**
     * Remove from database
     */  
    public static void remove(String key) {
    	LiveConfig toRemove = find.byId(key);
    	if (toRemove != null) {
    		toRemove.delete();
    	}
    }
    
    /**
     * Get String value
     */
    public static String getString(String key) {
    	if (key != null && find.byId(key) != null) {
    		return find.byId(key).valuestring;
    	}
    	return null;
    }
    
    /**
     * Get boolean value
     */
    public static boolean getBoolean(String key) {
    	if (key != null && find.byId(key) != null) {
    		return find.byId(key).valueboolean;
    	}
    	return false;
    }
    
    /**
     * Get Long value
     */
    public static Long getLong(String key) {
    	if (key != null && find.byId(key) != null) {
    		return find.byId(key).valuelong;
    	}
    	return null;
    }

    /**
     * Checking if a key is registered
     */
    public static boolean isKeyDefined(String key) {
    	if (key != null && find.byId(key) != null) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Get Date value
     */
    public static Date getDate(String key) {
    	if (key != null && find.byId(key) != null) {
    		return find.byId(key).valuedate;
    	}
    	return null;
    }

    /**
     * Return a page of LiveConfigs
     *
     * @param page Page to display
     * @param pageSize Number of liveConfigs per page
     * @param sortBy LiveConfig property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public static Page<LiveConfig> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("key", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }


}

