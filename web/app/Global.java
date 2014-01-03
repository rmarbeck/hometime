import static play.mvc.Results.badRequest;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.notFound;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import models.Watch;
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

@SuppressWarnings("unchecked")
public class Global extends GlobalSettings {
	public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }
	
	static class InitialData {
		public static void insert(Application app) {
			if (app.isDev() || forceReload()) {
				if(Ebean.find(Watch.class).findRowCount() == 0) {

					Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("available-watches-default.yml");

					Ebean.save(all.get("watches"));

					Ebean.save(all.get("pictures"));
					
					Ebean.save(all.get("brands"));

					Map<String,List<Object>> liveConfig = (Map<String,List<Object>>)Yaml.load("liveconfig-default.yml");
					Ebean.save(liveConfig.get("liveConfig"));
				}
			}
		}

	}
	
	@Override
    public void onStart(Application app) {
		if (!app.isTest()) {
			Logger.info("Application has started");
		InitialData.insert(app);
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
    
    private static boolean forceReload() {
    	try {
    		return ConfigFactory.load().getBoolean("force.initial.data.reload");
    	} catch (Throwable t) {
    		return false;
    	}
    }
}