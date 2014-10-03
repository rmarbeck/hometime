package models;

/**
 * Definition of an Quotation
 */
public class Quotation {

	public enum TypesOfNetwork {
	    IN_BOTH ("1"),
	    IN_ONLY ("2"),
	    OUT_BOTH ("3"),
	    OUT_ONLY ("4");
	    
		private String name = "";
		    
		TypesOfNetwork(String name){
		    this.name = name;
		}

		public String toString(){
		    return name;
		}
		
		public int intValue() {
			return Integer.valueOf(name);
		}
		
		public static TypesOfNetwork fromString(String name) {
	        for (TypesOfNetwork type : TypesOfNetwork.values()) {
	            if (type.name.equals(name)) {
	                return type;
	            }
	        }
	        throw new IllegalArgumentException("Illegal type name: " + name);
	    }
	}
	
	public OrderRequest.OrderTypes serviceType;
	
	public TypesOfNetwork typeOfNetwork;

	public String watch;
	
	public String brand;
	
	public String customerEmail;
	
	public String customerCity;
	
	public String watchChosen = null;

	public String delay;
	
	public String delayBrand1;
	
	public String delayBrand2;
	
	public String delayReturn;

	public String price;
	
	public String priceService;
	
	public String priceLoan;
	
	public String warantyTime;
	
	public Boolean priceIsNotFinal;
	
	public Boolean delayIsNotSure;
	
	public Boolean outsideZone;
	
	public Boolean delayCanBeReduced;
	
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
	
	public Quotation() {
		super();
	}

    
    public String toString() {
    	StringBuilder content = new StringBuilder();
    	content.append(this.getClass().getSimpleName() + " : [");
    	content.append(" Type is : " + this.serviceType.name());
    	content.append(", Brand is : " + this.brand);
    	content.append(", for model : " + this.watch);
    	content.append(". Watch chosen : " + ((this.watchChosen==null)?("none"):(this.watchChosen)));
    	content.append("]");
    	return content.toString();
    }
}


