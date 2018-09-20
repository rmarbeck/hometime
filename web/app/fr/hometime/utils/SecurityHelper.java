package fr.hometime.utils;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import play.Logger;
import play.mvc.Http.Session;
import play.mvc.Http.Context;
import models.User;
import models.User.Role;

/**
 * Security class containing functions for security purpose 
 * 
 * @author Raphael
 *
 */

public class SecurityHelper {
	public static String toMD5(String toEncode) {
		return encode(toEncode, "MD5");
	}

	public static String toSHA1(String toEncode) {
		return encode(toEncode, "SHA1");
	}

	public static String decodeHMacSHA256(String toDecode, String key) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKey);
			byte[] hmacData = mac.doFinal(toDecode.getBytes("UTF-8"));
			return new String(hmacData);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String encode(String toEncode, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] array = md.digest(toEncode.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}
	
    public static boolean doesFieldContainSPAM(String value) {
    	if (value != null && (value.contains("http://") || value.contains("https://")))
    		return true;
    	return false;
    }
    
    public static String retrieveUsername(Context ctx, Predicate<User> isRoleEnough) {
    	String currentUserToken = ctx.session().get("token");
    	if (currentUserToken != null) {
    		Optional<User> oLoggedInUser = tryToGetUserByEmail(currentUserToken);
    		if (oLoggedInUser.isPresent()) {
    			Logger.debug("There seems to be a user in session : {}", oLoggedInUser.get());
	    		if (isRoleEnough.test(oLoggedInUser.get()))
	    			return oLoggedInUser.get().email;
	    		Logger.debug("User in session is not authorized for this page ({}): {}", ctx.request().path(), oLoggedInUser.get());
    		}
    	}
    		
        return null;
    }
    
    public static String retrieveUsername(Context ctx) {
        return retrieveUsername(ctx, (user) -> true);
    }
    
    public static boolean isLoggedInUserAuthorized(Session session, Predicate<User> isRoleEnough) {
    	return isLoggedInUserAuthorized(session.get("token"), isRoleEnough);
    }
    
    public static boolean isLoggedInUserAuthorized(String token, Predicate<User> isRoleEnough) {
    	Optional<User> loggedInUser = getLoggedInUser(token);
    	if (loggedInUser.isPresent()) {
    		return isRoleEnough.test(loggedInUser.get());
    	}
        return false;
    }
    
    public static Optional<User> getLoggedInUser(Session session) {
    	return getLoggedInUser(session.get("token"));
    }
    
    public static Optional<User> getLoggedInUser(String token) {
    	if (token != null)
    		return Optional.ofNullable(User.findByEmail(token));
        return Optional.empty();
    }
    
    public static boolean isUserAuthorized(String username, User.Role[] rolesAuthorized) {
    	Optional<User> oFoundUser = tryToGetUserByUsername(username);
    	if (oFoundUser.isPresent())
    		for (User.Role currentRole : rolesAuthorized)
    			if (oFoundUser.get().role.equals(currentRole))
    				return true;
    	return false;
    }

    public static Predicate<User> isAdmin = (user) -> (user!= null && user.role == Role.ADMIN);
    
    public static Predicate<User> isCollaboratorOrPartner = (user) -> (user!= null && ( user.role == Role.COLLABORATOR || user.role == Role.PARTNER) );
    
    public static Predicate<User> isAdminOrCollaborator = (user) -> (user!= null && ( user.role == Role.COLLABORATOR || user.role == Role.ADMIN) );
    
    public static Predicate<User> isAdminOrPartner = (user) -> (user!= null && ( user.role == Role.PARTNER || user.role == Role.ADMIN) );
    
    public static Predicate<User> isAdminOrCollaboratorOrPartner = (user) -> (user!= null && ( user.role == Role.PARTNER || user.role == Role.COLLABORATOR || user.role == Role.ADMIN) );
    
    public static Predicate<User> isCollaborator = (user) -> (user!= null && user.role == Role.COLLABORATOR );
    
    public static Predicate<User> isPartner = (user) -> (user!= null && user.role == Role.PARTNER );
    
    public static Predicate<User> isCustomer = (user) -> (user!= null && user.role == Role.CUSTOMER );
    
    public static Predicate<User> isWatchmaker = (user) -> (user!= null && user.role == Role.MASTER_WATCHMAKER );
    
    public static Predicate<User> isAdminOrWatchmakerOrCollaborator = (user) -> (user!= null && ( user.role == Role.ADMIN || user.role == Role.COLLABORATOR || user.role == Role.MASTER_WATCHMAKER) );
    
    
    
    public static List<User> findQuickLogins() {
        return User.find.where().contains("email", "_quick").findList();
    }
    
	public static String quickAuthenticateAdmin(String password) {
		Optional<User> foundUser = findByQuickPassword(password);
		if (foundUser.isPresent()) {
			findQuickLogins().stream().forEach((user) -> User.resetNumberOfBadPasswords(user));
			return foundUser.get().email;
		}
		return null;
	}
	
    public static Optional<User> findByQuickPassword(String password) {
    	return User.findQuickLogins().stream().filter((user) -> (user.active && user.numberOfBadPasswords <= User.ADMIN_QUICK_MAX_ATTEMPT)).filter((user) -> User.isPasswordMatching(user.email, password)).findFirst();
    }
    
	
	public static String getLoggedInUserEmail(Session session) {
		Optional<User> loggedInUser = SecurityHelper.getLoggedInUser(session);
		if (loggedInUser.isPresent())
			return loggedInUser.get().email;
		return "?";
	}
	
	private static Optional<User> tryToGetUserByEmail(String email) {
		return tryToGetUserByUsername(email);
	}
	
	private static Optional<User> tryToGetUserByUsername(String username) {
		User loggedInUser = User.findByEmail(username);
		if (loggedInUser != null)
			return Optional.of(loggedInUser);
		return Optional.empty();
	}
}
