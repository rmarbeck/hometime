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

	public String name;

	public String logo_url;
	
	public String alt;
	
	public boolean supported = true;
	
	@Column(length = 10000)
	public String description;
	
	@Column(length = 10000)
	public String remarks;
	
	public Brand(String name) {
		this.name = name;
	}
    
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Brand> find = new Model.Finder(String.class, Brand.class);
    
    public static List<Brand> findAll() {
        return find.all();
    }

    public static Brand findById(Long id) {
        return find.byId(id.toString());
    }
}

