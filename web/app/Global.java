import static play.mvc.Results.badRequest;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.notFound;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.libs.Yaml;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.SimpleResult;

import com.avaje.ebean.Ebean;
import com.typesafe.config.ConfigFactory;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;

public class Global extends GlobalSettings {
	public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }
	
	@Override
    public void onStart(Application app) {
		if (!app.isTest()) {
			Logger.info("Application has started");
		} else {
			Logger.info("Application has started in test mode.");
		}
    }

	@Override
    public void onStop(Application app) {
		Logger.info("Application shutdown...");
    }

    @SuppressWarnings("rawtypes")
	public Action onRequest(Request request, Method actionMethod) {
        Logger.debug("Before each request... {}", request.toString());
        return super.onRequest(request, actionMethod);
    }

    @Override
    public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
        return Promise.<SimpleResult>pure(internalServerError(
            views.html.error.generic.render(t)
        ));
    }
    

    @Override
    public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
        return Promise.<SimpleResult>pure(notFound(
            views.html.error.notfound.render(request.uri())
        ));
    }

    @Override
    public Promise<SimpleResult> onBadRequest(RequestHeader request, String error) {
        return Promise.<SimpleResult>pure(badRequest("Don't try to hack the URI!"));
    }
}
