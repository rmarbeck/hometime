package models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.Quotation.TypesOfNetwork;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import fr.hometime.payment.systempay.PaymentConfirmation;
import fr.hometime.utils.DateHelper;
import fr.hometime.utils.UniqueAccountingNumber;

/**
 * Definition of a payment request
 */
@Entity
public class PaymentRequest extends Model implements CrudReady<PaymentRequest, PaymentRequest> {
	private static final long serialVersionUID = 4049569452958427009L;

	private static PaymentRequest singleton = null;

	public enum PaymentSolution {
	    SYSTEM_PAY ("SYSTEM_PAY"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2");
	    
		private String name = "";
		    
		PaymentSolution(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static PaymentSolution fromString(String name) {
	        for (PaymentSolution status : PaymentSolution.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum PaymentType {
	    ONE_SHOT_IMMEDIATE ("ONE_SHOT_IMMEDIATE"),
	    ONE_SHOT_POSTPONED ("ONE_SHOT_POSTPONED"),
	    MULTI ("MULTI"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2"),
	    RESERVED_3 ("RESERVED_3"),
	    RESERVED_4 ("RESERVED_4");
	    
		private String name = "";
		    
		PaymentType(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static PaymentType fromString(String name) {
	        for (PaymentType status : PaymentType.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public enum Status {
	    OPEN ("OPEN"),
	    EXPIRED ("EXPIRED"),
	    PENDING ("PENDING"),
	    CANCELED ("CANCELED"),
	    VALIDATED_NO_WARNING ("VALIDATED_NO_WARNING"),
	    VALIDATED_WITH_WARNING ("VALIDATED_WITH_WARNING"),
	    IN_ERROR ("IN_ERROR"),
	    RESERVED_1 ("RESERVED_1"),
	    RESERVED_2 ("RESERVED_2"),
	    RESERVED_3 ("RESERVED_3"),
	    RESERVED_4 ("RESERVED_4");
	    
		private String name = "";
		    
		Status(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static Status fromString(String name) {
	        for (Status status : Status.values()) {
	            if (status.name.equals(name)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	@Id
	public Long id;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(name="first_try_date")
	public Date firstTryDate;
	
	@Column(name="last_try_date")
	public Date lastTryDate;
	
	@Column(name="closing_date")
	public Date closingDate;
	
	@Column(name="valid_until_date")
	public Date validUntilDate;

	@Column(name="price_in_euros")
	public float priceInEuros;
	
	@Column(name="access_key")
	public String accessKey;
	
	@ManyToOne
	public Customer customer;
	
	@Column(name="order_number")
	public String orderNumber;
	
	@Column(length = 255)
	public String description;
	
	@Column(length = 255)
	public String description2;
	
	@Column(name="is_open")
	public Boolean isOpen;
	
	@Column(name="allow_amex")
	public Boolean allowAmex;
	
	@Column(name="amex_only")
	public Boolean amexOnly;
	
	@Constraints.Required
	@Enumerated(EnumType.STRING)
	@Column(name="solution_to_use")
	public PaymentSolution solutionToUse;
	
	@Constraints.Required
	@Enumerated(EnumType.STRING)
	@Column(name="type_of_payment")
	public PaymentType typeOfPayment;
	
	@Constraints.Required
	@Enumerated(EnumType.STRING)
	@Column(name="request_status")
	public Status requestStatus;
	
	@Column(length = 10000)
	public String statusInfo;
	
	@Column(name="delay_in_days")
	public int delayInDays = 0;
		
	public PaymentRequest() {
		
	}
	
	public PaymentSolution getSolutionToUse() {
		return solutionToUse;
	}
	
	public void setSolutionToUse(String name) {
		solutionToUse = PaymentSolution.fromString(name);
	}
	
	public PaymentType getTypeOfPayment() {
		return typeOfPayment;
	}
	
	public void setTypeOfPayment(String name) {
		typeOfPayment = PaymentType.fromString(name);
	}
	
	public static PaymentRequest of() {
    	if (singleton == null)
    		singleton = new PaymentRequest();
    	return singleton;
    }

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,PaymentRequest> find = new Model.Finder(String.class, PaymentRequest.class);
    
    public static List<PaymentRequest> findAll() {
        return find.all();
    }
    
    public static PaymentRequest findById(Long id) {
        return find.byId(id.toString());
    }
    
    public boolean isValid() {
    	return this.isOpen && DateHelper.isAfterNow(getEnOfTheDayValidityDate()) && this.requestStatus.equals(Status.OPEN);
    }
    
    private Date getEnOfTheDayValidityDate() {
    	return DateHelper.toDate(DateHelper.endOfTheDay(validUntilDate));
    }
    
    public static Page<PaymentRequest> page(int page, int pageSize, String sortBy, String order, String filter) {
    	if (sortBy == null || sortBy.equals("")) {
    		sortBy = "creation_date";
    		order = "DESC";
    	}
        return 
            find.where().or(Expr.ilike("description", "%" + filter + "%"), Expr.ilike("access_key", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

    
	@Override
	public void save() {
		this.creationDate = new Date();
		checkBeforeSaving();
		super.save();
	}

	@Override
	public void update() {
		checkBeforeSaving();
		super.update();
	}

	@Override
	public Model.Finder<String, PaymentRequest> getFinder() {
		return find;
	}

	@Override
	public Page<PaymentRequest> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private void checkBeforeSaving() {
		if (this.customer != null)
			this.customer = Customer.findByEmail(this.customer.email);
	}
	
	public String getFullName() {
		if (this.customer != null)
			return this.customer.getFullName();
		return "unknown";
	}
	
	public void updateAfterConfirmationResult(PaymentConfirmation confirmation) {
		this.statusInfo = confirmation.toString();
		if (!confirmation.containsWarnings() && confirmation.getEffectiveAmount() == this.priceInEuros * 100) {
			this.requestStatus = Status.VALIDATED_NO_WARNING;
			this.closingDate = new Date();
		} else if (confirmation.isTransactionAuthorized()){
			this.requestStatus = Status.VALIDATED_WITH_WARNING;
			this.closingDate = new Date();
		} else {
			this.requestStatus = Status.IN_ERROR;
			this.lastTryDate = new Date();
		}
		this.update();
	}
	
	public void setStatus(Status newStatus) {
		this.requestStatus = newStatus;
		switch (newStatus) {
			case VALIDATED_NO_WARNING:
			case VALIDATED_WITH_WARNING:
				this.closingDate = new Date();
				this.isOpen = false;
				break;
			case IN_ERROR:
				this.lastTryDate = new Date();
				break;
			case CANCELED:
			case EXPIRED:
				this.isOpen = false;
				break;
			case OPEN:
				this.isOpen = true;
				break;
		default:
			break;
		}
	}
	
	public static PaymentRequest getDefaultValues() {
		PaymentRequest instance = PaymentRequest.of();
		instance.delayInDays = 1;
		instance.isOpen = true;
		instance.validUntilDate = Date.from(Instant.now().plus(15, ChronoUnit.DAYS));
		instance.typeOfPayment = PaymentType.ONE_SHOT_IMMEDIATE;
		instance.solutionToUse = PaymentSolution.SYSTEM_PAY;
		instance.requestStatus = Status.OPEN;
		instance.orderNumber = UniqueAccountingNumber.getLastForOrders().toString();
		instance.allowAmex = false;
		instance.amexOnly = false;
		return instance;
	}
	
	public Customer getCustomer() {
		return customer;
	}
}

