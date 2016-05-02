package fr.hometime.payment.systempay;

public class DataDictionnary {
	public static final String PARAMETER_PREFIX = "vads_";
	
	public static final String SIGNATURE = "signature";
	
	public static final String SITE_ID = "site_id";
	public static final String CTX_MODE = "ctx_mode";
	public static final String TRANS_ID = "trans_id";
	public static final String ORDER_ID = "order_id";
	public static final String TRANS_DATE = "trans_date";
	public static final String AMOUNT = "amount";
	public static final String CURRENCY_CODE = "currency";
	public static final String ACTION_MODE = "action_mode";
	public static final String PAGE_ACTION = "page_action";
	public static final String VERSION = "version";
	public static final String PAYMENT_CONFIG = "payment_config";
	public static final String CAPTURE_DELAY = "capture_delay";
	public static final String VALIDATION_MODE = "validation_mode";
	
	public static final String RESULT = "result";
	public static final String RESULT_OK = "00";
	public static final String WARRANTY_RESULT = "warranty_result";
	public static final String WARRANTY_RESULT_OK = "YES";
	public static final String EFFECTIVE_AMOUNT = "effective_amount";
	public static final String TRANSACTION_STATUS = "trans_status";
	public static final String TRANSACTION_STATUS_OK_PATTERN = "AUTHORISED";
	
	public static final String CTX_MODE_TEST = "TEST";
	public static final String CTX_MODE_PRODUCTION = "PRODUCTION";
	
	public static final String ACTION_MODE_INTERACTIVE = "INTERACTIVE";
	
	public static final String PAGE_ACTION_PAYMENT = "PAYMENT";
	
	public static final String CURRENT_VERSION = "V2";
	
	public static final String PAYMENT_CONFIG_SINGLE = "SINGLE";
	public static final String PAYMENT_CONFIG_MULTI = "MULTI";
	
	public static final int CURRENCY_CODE_EURO = 978;
	
	public static final int AUTOMATIC_VALIDATION_MODE = 0;
	
	
	public static final String ORDER_INFO_1 = "order_info";
	public static final String ORDER_INFO_2 = "order_info2";
	
	public static final String PAYMENT_CARDS = "payment_cards";
	public static final String PAYMENT_CARDS_AMEX = "AMEX";
	public static final String PAYMENT_CARDS_CB = "CB";
	public static final String PAYMENT_CARDS_E_CB = "E-CARTEBLEUE";
	public static final String PAYMENT_CARDS_MAESTRO = "MAESTRO";
	public static final String PAYMENT_CARDS_MASTERCARD = "MASTERCARD";
	public static final String PAYMENT_CARDS_VISA = "VISA";
	public static final String PAYMENT_CARDS_VISA_ELECTRON = "VISA_ELECTRON";
	
	
	public static final String PAYMENT_CARDS_DEFAULT = PAYMENT_CARDS_AMEX+";"+PAYMENT_CARDS_CB+";"+PAYMENT_CARDS_E_CB+";"+PAYMENT_CARDS_MAESTRO+";"+PAYMENT_CARDS_MASTERCARD+";"+PAYMENT_CARDS_VISA+";"+PAYMENT_CARDS_VISA_ELECTRON;
	
	public static final String SHOP_NAME = "shop_name";
	public static final String SHOP_NAME_DEFAULT = "Watch Next";
}
