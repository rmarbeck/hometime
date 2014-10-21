package controllers;

import play.libs.F.Promise;
import play.mvc.Action.Simple;
import play.mvc.Http.Context;
import play.mvc.Result;

public class NoCacheAction extends Simple {

	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		ctx.response().setHeader("Pragma", "no-cache");
		return delegate.call(ctx);
	}

}
