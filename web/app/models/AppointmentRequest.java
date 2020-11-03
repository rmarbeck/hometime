package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import controllers.CrudReady;
import fr.hometime.utils.AppointmentOptionHelper;
import fr.hometime.utils.AppointmentRequestHelper;
import fr.hometime.utils.PhoneNumberHelper;
import fr.hometime.utils.RandomHelper;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Model;
import play.i18n.Messages;

/**
 * Definition of an appointment option
 */
@Entity
public class AppointmentRequest extends Model implements CrudReady<AppointmentRequest, AppointmentRequest> {
	private static final long serialVersionUID = 3872701757909665792L;
	
	private static AppointmentRequest singleton = null;
	
	public enum Reason {
	    DEPOSIT ("DEPOSIT"),
	    PICKUP ("PICKUP");
	    
		private String name = "";
		    
		Reason(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public static Reason fromString(String name) {
	        for (Reason type : Reason.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}

	public enum Status {
		WAITING_VALIDATION ("WAITING_VALIDATION"),
		VALIDATED ("VALIDATED"),
		CANCELED ("CANCELED"),
	    EXPIRED ("EXPIRED"),
	    NO_MORE_AVAILABLE ("NO_MORE_AVAILABLE"),
	    IN_ERROR ("IN_ERROR");
	    
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
	
	@Column(name="unique_key")
	public String uniqueKey;
	
	@Column(name="creation_date")
	public Date creationDate;
	
	@Column(name="last_action_date")
	public Date lastActionDate;
	
	@Column(name="end_of_option_date")
	public Date endOfOptionDate;
	
	@Column(name="appointment_as_date")
	public Date appointmentAsDate;
	
	@Constraints.Required
	@Column(name="appointment_as_string")
	public String appointmentAsString;
	
	@Constraints.Required
	@Column(name="customer_details")
	public String customerDetails;
	
	@Constraints.Required
	@Column(name="customer_phone_number")
	public String customerPhoneNumber;
	
	@Column(length = 10000, name="customer_remark")
	public String customerRemark;
	
	@Column(length = 10000, name="private_remark")
	public String privateRemark;
	
	@Enumerated(EnumType.STRING)
	@Column(name="appointment_status")
	public Status status;
	
	@Enumerated(EnumType.STRING)
	@Column(name="appointment_reason")
	public Reason reason;
		
	public AppointmentRequest() {
		
	}
		
	public static AppointmentRequest of() {
    	if (singleton == null)
    		singleton = new AppointmentRequest();
    	return singleton;
    }

    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,AppointmentRequest> find = new Model.Finder(String.class, AppointmentRequest.class);
    
    public static List<AppointmentRequest> findAll() {
        return find.all();
    }
    
    public static AppointmentRequest findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static AppointmentRequest findByKey(String key) {
    	return find.where().eq("unique_key", key).findUnique();
    }
    
    public static List<AppointmentRequest> findByAppointment(LocalDateTime appointmentAsLocalDatetime) {
    	return find.where().eq("appointmentAsDate", AppointmentOptionHelper.convertToDate(appointmentAsLocalDatetime)).findList();
    }
    
    public static List<AppointmentRequest> findByAppointment(Date appointmentAsDate) {
    	return find.where().eq("appointmentAsDate", appointmentAsDate).findList();
    }
    
    public static List<AppointmentRequest> findInFutureOnly() {
    	return find.where().gt("appointmentAsDate", new Date()).findList();
    }
    
    public boolean isValid() {
    	return this.status.equals(Status.VALIDATED);
    }
    
    public boolean isCanceled() {
    	return this.status.equals(Status.CANCELED);
    }
    
    public boolean isAvailableForNewOption() {
    	return this.status.equals(Status.CANCELED) || (this.status.equals(Status.WAITING_VALIDATION) && endOfOptionDate.before(new Date()));
    }
    
    public boolean notAvailableForNewOption() {
    	return !isAvailableForNewOption();
    }
    
    public boolean isInFuture() {
    	return this.appointmentAsDate.after(new Date());
    }
    
    public static Page<AppointmentRequest> page(int page, int pageSize, String sortBy, String order, String filter) {
    	if (sortBy == null || sortBy.equals("")) {
    		sortBy = "creation_date";
    		order = "DESC";
    	}
        return 
            find.where().or(Expr.ilike("customerDetails", "%" + filter + "%"), Expr.ilike("unique_key", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
    }

    
	@Override
	public void save() {
		this.uniqueKey = RandomHelper.getRandomAlphanumericString(5).toLowerCase();
		this.creationDate = new Date();
		this.status = Status.WAITING_VALIDATION;
		this.endOfOptionDate = Date.from(this.creationDate.toInstant().plusSeconds(3600)); 
		this.appointmentAsDate = AppointmentOptionHelper.convertToDateFromString(appointmentAsString);
		checkBeforeSaving();
		super.save();
	}

	@Override
	public void update() {
		checkBeforeSaving();
		super.update();
	}

	@Override
	public Model.Finder<String, AppointmentRequest> getFinder() {
		return find;
	}

	@Override
	public Page<AppointmentRequest> getPage(int page, int pageSize, String sortBy,
			String order, String filter) {
		return page(page, pageSize, sortBy, order, filter);
	}
	
	private void checkBeforeSaving() {
		if (this.appointmentAsDate != null)
			this.appointmentAsString = AppointmentRequestHelper.getAppointmentAsStringFromDate(this.appointmentAsDate);
	}
	
	public void updateAfterConfirmation() {
		this.lastActionDate = new Date();
		this.status = Status.VALIDATED;
		this.update();
	}
	
	public void updateAfterCancelation() {
		this.lastActionDate = new Date();
		this.status = Status.CANCELED;
		this.update();
	}
	
	public String getUniqueKey() {
		return uniqueKey;
	}
	
    public List<ValidationError> validate() {
    	List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!PhoneNumberHelper.isItAFrenchMobilePhoneNumber(customerPhoneNumber)) {
       		errors.add(new ValidationError("customerPhoneNumber", Messages.get("admin.appointment.request.phonenumber.invalid")));
        }
        return errors.isEmpty() ? null : errors;
    }
    
	public String getNiceDisplayableDatetime() {
		try {
			return LocalDateTime.parse(appointmentAsString, AppointmentOption.DATE_FORMATTER).format(AppointmentOption.DATE_FORMATTER_TO_DISPLAY);
		} catch(RuntimeException re) {
			return appointmentAsString;
		}
	}
}

