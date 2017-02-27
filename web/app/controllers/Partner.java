package controllers;

import java.util.Date;

import models.CustomerWatch.CustomerWatchStatus;
import fr.hometime.utils.PartnerHelper;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.admin.customer_watches_for_partner;
import views.html.admin.customer_watches_for_partner_waiting_acceptation;
import views.html.admin.customer_watches_for_partner_waiting_quotation;

@Security.Authenticated(SecuredAdminAndReserved2Only.class)
@With(NoCacheAction.class)
public class Partner extends Controller {
	
	public static Result LIST_WAITING_ACCEPTATION_WATCHES = redirect(
			routes.Partner.displayWaitingAcceptationWatches(0, "lastStatusUpdate", "desc", "", 20, "")
			);
	
	public static Result displayAll(int page, String sortBy, String order, String filter, int size) {
        return displayWatches(page, sortBy, order, filter, size, "");
    }
	
	public static Result displayWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_partner.render(models.CustomerWatch.pageForPartner(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWaitingAcceptationWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_partner_waiting_acceptation.render(models.CustomerWatch.pageForPartnerWaitingAcceptation(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }
	
	public static Result displayWaitingQuotationWatches(int page, String sortBy, String order, String filter, int size, String status) {
        return ok(customer_watches_for_partner_waiting_quotation.render(models.CustomerWatch.pageForPartnerWaitingQuotation(page, size, sortBy, order, filter, status, session()), sortBy, order, filter, size, status));
    }

	public static Result acceptWatch(Long id) {
		models.CustomerWatch requestedWatch = models.CustomerWatch.findById(id);
		if (PartnerHelper.isWatchAllocatedToLoggedInPartner(requestedWatch, session()))
			acceptWatchAndSave(requestedWatch);
        return LIST_WAITING_ACCEPTATION_WATCHES;
    }
	
	private static void acceptWatchAndSave(models.CustomerWatch requestedWatch) {
		requestedWatch.status = CustomerWatchStatus.STORED_BY_A_REGISTERED_PARTNER;
		requestedWatch.firstEntryInPartnerWorkshopDate = new Date();
		requestedWatch.update();
	}
}
