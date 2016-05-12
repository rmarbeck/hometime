package controllers;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;
import play.Logger;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import play.twirl.api.Template1;
import play.twirl.api.Template2;
import play.twirl.api.Template5;

import com.avaje.ebean.Page;

public class Crud<T extends Model & CrudReady<T, F>, F> {
	private Template1<T,Html> showTemplate;
	private Template2<Form<F>, Boolean, Html> editTemplate;
	private Template5<Page<T>, String, String, String, Integer, Html> pageTemplate;
    private final CrudReady<T, F> tInstance;
    private final Form<F> form;
	private final Logger.ALogger logger;

    public Crud(Template1<T,Html> showTemplate, Template2<Form<F>, Boolean, Html> editTemplate, Template5<Page<T>, String, String, String, Integer, Html> pageTemplate, CrudReady<T, F> tInstance, Form<F> form, Logger.ALogger logger) {
    	this.showTemplate = showTemplate;
    	this.editTemplate = editTemplate;
    	this.pageTemplate = pageTemplate;
        this.tInstance = tInstance;
        this.form = form;
        this.logger = logger;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <A extends Model & CrudReady<A, B>, B> Crud<A, B> of(A tInstance, Template1<A,Html> showTemplate, Template2<Form<B>, Boolean, Html> editTemplate, Template5<Page<A>, String, String, String, Integer, Html> pageTemplate, Form<B> form) {
    	return new Crud(showTemplate, editTemplate, pageTemplate, tInstance, form, Logger.of(tInstance.getClass()));
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <A extends Model & CrudReady<A, A>> Crud<A, A> of(A tInstance, Template1<A,Html> showTemplate, Template2<Form<A>, Boolean, Html> editTemplate, Template5<Page<A>, String, String, String, Integer, Html> pageTemplate) {
    	return new Crud(showTemplate, editTemplate, pageTemplate, tInstance, Form.form(tInstance.getClass()), Logger.of(tInstance.getClass()));
    }
    
    public Result display(Long id) {
        return display(tInstance.getFinder().byId(id.toString()));
    }
    
    private Result display(T instance) {
        if (instance == null) {
            return notFound();
        }
        return ok(showTemplate.render(instance));
    }
    
    public Result edit(Long id) {
    	T t = tInstance.getFinder().byId(id.toString());
        if (t == null) {
            return notFound();
        }
        return ok(editTemplate.render(t.fillForm(form, true), false));
    }
    
    public Result create() {
        return create(form);
    }
    
    public Result create(Form<F> preFilledForm) {
        return ok(editTemplate.render(tInstance.fillForm(preFilledForm, false), true));
    }
    
    public Result save() {
        Form<F> saveForm = form.bindFromRequest();
        if (saveForm.hasErrors()) {
            return badRequest();
        } else {
        	tInstance.getInstanceFromForm(saveForm).update();
            return ok();
        }
    }
    
    public Result page(int page, String sortBy, String order, String filter, int pageSize) {
        return ok(pageTemplate.render(tInstance.getPage(page, pageSize, sortBy, order, filter), sortBy, order, filter, pageSize));
    }
    
    public Result manage() {
    	Form<F> currentForm = form.bindFromRequest();
    	Logger.debug(currentForm.toString());
		String action = Form.form().bindFromRequest().get("action");
		if ("delete".equals(action)) {
			T instanceInDB = tInstance.getFinder().byId(Form.form().bindFromRequest().get("id").toString());
			instanceInDB.delete();
		} else {
			if (currentForm.hasErrors()) {
				logger.error("error in form ["+currentForm+"] "+" - "+ currentForm.errors());
				if ("save".equals(action))
					return badRequest(editTemplate.render(currentForm, true));
				return badRequest(editTemplate.render(currentForm, false));
			} else {
				T currentInstance = tInstance.getInstanceFromForm(currentForm);
				if ("save".equals(action)) {
					currentInstance.save();
				} else if ("show".equals(action)) {
					return display(currentInstance);
				} else {
					currentInstance.update();
				}
			}
		}
		return redirect(
				routes.CrudHelper.displayAll(CrudHelper.guessControllerNameByModelInstance(tInstance).get(), 20)
				);
    }
}
