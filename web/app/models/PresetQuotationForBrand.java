package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Definition of a Preset Quotation for a brand
 */
@Entity
public class PresetQuotationForBrand extends Model {
	private static final long serialVersionUID = 4105976197705116592L;

	@Id
	public Long id;
	
	@Constraints.Required
    @Formats.NonEmpty
	public String presetName;
	
	@ManyToOne
	public Brand brand;
	
	public String delay;
	
	public String delayBrand1;
	
	public String delayBrand2;
	
	public String delayReturn;

	public Long priceServiceLowBound;
	public Long priceServiceHighBound;
	
	public String warantyTime;
	
	public Boolean priceIsNotFinal = true;
	
	public Boolean delayIsNotSure = true;
	
	public Boolean delayCanBeReduced = false;
	
	public Boolean shouldTalkAboutWaterResistance = true;
	
	public String remark1 = null;
	public String remark2 = null;
	public String remark3 = null;
	
	public String hypothesis1 = null;
	public String hypothesis2 = null;
	public String hypothesis3 = null;
	
	public PresetQuotationForBrand() {
		super();
	}
	
    // -- Queries
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Model.Finder<String,PresetQuotationForBrand> find = new Model.Finder(String.class, PresetQuotationForBrand.class);
    
    public static List<PresetQuotationForBrand> findAll() {
        return find.all();
    }
    
    public static PresetQuotationForBrand findById(Long id) {
        return find.byId(id.toString());
    }
    
    public static List<PresetQuotationForBrand> findByBrand(Brand brand) {
        return find.where().eq("brand", brand).findList();
    }
    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	content.append(" Preset is called : " + this.presetName);
    	content.append(", Brand is : " + this.brand);
    	content.append("]");
    	return content.toString();
    }
}


