package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Brand;
import models.BuyRequestProposal;
import models.Order;
import models.OrderRequest;
import models.OrderRequest.OrderTypes;
import models.PresetQuotationForBrand;
import models.Quotation;
import models.ServiceTest;
import models.ServiceTest.TestResult;
import models.Watch;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.admin.buy_request_proposal_form;
import views.html.admin.buy_request_proposal;
import views.html.admin.buy_request;
import views.html.admin.buy_requests;
import views.html.mails.notify_buy_request;
import fr.hometime.utils.MailjetAdapter;
import fr.hometime.utils.ServiceTestHelper;

@Security.Authenticated(SecuredAdminOnly.class)
@With(NoCacheAction.class)
public class BuyRequest extends Controller {
	
	public static Result LIST_BUY_REQUESTS = redirect(
			routes.BuyRequest.displayBuyRequests(0, "requestDate", "desc", "")
			);
	
	public static Result displayBuyRequests(int page, String sortBy, String order, String filter) {
        return ok(buy_requests.render(models.BuyRequest.page(page, 10, sortBy, order, filter), sortBy, order, filter));
    }
	
	public static Result displayBuyRequest(long id) {
		if (buyRequestIsValid(id))
			return ok(buy_request.render(models.BuyRequest.findById(id)));
		flash("error", "Unknown id");
		return LIST_BUY_REQUESTS;
    }
	
	public static Result displayBuyRequestMail(long id) {
		if (buyRequestIsValid(id))
			return ok(notify_buy_request.render(models.BuyRequest.findById(id)));
		flash("error", "Unknown id");
		return LIST_BUY_REQUESTS;
    }
	
    public static Result prepareProposal() {
    	try {
	        return ok(buy_request_proposal_form.render(Form.form(BuyRequestProposal.class).fill(new BuyRequestProposal())));
    	} catch (Exception e) {
    		return internalServerError();
    	}
    }
    
    public static Result prepareProposalFromRequest(long requestId) {
    	if (buyRequestIsValid(requestId))
    		return ok(buy_request_proposal_form.render(Form.form(BuyRequestProposal.class).fill(new BuyRequestProposal(models.BuyRequest.findById(requestId)))));
    	return prepareProposal();
    }
    
	public static Promise<Result> manageProposal() {
		final Form<BuyRequestProposal> proposalForm = Form.form(BuyRequestProposal.class).bindFromRequest();
		Logger.debug("Managing Proposal");
		if(proposalForm.hasErrors()) {
			Logger.debug("Error in form : {}", proposalForm.errors());
			return Promise.pure((Result) badRequest(buy_request_proposal_form.render(proposalForm)));
		} else {
			if ("preview".equals(proposalForm.get().action))
				return displayProposal(proposalForm.get());
			if ("edit".equals(proposalForm.get().action))
				return editProposal(proposalForm.get());
			return sendProposal(proposalForm);
		}
	}
	
	
	private static Promise<Result> displayProposal(BuyRequestProposal proposalFilled) {
		Logger.debug("Displaying Proposal");
		return Promise.pure(ok(buy_request_proposal.render(proposalFilled)));
    }
    
	private static Promise<Result> editProposal(BuyRequestProposal proposalFilled) {
		Logger.debug("Editing Proposal");
		return Promise.pure(ok(buy_request_proposal.render(proposalFilled)));
    }

	private static Promise<Result> sendProposal(final Form<BuyRequestProposal> proposalForm) {
		Logger.debug("Sending Proposal");
		try {
			final BuyRequestProposal proposalFilled = proposalForm.get();
			String subject = Messages.get("admin.buy.request.proposal.email.subject");
			String title = Messages.get("admin.buy.request.proposal.email.title",proposalFilled.customerEmail);
			String email = proposalFilled.customerEmail;
			String html = buy_request_proposal.render(proposalFilled).body();
			String textVersion = Messages.get("admin.buy.request.proposal.email.text.version");

			Promise<Result> resultPromise = MailjetAdapter.wsCreateACampaignWithHtmlContent(subject, title, email, html, textVersion)
					.map(url -> {
						flash("success", Messages.get("admin.buy.request.proposal.success", url));
						return LIST_BUY_REQUESTS;
						});
							
			Promise<Result> recoverPromise = resultPromise.recoverWith(throwable -> {
				Logger.debug("4");
				Logger.error(throwable.getMessage());
				flash("error", throwable.getMessage());
	        	return Promise.pure((Result) badRequest(buy_request_proposal_form.render(proposalForm)));
			});
			
			return recoverPromise;
		} catch (Exception e) {
			Logger.debug("5");
			flash("error", e.getMessage());
			return Promise.pure((Result) badRequest(buy_request_proposal_form.render(proposalForm)));	
		}
    }
	
	private static boolean buyRequestIsValid(long id) {
		models.BuyRequest requestFound = models.BuyRequest.findById(id);
		return requestFound != null;
	}
	
    private static String getStringValue(String value) {
    	if (value != null && value.length() == 0) {
    		return null;
    	} else {
    		return value;
    	}
    }
}
