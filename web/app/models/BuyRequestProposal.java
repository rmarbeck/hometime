package models;

import play.data.validation.Constraints;

/**
 * Definition of an BuyRequestProposal
 */
public class BuyRequestProposal {
	
	public String brand;
	
	public String model;
	
	public String criteria;
	
	public String remark;

	public String expectedPrice;
	
	public String priceHigherBound;
	
	@Constraints.Required
	public String story;
	
	@Constraints.Required
	public String pack;
	
	@Constraints.Required
	public String timeframe;
	
	@Constraints.Required
	public Boolean outsideZone;
	
	public Boolean delayCanBeReduced;
	
	public String customerEmail;
	
	public String customerCity;
	
	public String action;
	
	public String infosGivenByCustomer1 = null;
	public String infosGivenByCustomer2 = null;
	public String infosGivenByCustomer3 = null;
	
	public String remark1 = null;
	public String remark2 = null;
	public String remark3 = null;
	
	public String partnerAsset = null;
	
	public String hypothesis1 = null;
	public String hypothesis2 = null;
	public String hypothesis3 = null;
	
	@Constraints.Required
	public String proposal1 = null;
	public String proposal2 = null;
	public String proposal3 = null;
	
	public BuyRequestProposal() {
		super();
	}
	
	public BuyRequestProposal(BuyRequest request) {
	    	this();
	    	this.brand = request.brand.display_name;
	    	this.model = request.model;
			this.criteria = request.criteria;
			this.infosGivenByCustomer1 = request.remark;
			this.expectedPrice = request.expectedPrice;
			this.priceHigherBound = request.priceHigherBound;
			this.story = request.story.toString();
			this.pack = request.pack.toString();
			this.timeframe = request.timeframe.toString();
			this.customerEmail = request.email;
			this.customerCity = request.city;
	    }
}


