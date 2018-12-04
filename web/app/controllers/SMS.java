package controllers;

import fr.hometime.utils.MailjetSMS;
import fr.hometime.utils.MailjetSMS.MailjetSMSStatus;
import fr.hometime.utils.PhoneNumberHelper;
import fr.hometime.utils.PhoneNumberHelper.FrenchPhoneNumber;
import fr.hometime.utils.SMSHelper;
import models.OrderRequest;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.MASTER_WATCHMAKER, models.User.Role.COLLABORATOR})
@With(NoCacheAction.class)
public class SMS extends Controller {
	
	public static Promise<models.SMS> sendSMS(String phoneNumber, String message) {
		try {
			return sendSMS(MailjetSMS.getDefaultSender(), phoneNumber, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Promise<models.SMS> sendSMS(String sender, String phoneNumber, String message) {
		try {
			Promise<MailjetSMSStatus> smsStatus = MailjetSMS.sendSMS(sender,phoneNumber, message);
			return smsStatus.map(status -> {
				models.SMS currentSMS = new models.SMS(sender, phoneNumber, message);
				if (status.errorMessage == null)
					return currentSMS.updateWithSendingStatus(status.sendingDate, status.status, status.smsCount, status.messageId);
				return currentSMS.updateWithSendingStatusKO(status.errorMessage);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Promise<Result> testSMS() {
		return sendSMSAndGOToHome("+33782434751", "Bonjour, comment ça va ? www.hometime.fr/venir-nous-voir");
	}
	
	public static Promise<Result> sendSMSAndGOToHome(String phoneNumber, String message) {
		Promise<models.SMS> sms = sendSMS(phoneNumber, message);

		return sms.map(currentSms -> {
			if (currentSms.smsCount > 1) {
				flash("success", currentSms.status);
			} else {
				flash("error", currentSms.status);
			}
			return Admin.INDEX;
		});
	}
	
	public static Promise<Result> sendStandardSMS(long orderRequestId) {
		OrderRequest orderRequestFound = OrderRequest.findById(orderRequestId);
		if (orderRequestFound != null && orderRequestFound.phoneNumber != null) {
			if (SMSHelper.canSendSMS(orderRequestFound.phoneNumber)) {
				String phoneNumberInInternationalFormat = PhoneNumberHelper.of(orderRequestFound.phoneNumber).getInInternationalFormat().get();
				return sendSMSAndGOToHome(phoneNumberInInternationalFormat, "toto");
			}
		}
		
		flash("error", "SMS not allowed");
		return Promise.pure(Admin.INDEX);

	}
}
