package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

import com.avaje.ebean.Page;


/**
 * Definition of a News Item
 */
@Entity
public class NewsAlt extends Model {
	private static final long serialVersionUID = 1141388675389465763L;

	@Id
	public Long id;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	public News news;
	
	public String value;
	
	public NewsAlt(News news, String value) {
		this.news = news;
		this.value = value;
	}
    
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,NewsAlt> find = new Model.Finder(String.class, NewsAlt.class);
    
    public static List<NewsAlt> findAll() {
        return find.all();
    }

    public static List<NewsAlt> findPicturesForWatch(Watch watch) {
        return find.where().eq("watch", watch).orderBy("display_order ASC").findList();
    }

    public static NewsAlt findById(Long id) {
        return find.byId(id.toString());
    }

    public static Page<NewsAlt> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("value", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
    
	public String toString() {
		return value;
	}
}

