package controllers;

import java.util.List;

import models.InvoiceLineReport;
import models.LegalRegisterReport;
import models.MarginVatReport;
import models.PaymentsReport;
import models.StockReport;
import models.Invoice.InvoiceType;
import models.WatchSalesReport;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.admin.reports.index;
import views.html.admin.reports.stock;
import views.html.admin.reports.legal_register;
import views.html.admin.reports.invoice_lines;
import views.html.admin.reports.address_tab;
import views.html.admin.reports.address_tab_alpha;
import views.html.admin.reports.payments;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class Reporting extends Controller {
	public static Result index() {
		return ok(index.render(WatchSalesReport.generateReport(invoice -> InvoiceType.MARGIN_VAT.equals(invoice.type))));
    }
	
	public static Result exportSales() {
		return ok(index.render(WatchSalesReport.generateReport(invoice -> InvoiceType.RESERVED_1.equals(invoice.type))));
    }
	
	public static Result invoiceLines() {
		return ok(invoice_lines.render(InvoiceLineReport.generateReport()));
    }
	
	public static Result stock() {
		return ok(stock.render(StockReport.generateReport()));
    }
	
	public static Result legalRegister() {
		return ok(legal_register.render(LegalRegisterReport.generateReport()));
    }
	
	public static Result addresses() {
		return ok(address_tab.render(getCustomers()));
    }
	
	public static Result addressBook() {
		return ok(address_tab_alpha.render(getAddressBook()));
    }
	
	public static Result payments() {
		return ok(payments.render(PaymentsReport.generateReport()));
    }
	
	private static List<models.Customer> getCustomers() {
		/*List<models.Customer> customers = new ArrayList<models.Customer>();
		for(int index = 0; index <10; index++) {
			customers.addAll(models.Customer.findPrintableByDescId());
		}*/
		return models.Customer.findPrintableByDescId();
	}
	
	private static List<models.Customer> getAddressBook() {
		return models.Customer.findByNameAsc();
	}
}
