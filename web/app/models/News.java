package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

import com.avaje.ebean.Page;

import controllers.CrudReady;

/**
 * Definition of a News Article
 */
@Entity 
public class News extends Model implements CrudReady<News, News> {
	private static final long serialVersionUID = 3093720171808424551L;

	private static News singleton = null;
	
	public enum NewsType {
	    ONE_PICTURE ("ONE_PICTURE"),
	    VIDEO ("VIDEO"),
	    DIAPORAMA ("DIAPORAMA"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2");
	    
		private String name = "";
		    
		NewsType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static NewsType fromString(String name) {
	        for (NewsType type : NewsType.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@Column(length = 1000)
	public String title;
	
	@Column(length = 10000)
	public String body;
	
	@Column(name="news_type", length = 40)
	@Enumerated(EnumType.STRING)
	public NewsType type;
	
	public Date date;
	
	@ManyToMany(mappedBy="newsInThisCategory")
	public List<NewsCategory> categories;
	
	@OneToMany(mappedBy="news", cascade = CascadeType.ALL)
	public List<NewsUrl> previewUrl;
	
	@OneToMany(mappedBy="news", cascade = CascadeType.ALL)
	public List<NewsAlt> previewAlt;
	

	public String readMoreUrl;
	
	public boolean active = true;
	
	public String privateInfos;
	
	public News(String title) {
		this.title = title;
	}
	
	public News() {
		date = new Date();
	}
	
	public static News of() {
    	if (singleton == null)
    		singleton = new News();
    	return singleton;
    }
    
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,News> find = new Model.Finder(String.class, News.class);
    
    public static List<News> findAll() {
        return find.all();
    }

    public static News findById(Long id) {
        return find.byId(id.toString());
    }

	@Override
    public Page<News> getPage(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("title", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

	@Override
	public Model.Finder<String, News> getFinder() {
		return find;
	}
}

