package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.Logger;
import play.db.ebean.Model;

/**
 * Definition of a Brand
 */
@Entity 
public class Brand extends Model {
	private static final long serialVersionUID = -4523379544447166820L;

	@Id
	public Long id;

	@Column(unique=true)
	public String internal_name;
	
	public String display_name;
	
	public String seo_name;

	public String logo_url;
	
	public String alt;
	
	public boolean supported = true;
	
	@Column(length = 10000)
	public String description;
	
	@Column(length = 10000)
	public String remarks;
	
	public Brand() {
	}
	
	public Brand(String internal_name) {
		this.internal_name = internal_name;
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Brand> find = new Model.Finder(String.class, Brand.class);
    
    public static List<Brand> findAll() {
        return find.all();
    }
    
    public static List<Brand> findAllByAscId() {
        return find.orderBy("id ASC").findList();
    }
    
    public static List<Brand> findAllByAscName() {
        return find.orderBy("internal_name ASC").findList();
    }
    
    public static List<Brand> findAllSupportedByAscId() {
        return find.where().eq("supported", true).orderBy("id ASC").findList();
    }

    public static List<Brand> findAllSupportedByAscName() {
        return find.where().eq("supported", true).orderBy("internal_name ASC").findList();
    }

    public static Brand findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Brand findBySeoName(String seoName) {
    	return find.where().eq("seo_name", seoName).findUnique();
    }
    
    public static Brand findByInternalName(String internalName) {
    	return find.where().eq("internal_name", internalName).findUnique();
    }
    
    public static List<String> getDisplayNamesByNameAsc() {
    	List<String> displayNames = new ArrayList<String>();
    	for (Brand b : findAllByAscName())
    		displayNames.add(b.display_name);
    	return displayNames;
    }
    
    public static List<String> getInternalNamesByNameAsc() {
    	List<String> internalNames = new ArrayList<String>();
    	for (Brand b : findAllByAscName())
    		internalNames.add(b.internal_name);
    	return internalNames;
    }
    
    public static List<String> getIDsByNameAsc() {
    	List<String> ids = new ArrayList<String>();
    	for (Brand b : findAllByAscName())
    		ids.add(b.id.toString());
    	return ids;
    }

    public String toString() {
    	return internal_name;
    }
}

