package es.gmbdesign.invoiceme.utiles;

import org.apache.commons.codec.digest.DigestUtils;

import es.gmbdesign.invoiceme.business.interfaces.IValidateObjects;
import es.gmbdesign.invoiceme.dto.DeviceRegistered;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTUtil {

	public static String createJWT (DeviceRegistered device, final String SCOPE) {
		String JWT = null;
		final String USER = device.getUser();
		final String APP_VERSION = device.getAppVersion(); 
		JWT = Jwts.builder()
				.setSubject("invoiceMe-API")
				.claim("user", USER)
				.claim("appVersion", APP_VERSION)
				.claim("scope", SCOPE)
				.claim("hash", createUserHash(USER))
				.signWith(SignatureAlgorithm.HS256, getApiKey())
				.compact();
		return JWT;
	}
	
	public static boolean validateJWT (String token) {
		boolean validado = false;
		Jws<Claims> claims = Jwts.parser().setSigningKey(getApiKey()).parseClaimsJws(token);
		final String USER = claims.getBody().get("user", String.class);
		final String HASH = claims.getBody().get("hash", String.class);
		IValidateObjects<String, String> iValidator = (a, b) -> (a.equals(createUserHash(b)));
		validado = iValidator.validate(HASH, USER);
		return validado;
	}
	
	private static byte[] getApiKey() {
		return PropertyUtil.getProperty("api.key").getBytes();
	}
	
	private static String createUserHash(final String USER) {
		return DigestUtils.sha256Hex(USER);
	}
}
