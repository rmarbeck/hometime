package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * Definition of a document provided by an external source (partner...)
 */
@SuppressWarnings("unused")
@Entity
public class ExternalDocument extends Model implements CrudReady<ExternalDocument, ExternalDocument> {
	private static final long serialVersionUID = 9172312438070646082L;
	private static ExternalDocument singleton = null;

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(unique=true)
	public String name;
	
	@Column(length = 10000)
	public String description;
	
	public String url;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public ExternalDocument() {
		this.creationDate = new Date();
	}
	
	public static ExternalDocument of() {
    	if (singleton == null)
    		singleton = new ExternalDocument();
    	return singleton;
    }
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,ExternalDocument> find = new Model.Finder(String.class, ExternalDocument.class);
    
    public static List<ExternalDocument> findAll() {
        return find.all();
    }
    
    public static List<ExternalDocument> findAllNewerFirst() {
        return find.orderBy("creationDate DESC").findList();
    }
    
    public static Page<ExternalDocument> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().or(Expr.ilike("description", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

	@Override
	public Finder<String, ExternalDocument> getFinder() {
		return find;
	}

    public static ExternalDocument findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static ExternalDocument findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

	@Override
	public Page<ExternalDocument> getPage(int page, int pageSize,
			String sortBy, String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	public String toString() {
		return name;
	}
}

