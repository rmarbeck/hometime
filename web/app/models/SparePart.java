package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import play.db.ebean.Model;

/**
 * Definition of a Spare Part associated belonging to a customer watch
 */
@Entity 
public class SparePart extends Model implements CrudReady<SparePart, SparePart> {
	private static final long serialVersionUID = 1423684174998061395L;

	private static SparePart singleton = null;
	
	public static SparePart of() {
    	if (singleton == null)
    		singleton = new SparePart();
    	return singleton;
    }
	
	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
		
	@Column(name="expected_availability_date")
	public Date expectedAvailability;
	
	public Date lastStatusUpdate;
	
	@Column(length = 10000)
	public String description;
	
	@Column(length = 10000)
	public String otherInfos;
	
	public Integer unitNeeded = 1;
	
	public Long expectedInPrice = 0L;
	
	public Long realInPrice = 0L;
	
	public Long outPrice = 0L;
	
	public boolean found = false;
	
	public boolean got = false;
	
	public boolean closed = false;
	
	public Long emergencyLevel = 0L;
		
    @ManyToOne
	public CustomerWatch watch;

	public SparePart() {
		this.creationDate = new Date();
		this.lastStatusUpdate = creationDate;
	}
	
	public SparePart(CustomerWatch watch) {
		this();
		this.watch = watch;
	}
	
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,SparePart> find = new Model.Finder(String.class, SparePart.class);
    
    public static List<SparePart> findAll() {
        return find.all();
    }
    
    public static List<SparePart> findAllByCreationDateAsc() {
        return find.where().orderBy("creationDate ASC").findList();
    }
    
    public static List<SparePart> findAllByCreationDateDesc() {
        return find.where().orderBy("creationDate DESC").findList();
    }
        
    public static SparePart findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Page<SparePart> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where().ilike("description", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }
	@Override
	public void save() {
		this.creationDate = new Date();
		super.save();
	}

	@Override
	public void update() {
		this.lastStatusUpdate = new Date();
		super.update();
	}
	

	@Override
	public Finder<String, SparePart> getFinder() {
		return find;
	}

	@Override
	public Page<SparePart> getPage(int page, int pageSize, String sortBy, String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	public static Page<SparePart> pageOfOpenSpareParts(int page, int pageSize, String sortBy, String order, String filter) {
        return 
                find.where().and(Expr.ilike("description", "%" + filter + "%"), Expr.eq("closed", false))
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .getPage(page);
	}
	
	
	public String getWatchDescription() {
		if (watch != null)
			return watch.getDisplayName();
		return "?";
	}
	
	public String getOpenStatus() {
		if (closed)
			return "spare_closed";
		if (found)
			return "spare_open_found";
		return "spare_open";
	}
}

