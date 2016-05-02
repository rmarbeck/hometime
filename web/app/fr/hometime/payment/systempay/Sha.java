package fr.hometime.payment.systempay;

import java.security.MessageDigest;

public class Sha {
	static public final String SEPARATOR = "+" ;
	public static String encode(String src) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance( "SHA-1" );
			byte bytes[] = src.getBytes( "UTF-8" );
			md.update(bytes, 0, bytes. length );
			byte[] sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	private static String convertToHex(byte[] sha1hash) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < sha1hash. length ; i++) {
			byte c = sha1hash[i];
			addHex(builder, (c >> 4) & 0xf);
			addHex(builder, c & 0xf);
		}
		return builder.toString();
	}
	private static void addHex(StringBuilder builder, int c) {
		if (c < 10)
			builder.append((char) (c + '0' ));
		else
			builder.append((char) (c + 'a' - 10));
	}
}
