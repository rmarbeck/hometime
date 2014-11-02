package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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

    public static Brand findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Brand findBySeoName(String seoName) {
    	return find.where().eq("seo_name", seoName).findUnique();
    }
    
    public String toString() {
    	return display_name;
    }
}

