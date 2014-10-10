package models;

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

import play.db.ebean.Model;

/**
 * Definition of an Order
 */
@Entity
@Table(name = "order_table")
public class Order extends Model {
	private static final long serialVersionUID = 1432152323100975220L;
	
	public enum OrderStatus {
	    INFORMAL_AGREEMENT ("10"),
	    FORMAL_AGREEMENT ("20"),
	    PICK_UP_PLANNED ("30"),
	    PREPACKAGE_SENT ("32"),
	    PREPACKAGE_RECEIVED ("34"),
	    WATCH_SENT ("36"),
	    PICK_UP_DONE ("40"),
	    WATCH_RECEIVED ("42"),
	    NEW_INFORMAL_PROPOSAL_SUBMITED ("50"),
	    NEW_FORMAL_PROPOSAL_SUBMITED ("55"),
	    PROPOSAL_AGREED ("60"),
	    WORK_STARTED ("70"),
	    WORK_ISSUE_WAITING_INTERNAL_SOLUTION ("75"),
	    WORK_ISSUE_WAITING_CUSTOMER ("77"),
	    WORK_FINISHED ("80"),
	    WATCH_CONTROL ("90"),
	    REWORK ("92"),
	    BILL_SENT ("100"),
	    BILL_PAYED ("110"),
	    RETURN_PLANNED ("120"),
	    RETURN_DONE ("130"),
	    WATCH_SENT_BACK ("132"),
	    WATCH_RECEIVED_BACK ("134"),
	    FEEDBACK_ASKED ("140"),
	    FEEDBACK_RECEIVED ("150"),
	    ORDER_CLOSED ("200"),
	    ORDER_CANCELED ("300");
	    
		private String name = "";
		    
		OrderStatus(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
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
	
	public Date creationDate;
	
	public Date pickUpRealDate;
	
	public Date startWorkingRealDate;
	
	public Date endOfWorkRealDate;
	
	public Date startOfControlDate;
	
	public Date returnRealDate;
	
	public Date closingDate;
	
	@Column(name="last_comm_date")
	public Date lastCommunicationForThisOrderDate;

	public OrderRequest.OrderTypes orderType;
	
	@Column(name="order_status", length = 40)
	@Enumerated(EnumType.STRING)
	public OrderStatus status;

	public String brand;
	
	@Column(length = 1000)
	public String model;
	
	@Column(name="order_method")
	public OrderRequest.MethodTypes method;
	
	@Column(length = 1000)
	public String remark;
	
	public String watchChosen = null;

	@ManyToOne
	public Customer customer;
	
	public Order(Customer customer) {
		this.customer = customer;
		this.creationDate = new Date();
		this.status = OrderStatus.INFORMAL_AGREEMENT;
	}
	
	public Order(String emailOfNewCustomer) {
		this(Customer.getOrCreateCurstomerForEmail(emailOfNewCustomer));
	}
	
	public Order(OrderRequest request) {
		this(Customer.getOrCreateCurstomerForOrderRequest(request));
		this.request = request;
		this.brand = request.brand.display_name;
		this.model = request.model;
		this.orderType = request.orderType;
		this.method = request.method;
		this.remark = request.remark;
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
        return find.where().ne("status", OrderStatus.ORDER_CLOSED).orderBy("requestDate DESC").findList();
    }
    
    public static List<Order> findAllClosed() {
        return find.where().eq("status", OrderStatus.ORDER_CLOSED).orderBy("requestDate DESC").findList();
    }

    public static Order findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static Page<Order> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
	        find.where().or(Expr.ilike("customer.email", "%" + filter + "%"), Expr.ilike("customer.name", "%" + filter + "%"))
	            .orderBy(sortBy + " " + order)
	            .findPagingList(pageSize)
	            .getPage(page);
    }
}

