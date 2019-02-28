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
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

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
					
					Ebean.save(all.get("presets"));
					
					Ebean.save(all.get("orderRequests"));
					
					Ebean.save(all.get("customers"));
					
					Ebean.save(all.get("orders"));
					
					Ebean.save(all.get("customer_watches"));
					
					Map<String,List<Object>> liveConfig = (Map<String,List<Object>>)Yaml.load("liveconfig-default.yml");
					Ebean.save(liveConfig.get("liveConfig"));
					
					Map<String,List<Object>> users = (Map<String,List<Object>>)Yaml.load("users-default.yml");
					Ebean.save(users.get("thepartners"));
					Ebean.save(users.get("customers"));
					Ebean.save(users.get("users"));
					
					Map<String,List<Object>> feedbacks = (Map<String,List<Object>>)Yaml.load("feedbacks-default.yml");
					Ebean.save(feedbacks.get("feedbacks"));
					
					Map<String,List<Object>> templates = (Map<String,List<Object>>)Yaml.load("mail-templates-default.yml");
					Ebean.save(templates.get("mailTemplatesTypes"));
					Ebean.save(templates.get("mailTemplates"));
					
					Map<String,List<Object>> analytics = (Map<String,List<Object>>)Yaml.load("analytics.yml");
					Ebean.save(analytics.get("anayticCodes"));
					Ebean.save(analytics.get("preset"));
					Ebean.save(analytics.get("presetItems"));
					
					
					Map<String,List<Object>> prices = (Map<String,List<Object>>)Yaml.load("prices-default.yml");
					Ebean.save(prices.get("prices"));
				}
			}
		}
		
		public static void insertLiveConfigOnly(Application app) {
					Map<String,List<Object>> liveConfig = (Map<String,List<Object>>)Yaml.load("liveconfig-default.yml");
					Ebean.save(liveConfig.get("liveConfig"));
		}

	}
	
	@Override
    public void onStart(Application app) {
		if (!app.isTest()) {
			Logger.info("Application has started");
			InitialData.insert(app);
		} else {
			Logger.info("Application has started in test mode.");
			InitialData.insertLiveConfigOnly(app);
		}
    }

	@Override
    public void onStop(Application app) {
		Logger.info("Application shutdown...");
    }

    @SuppressWarnings("rawtypes")
	public Action onRequest(Request request, Method actionMethod) {
        Logger.debug("Before each request... {}", request.toString());
        if (isUserAgentBlocked(request))
        	throw new RuntimeException("User agent blocked");

        if (!request.secure() && "GET".equals(request.method()) && request.host().contains("hometime")) {
            return new Action.Simple() {
                public Promise<Result> call(Http.Context ctx) throws Throwable {
                    return Promise.pure(movedPermanently("https://"+request.host() + request.path()));
                }
            };
        }
        return super.onRequest(request, actionMethod);
    }

    @Override
	public Promise<Result> onError(RequestHeader request, Throwable t) {
        return Promise.<Result>pure(internalServerError(
            views.html.error.generic.render(t)
        ));
    }
    

    @Override
    public Promise<Result> onHandlerNotFound(RequestHeader request) {
    	String redirect = null;
    	if(request.uri()!= null && request.uri().startsWith("//"))
    		redirect = request.uri().replaceAll("//", "/");
        return Promise.<Result>pure(notFound(
            views.html.error.notfound.render(request.uri(), redirect)
        ));
    }

	
    @Override
    public Promise<Result> onBadRequest(RequestHeader request, String error) {
        return Promise.<Result>pure(badRequest("Don't try to hack the URI!"));
    }
    
    private static boolean forceReload() {
    	try {
    		return ConfigFactory.load().getBoolean("force.initial.data.reload");
    	} catch (Throwable t) {
    		return false;
    	}
    }
    
    private static boolean isUserAgentBlocked(Request request) {
    	String userAgent = request.getHeader("user-agent");
    	if (userAgent != null && userAgent.toLowerCase().matches("(.*)libwww-perl(.*)"))
    		return true;
    	return false;
    }
}
