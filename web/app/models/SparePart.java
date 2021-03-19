package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.CrudReady;
import fr.hometime.utils.DateHelper;
import fr.hometime.utils.ListenableModel;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * Definition of a Spare Part associated belonging to a customer watch
 */
@SuppressWarnings("unused")
@Entity 
public class SparePart extends ListenableModel implements CrudReady<SparePart, SparePart> {
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
	
	@Column(name="order_date")
	public Date orderDate;
	
	@Column(name="reception_date")
	public Date receptionDate;
	
	public Date lastStatusUpdate;
	
	@Column(length = 10000)
	public String description;
	
	@Column(length = 10000)
	public String otherInfos;
	
	public String partReference;
	
	public String provider;
	
	@Constraints.Required
	public Integer unitNeeded = 1;
	
	@Constraints.Required
	public Long expectedInPrice = 0L;
	
	@Constraints.Required
	public Long realInPrice = 0L;
	
	@Constraints.Required
	public Long outPrice = 0L;
	
	public boolean found = false;
	
	public boolean paid = false;
	
	public boolean confirmed = false;
	
	public boolean inStock = false;
	
	public boolean toOrder = false;
	
	public boolean ordered = false;
	
	public boolean got = false;
	
	public boolean checkedOK = false;
	
	public boolean checkedKO = false;
	
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

    public static List<SparePart> findAllJustCreatedByCreationDateDesc() {
        return find.where().conjunction().eq("toOrder", false).eq("closed", false).orderBy("creationDate DESC").findList();
    }
    
    public static List<SparePart> findAllToOrderByCreationDateDesc() {
        return find.where().conjunction().eq("toOrder", true).eq("ordered", false).orderBy("creationDate DESC").findList();
    }
    
    public static List<SparePart> findAllToReceiveByCreationDateDesc() {
        return find.where().conjunction().eq("ordered", true).eq("got", false).orderBy("creationDate DESC").findList();
    }
    
    public static List<SparePart> findAllToCheckByCreationDateDesc() {
        return find.where().conjunction().eq("closed", false).eq("got", true).eq("checkedOK", false).eq("checkedKO", false).orderBy("creationDate DESC").findList();
    }
    
    public static List<SparePart> findAllToCloseByCreationDateDesc() {
        return find.where().conjunction().eq("checkedOK", true).eq("closed", false).orderBy("creationDate DESC").findList();
    }
        
    public static SparePart findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<SparePart> findAllByCustomerWatch(CustomerWatch watch) {
    	return find.where().eq("watch.id", watch.id).orderBy("creationDate DESC").findList();
    }
    
    public static List<SparePart> findAllOpenByCustomerWatch(CustomerWatch watch) {
    	return find.where().conjunction().eq("watch.id", watch.id).eq("closed", false).orderBy("creationDate DESC").findList();
    }
    
    public static boolean hasOpenSparePartsByCustomerWatch(CustomerWatch watch) {
    	return findAllOpenByCustomerWatch(watch).size() != 0;
    }
    
    public static List<SparePart> findAllByCustomerWatchToOrder(CustomerWatch watch) {
    	return find.where().conjunction().eq("watch.id", watch.id).eq("toOrder", true).orderBy("creationDate DESC").findList();
    }
    
    public static List<SparePart> findAllByCustomerWatchToUse(CustomerWatch watch) {
    	return find.where().conjunction().eq("watch.id", watch.id).eq("confirmed", true).orderBy("creationDate DESC").findList();
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
	
	public String getWatchId() {
		if (watch != null)
			return "#"+watch.id.toString();
		return "?";
	}
	
	public boolean isToBeChecked() {
		return got && !checkedKO && !checkedOK;
	}
	
	public boolean isToBeOrdered() {
		return toOrder && !ordered;
	}
	
	public boolean isNotAcceptedYet() {
		return !toOrder && !closed;
	}
	
	
	public String getOpenStatus() {
		if (closed)
			return "spare_closed";
		if (found)
			return "spare_open_found";
		return "spare_open";
	}
	
	public String getDetailedStatus() {
		if (closed)
			return "spare_closed";
		if (checkedOK)
			return "spare_OK";
		if (checkedKO)
			return "spare_KO";
		if (got)
			return "spare_to_check";
		if (ordered)
			return "spare_ordered";
		if (toOrder)
			return "spare_to_order";
		if (confirmed)
			return "spare_confirmed";

		return "spare_open";
	}

    @Override
    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("id", getWatchId())
        	.put("watch", getWatchDescription())
        	.put("description", description)
        	.put("reference", partReference);

        return json;
    }
}

