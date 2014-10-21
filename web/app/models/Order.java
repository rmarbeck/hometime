package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import play.Logger;
import play.data.validation.ValidationError;
import play.db.ebean.Model;

/**
 * Definition of an Order
 */
@Entity
@Table(name = "order_table")
public class Order extends Model {
	private static final long serialVersionUID = 1432152323100975220L;
	
	public enum OrderStatus {
	    INFORMAL_AGREEMENT_10 ("INFORMAL_AGREEMENT_10"),
	    FORMAL_AGREEMENT_20 ("FORMAL_AGREEMENT_20"),
	    PICK_UP_PLANNED_30 ("PICK_UP_PLANNED_30"),
	    PREPACKAGE_SENT_32 ("PREPACKAGE_SENT_32"),
	    PREPACKAGE_RECEIVED_34 ("PREPACKAGE_RECEIVED_34"),
	    WATCH_SENT_36 ("WATCH_SENT_36"),
	    PICK_UP_DONE_40 ("PICK_UP_DONE_40"),
	    WATCH_RECEIVED_42 ("WATCH_RECEIVED_42"),
	    NEW_INFORMAL_PROPOSAL_SUBMITED_50 ("NEW_INFORMAL_PROPOSAL_SUBMITED_50"),
	    NEW_FORMAL_PROPOSAL_SUBMITED_55 ("NEW_FORMAL_PROPOSAL_SUBMITED_55"),
	    PROPOSAL_AGREED_60 ("PROPOSAL_AGREED_60"),
	    WORK_STARTED_70 ("WORK_STARTED_70"),
	    WORK_ISSUE_WAITING_INTERNAL_SOLUTION_75 ("WORK_ISSUE_WAITING_INTERNAL_SOLUTION_75"),
	    WORK_ISSUE_WAITING_CUSTOMER_77 ("WORK_ISSUE_WAITING_CUSTOMER_77"),
	    WORK_FINISHED_80 ("WORK_FINISHED_80"),
	    WATCH_CONTROL_90 ("WATCH_CONTROL_90"),
	    REWORK_92 ("REWORK_92"),
	    BILL_SENT_100 ("BILL_SENT_100"),
	    BILL_PAYED_110 ("BILL_PAYED_110"),
	    RETURN_PLANNED_120 ("RETURN_PLANNED_120"),
	    RETURN_DONE_130 ("RETURN_DONE_130"),
	    WATCH_SENT_BACK_132 ("WATCH_SENT_BACK_132"),
	    WATCH_RECEIVED_BACK_134 ("WATCH_RECEIVED_BACK_134"),
	    FEEDBACK_ASKED_140 ("FEEDBACK_ASKED_140"),
	    FEEDBACK_RECEIVED_150 ("FEEDBACK_RECEIVED_150"),
	    ORDER_CLOSED_200 ("ORDER_CLOSED_200"),
	    ORDER_CANCELED_300 ("ORDER_CANCELED_300");
	    
		private String name = "";
		    
		OrderStatus(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}

		public static OrderStatus fromString(String name) {
	        for (OrderStatus status : OrderStatus.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@OneToOne
	public OrderRequest request;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	public Date pickUpRealDate;
	
	public Date startWorkingRealDate;
	
	public Date endOfWorkRealDate;
	
	public Date startOfControlDate;
	
	public Date returnRealDate;
	
	public Date closingDate;
	
	@Column(name="last_comm_date")
	public Date lastCommunicationForThisOrderDate;

	public String orderType;
	
	@Column(name="order_status", length = 40)
	@Enumerated(EnumType.STRING)
	public OrderStatus status;

	public String brand;
	
	@Column(length = 1000)
	public String model;
	
	@Column(name="order_method")
	public String method;
	
	@Column(length = 1000)
	public String remark;
	
	public String watchChosen = null;

	@ManyToOne
	public Customer customer;
	
	public Order(Customer customer) {
		this.customer = customer;
		this.creationDate = new Date();
		this.status = OrderStatus.INFORMAL_AGREEMENT_10;
	}
	
	public Order(String emailOfNewCustomer) {
		this(Customer.getOrCreateCustomerForEmail(emailOfNewCustomer));
	}
	
	public Order(OrderRequest request) {
		this(Customer.getOrCreateCustomerFromOrderRequest(request));
		this.request = request;
		this.brand = request.brand.display_name;
		this.model = request.model;
		this.orderType = request.orderType.toString();
		this.method = request.method.toString();
		this.remark = request.remark;
		if (request.watchChosen != null)
			this.watchChosen = request.watchChosen.full_name;
	}

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,Order> find = new Model.Finder(String.class, Order.class);
    
    public static List<Order> findAll() {
        return find.all();
    }
    
    public static List<Order> findAllLatestFirst() {
        return find.orderBy("requestDate DESC").findList();
    }
    
    public static List<Order> findAllOpen() {
        return find.where().ne("status", OrderStatus.ORDER_CLOSED_200).orderBy("requestDate DESC").findList();
    }
    
    public static List<Order> findAllClosed() {
        return find.where().eq("status", OrderStatus.ORDER_CLOSED_200).orderBy("requestDate DESC").findList();
    }

    public static Order findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<Order> findByCustomer(models.Customer customer) {
    	return find.where().eq("customer.id", customer.id)
        			.orderBy("creation_date desc").findList();
    }
    
    public static Page<Order> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().or(Expr.ilike("customer.email", "%" + filter + "%"), Expr.ilike("customer.name", "%" + filter + "%"))
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

