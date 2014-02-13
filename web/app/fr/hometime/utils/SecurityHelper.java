package fr.hometime.utils;

import java.io.UnsupportedEncodingException;
import java.security.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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

}
