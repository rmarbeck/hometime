package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.ebean.Model;

import com.avaje.ebean.Page;

import controllers.CrudReady;

/**
 * Definition of a News Item
 */
@Entity
public class NewsCategory extends Model implements CrudReady<NewsCategory, NewsCategory> {
	private static final long serialVersionUID = -4296685139773542614L;

	private static NewsCategory singleton = null;
	
	@Id
	public Long id;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	public List<News> newsInThisCategory;
	
	@Column(length = 1000)
	public String name;
	
	@Column(length = 10000)
	public String description;
	
	public NewsCategory() {
	}
	
	public NewsCategory(String name) {
		this.name = name;
	}
	
	public static NewsCategory of() {
    	if (singleton == null)
    		singleton = new NewsCategory();
    	return singleton;
    }
    
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,NewsCategory> find = new Model.Finder(String.class, NewsCategory.class);
    
    public static List<NewsCategory> findAll() {
        return find.all();
    }

    public static NewsCategory findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<NewsCategory> getAllByNameAsc() {
    	return find.where().orderBy("name ASC").findList();
    }

    public static Page<NewsCategory> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

	@Override
	public Finder<String, NewsCategory> getFinder() {
		return find;
	}

	@Override
	public Page<NewsCategory> getPage(int page, int pageSize, String sortBy, String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	public String toString() {
		return name;
	}
}

