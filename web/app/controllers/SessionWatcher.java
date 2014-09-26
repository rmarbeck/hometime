package controllers;

import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;

public class SessionWatcher extends play.mvc.Action.Simple {
	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		setIsFirstPageOfSessionFlag(ctx);
		return delegate.call(ctx);
	}
	
	private void setIsFirstPageOfSessionFlag(Context ctx) {
		if (ctx.session().containsKey("SessionWatcher") == false) {
			ctx.session().put("SessionWatcher", "true");
		} else {
			ctx.session().put("SessionWatcher", "false");
		}
	}
	
	public static boolean isItFirstPageOfSession(Session session) {
		if (session.get("SessionWatcher").equals("true")) {
			Logger.debug("First page of session");
			return true;
		} else {
			Logger.debug("Page n of session");
			return false;
		}
	}
}