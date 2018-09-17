package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import com.avaje.ebean.PagedList;

/**
 * Definition of a Picture
 */
@Entity 
public class Picture extends Model {
	private static final long serialVersionUID = -7468362210071613032L;

	@Id
	public Long id;

	public String url;
	
	public String thumbnail_url;

	public String alt;
	
	@Column(length = 1000)
	public String description;

	@ManyToOne(cascade = CascadeType.PERSIST)
	public Watch watch;
	
	public Long display_order = 100L;
	
	public Picture(String url) {
		this.url = url;
	}
    
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Picture> find = new Model.Finder(String.class, Picture.class);
    
    public static List<Picture> findAll() {
        return find.all();
    }

    public static List<Picture> findPicturesForWatch(Watch watch) {
        return find.where().eq("watch", watch).orderBy("display_order ASC").findList();
    }

    public static Picture findById(Long id) {
        return find.byId(id.toString());
    }

    public static PagedList<Picture> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("title", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagedList(page, pageSize);
                
    }
}

