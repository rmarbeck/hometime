package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.Logger;
import play.data.validation.ValidationError;
import com.avaje.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;

/**
 * Definition of a Watch
 */
@Entity 
public class Watch extends Model {
	private static final long serialVersionUID = -3440171768196897297L;

	@Id
	public Long id;
	
	public boolean should_display = true;
	
	public boolean is_available = true;

	public String brand;
	
	public String full_name;
	
	public String short_name;
	
	public String seo_name;
	
	@Column(length = 1000)
	public String movement;
	
	@Column(length = 1000)
	public String case_infos;
	
	@Column(length = 1000)
	public String functions;
	
	public String water_resistance;
	
	@Column(length = 1000)
	public String specials;
	
	@Column(length = 1000)
	public String production_period;

	public String item_year;
	
	public String item_strap;
	
	public String item_reference;
	
	public Float price;
	
	public String main_picture_url;
	
	public String main_thumbnail_url;
	
	@Column(length = 10000)
	public String model_history;
	
	@Column(length = 10000)
	public String item_history;
	
	@Column(length = 10000)
	public String reason_why;
	
	public int number_of_loans = 0;
	
	public Float owning_cost;
	
	public Float lower_selling_price;
	
	public Float best_selling_price;
	
	@Column(name="acquisition_date")
	public Date acquisition_date;
	
	@OneToMany(mappedBy="watch", cascade = CascadeType.ALL)
	public List<Picture> pictures;

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Watch> find = new Model.Finder(String.class, Watch.class);
    
    public static List<Watch> findAll() {
        return find.all();
    }

    public static Watch findBySeoName(String seoName) {
        return find.where().eq("seo_name", seoName).findUnique();
    }
    
    public static List<Watch> findDisplayable() {
    	List<Watch> displayableWatches = find.where().eq("should_display", true).orderBy("id ASC").findList();
    	if (displayableWatches == null)
    		return new ArrayList<Watch>();
        return displayableWatches;
    }

    public static List<Watch> findAvailable() {
    	List<Watch> avalaibleWatches = find.where().eq("is_available", true).eq("should_display", true).orderBy("id ASC").findList();
    	if (avalaibleWatches == null)
    		return new ArrayList<Watch>();
        return avalaibleWatches;
    }

    public static Watch findById(Long id) {
        return find.byId(id.toString());
    }

    public static PagedList<Watch> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("full_name", "%" + filter + "%"), Expr.ilike("brand", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagedList(page, pageSize);
                
    }
    
	@Override
	public void save() {
		super.save();
	}


	@Override
	public void update() {
		try {
			Logger.debug("Updating "+this.getClass().getName());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		super.update();
	}
	
    public List<ValidationError> validate() {
    	List<ValidationError> errors = new ArrayList<ValidationError>();
        return errors.isEmpty() ? null : errors;
    }
    
}

