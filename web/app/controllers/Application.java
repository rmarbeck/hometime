package controllers;

import java.util.List;

import models.Picture;
import models.Watch;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render(""));
    }

    public static Result service() {
        return ok(service.render(""));
    }

    public static Result watch_collection() {
        return ok(watch_collection.render("", getDisplayableWatches()));
    }
    
    public static Result offer() {
        return ok(offer.render(""));
    }

    public static Result watch_detail(Long id) {
    	try {
	    	Watch currentWatch = Watch.findById(id);
	        return ok(watch_detail.render("Submariner", currentWatch, Picture.findPicturesForWatch(currentWatch), getDisplayableWatchesExceptOne(currentWatch)));
    	} catch (Exception e) {
    		return badRequest();
    	}
    }

    
    private static List<Watch> getDisplayableWatches() {
    	return Watch.findDisplayable();
    }
    

    private static List<Watch> getDisplayableWatchesExceptOne(Watch currentWatch) {
    	List<Watch> displayableWatches = getDisplayableWatches();
    	if (displayableWatches != null && !displayableWatches.isEmpty())
    		displayableWatches.remove(currentWatch);
    	return displayableWatches;
    }
}
