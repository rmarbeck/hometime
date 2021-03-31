package controllers;

import java.util.List;
import java.util.function.Predicate;

import fr.hometime.utils.UniqueAccountingNumber;
import models.LegalRegisterReport;
import models.StockReport;
import models.Watch;
import models.WatchToSell;
import models.Invoice.InvoiceType;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import reporting.AnalyticsReport;
import reporting.AnalyticsReportEnhanced;
import reporting.AnalyticsReportEnhanced2;
import reporting.AnalyticsChartsReport;
import reporting.CurrentOrdersReport;
import reporting.AnalyticsDetailedReport;
import reporting.AnalyticsReportEnhanced2;
import reporting.InvoiceLineReport;
import reporting.InvoicesReport;
import reporting.MesurableReport;
import reporting.PaymentsReport;
import reporting.PossibleWatchesJustGoneReport;
import reporting.ReportWithStats;
import reporting.WatchSalesReport;
import views.html.admin.reports.margin_vat;
import views.html.admin.reports.with_vat;
import views.html.admin.reports.export_sales;
import views.html.admin.reports.stock;
import views.html.admin.reports.legal_register;
import views.html.admin.reports.legal_register_helper;
import views.html.admin.reports.invoice_lines;
import views.html.admin.reports.invoices;
import views.html.admin.reports.address_tab;
import views.html.admin.reports.address_tab_table;
import views.html.admin.reports.address_tab_alpha;
import views.html.admin.reports.payments;
import views.html.admin.reports.financial_report;
import views.html.admin.reports.financial_report2;
import views.html.admin.reports.financial_charts_report;
import views.html.admin.reports.financial_detailed_report;
import views.html.admin.reports.current_orders_report;
import views.html.admin.reports.watches_just_gone_mesured;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN})
@With(NoCacheAction.class)
public class Reporting extends Controller { 

	public static Result financialReportEnhanced() {
		return ok(financial_report.render(AnalyticsReportEnhanced.generateReportEnhanced()));
    }
	
	public static Result financialReportEnhanced2() {
		return ok(financial_report2.render(AnalyticsReportEnhanced2.generateReportEnhanced()));
    }
	
	public static Result financialChartsReport() {
		return ok(financial_charts_report.render(AnalyticsChartsReport.generateReportUnhanced()));
    }
	
	public static Result financialDetailedReport() {
		return ok(financial_detailed_report.render(AnalyticsDetailedReport.generateReport()));
    }
	
	public static Result currentOrdersReport() {
		return ok(current_orders_report.render(CurrentOrdersReport.generateReport()));
    }
	
	public static Result possibleWatchesJustGoneReportWithStats() {
		return ok(watches_just_gone_mesured.render(ReportWithStats.mesure(() -> PossibleWatchesJustGoneReport.generateReport(PossibleWatchesJustGoneReport.atLeastAWatchIsStillMarkedBeingHere()))));
    }
	
	public static Result marginVat() {
		return ok(margin_vat.render(WatchSalesReport.generateReport(invoice -> InvoiceType.MARGIN_VAT.equals(invoice.type))));
    }
	
	public static Result exportSales() {
		return ok(export_sales.render(WatchSalesReport.generateReport(invoice -> InvoiceType.RESERVED_1.equals(invoice.type))));
    }
	
	public static Result withVat() {
		return ok(with_vat.render(WatchSalesReport.generateReport(invoice -> InvoiceType.VAT.equals(invoice.type))));
    }
	
	public static Result invoiceLines() {
		return ok(invoice_lines.render(InvoiceLineReport.generateReport()));
    }
	
	public static Result invoiceLinesEnhanced(Long nbOfMonthBack) {
		return ok(invoice_lines.render(InvoiceLineReport.generateReportEnhanced(nbOfMonthBack)));
    }
	
	public static Result invoicesStillToPayWithStats() {
		return ok(invoices.render(ReportWithStats.mesure(() -> InvoicesReport.generateReport(InvoicesReport.isAlreadyPayed().and((invoice) -> UniqueAccountingNumber.fromStringIfValidOnly(invoice.uniqueAccountingNumber, false).map(UniqueAccountingNumber::isInPreviousOrCurrentFinancialYear).orElse(false))))));
    }
	
	public static Result stock() {
		return ok(stock.render(StockReport.generateReport()));
    }
	
	public static Result legalRegister(Long starting) {
		return ok(legal_register.render(LegalRegisterReport.generateReport(starting)));
    }
	
	public static Result legalRegisterHelper(Long starting) {
		return ok(legal_register_helper.render(LegalRegisterReport.generateReport(starting)));
    }
	
	public static Result markInLegalRegister(Long watchId) {
		WatchToSell watchFound = WatchToSell.findById(watchId);
		if (watchFound != null
				&& watchFound.shouldBeInRegistry
				&& !watchFound.isInRegistry) {
			watchFound.isInRegistry = true;
			watchFound.update();
		}
		return redirect(
				routes.Reporting.legalRegisterHelper(1l)+"#"+watchId
				);
	}
	
	public static Result addresses() {
		return ok(address_tab.render(getCustomers()));
    }
	
	public static Result addressesAlt() {
		return ok(address_tab_table.render(getCustomers()));
    }
	
	public static Result addressBook() {
		return ok(address_tab_alpha.render(getAddressBook()));
    }
	
	public static Result payments() {
		return ok(payments.render(PaymentsReport.generateReportEnhanced()));
    }
	
	public static Result paymentsByCreationDate() {
		return ok(payments.render(PaymentsReport.generateReportEnhancedCreationDateDESC()));
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
