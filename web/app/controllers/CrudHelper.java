package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;

@SecurityEnhanced.Authenticated(value=SecuredEnhanced.class, rolesAuthorized =  {models.User.Role.ADMIN, models.User.Role.COLLABORATOR, models.User.Role.CUSTOMER, models.User.Role.PARTNER, models.User.Role.MASTER_WATCHMAKER})
@With(NoCacheAction.class)
public class CrudHelper extends Controller {

	public static Result display(String controllerName, Long id) {
		return crud(controllerName).map(crud -> {
			return crud.display(id);
		}).orElse(badRequest("Don't try to hack the URI!!"));
	}
	
	public static Result manage(String controllerName) {
		return crud(controllerName).map(crud -> {
			return crud.manage();
		}).orElse(badRequest("Don't try to hack the URI!!"));
	}
	
	public static Result edit(String controllerName, Long id) {
		return crud(controllerName).map(crud -> {
			return crud.edit(id);
		}).orElse(badRequest("Don't try to hack the URI!!"));
	}
	
	public static Result create(String controllerName) {
		return crud(controllerName).map(crud -> {
			return crud.create();
		}).orElse(badRequest("Don't try to hack the URI!!"));
	}
	
    public static Result page(String controllerName,int page, String sortBy, String order, String filter, int pageSize) {
    	return crud(controllerName).map(crud -> {
			return crud.page(page, sortBy, order, filter, pageSize);
		}).orElse(badRequest("Don't try to hack the URI!!"));
    }
    
    public static Result displayAll(String controllerName, int pageSize) {
    	return page(controllerName, 0, "", "", "", pageSize);
    }
	
	private static Optional<Crud> crud(String controllerName) {
		Optional<Crud> crudValue;
		crudValue = retrieveCrudInstance(controllerName).map(crud -> {
			try {
				return Optional.of((Crud) crud.get(null));
			} catch (Exception e) {
				return Optional.ofNullable((Crud) null);
			}
		}).orElse(Optional.empty());
		
		return crudValue;
	}
	
	private static Optional<Field> retrieveCrudInstance(String controllerName) {
		Optional<Field> crudField;
		
		crudField = guessControllerByName(controllerName).map(clazz -> {
			try {
				if (userIsAuthorizedForThisController(clazz))
					return Optional.of(clazz.getField("crud"));
				return Optional.ofNullable((Field) null);
			} catch (Exception e) {
				return Optional.ofNullable((Field) null);
			}
		}).orElse(Optional.empty());
		
		return crudField;
	}
	
	private static boolean userIsAuthorizedForThisController(Class<?> foundClass) {
		Optional<Security.Authenticated> securityLevel = getSecurityAnnotationForThisControllerifExists(foundClass);
		if (securityLevel.isPresent())
			return isLoggedInUserHasEnoughPrivileges(securityLevel.get());
		
		
		Optional<SecurityEnhanced.Authenticated> securityEnhancedLevel = getSecurityEnhancedAnnotationForThisControllerifExists(foundClass);
		if (securityEnhancedLevel.isPresent())
			return isLoggedInUserHasEnoughPrivileges(securityEnhancedLevel.get());
		
		
		return false;
	}
	
	private static Optional<SecurityEnhanced.Authenticated> getSecurityEnhancedAnnotationForThisControllerifExists(Class<?> foundControllerClass) {
		Annotation[] annotations = foundControllerClass.getAnnotations();

		for(Annotation annotation : annotations)
			if (annotation instanceof SecurityEnhanced.Authenticated)
				return Optional.of((SecurityEnhanced.Authenticated) annotation);

		return Optional.empty();
	}
	
	private static Optional<Security.Authenticated> getSecurityAnnotationForThisControllerifExists(Class<?> foundControllerClass) {
		Annotation[] annotations = foundControllerClass.getAnnotations();

		for(Annotation annotation : annotations)
			if (annotation instanceof Security.Authenticated)
				return Optional.of((Security.Authenticated) annotation);

		return Optional.empty();
	}
	
	private static boolean isLoggedInUserHasEnoughPrivileges(Security.Authenticated securityLevel) {
		String securityClassName = securityLevel.value().getName();
		try {
			return (boolean) Class.forName(securityClassName).getMethod("isLoggedInUserAuthorized", String.class).invoke(null, session().getOrDefault("token", ""));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}	
		
		return false;
	}
	
	private static boolean isLoggedInUserHasEnoughPrivileges(SecurityEnhanced.Authenticated securityLevel) {
		String securityClassName = securityLevel.value().getName();
		User.Role[] rolesAuthorized = securityLevel.rolesAuthorized();
		
		try {
			return (boolean) Class.forName(securityClassName).getMethod("isRoleAuthorized", String.class, User.Role[].class).invoke(null, session().getOrDefault("token", ""), rolesAuthorized);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}	
		
		return false;
	}

	private static Optional<Class<?>> guessControllerByName(String controllerName) {
		Optional<Class<?>> controllerFound;

		StringBuilder fqControllerName = new StringBuilder();
		fqControllerName.append("controllers.").append(controllerName);
		try {
			controllerFound = Optional.of(Class.forName(fqControllerName.toString()));
		} catch (SecurityException | ClassNotFoundException e) {
			controllerFound = Optional.empty();
		}
		return controllerFound;
	}
	
	protected static Optional<String> guessControllerNameByModelInstance(Object instance) {
		return guessControllerNameByModelName(instance.getClass().getSimpleName());
	}
	
	public static Optional<String> guessControllerNameByModelName(String modelName) {
		StringBuilder controllerName = new StringBuilder();
		if (modelName.equals("WatchToSell")) {
			return Optional.of(new String("WatchesToSell"));
		} else if (modelName.equals("News")) {
			return Optional.of(new String("News"));
		} else if (modelName.equals("Customer")) {
			return Optional.of(new String("SimplifiedCustomers"));
		} else if (modelName.equals("CustomerWatch")) {
			return Optional.of(new String("SimplifiedCustomerWatches"));
		} else if (modelName.endsWith("y")) {
			controllerName.append(removeLastChar(modelName));
			controllerName.append("ie");
		} else if (modelName.endsWith("ch")) {
			controllerName.append(modelName);
			controllerName.append("e");
		} else {
			controllerName.append(modelName);
		}
		
		return Optional.ofNullable(controllerName.append("s").toString());
	}
	
	protected static Optional<String> guessModelNameByModelInstance(Object instance) {
		if (instance != null) {
			String rawClassName = instance.getClass().getSimpleName();
			StringBuilder classNameFormatted = new StringBuilder(rawClassName); 
			return Optional.of(classNameFormatted.toString()); 
		}
		return Optional.empty();
	}
	
	private static String removeLastChar(String toAlter) {
		int toAlterSize = toAlter.length();
		return toAlter.substring(0, toAlterSize-1);
	}
}

