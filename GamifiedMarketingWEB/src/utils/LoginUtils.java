package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginUtils {
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	public static String get_SHA_512_Password(String password, String salt) throws NoSuchAlgorithmException{
		String generatedPassword = null;

		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt.getBytes(StandardCharsets.UTF_8));
		byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();

		for(int i=0; i< bytes.length ;i++){
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		generatedPassword = sb.toString();
		return generatedPassword;
	}

	public static String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[64];
		random.nextBytes(salt);
		return salt.toString();
	}

	public static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
}
