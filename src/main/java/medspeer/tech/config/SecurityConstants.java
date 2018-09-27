package medspeer.tech.config;

public class SecurityConstants {

	public static final String SECRET = "SecretKeyToGenJWTs";
	public static final long EXPIRATION_TIME = 864_000_000; // 10 days
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/user/signUp";
	public static final String EMAIL_VERIFICATION_URL = "/user/verifyEmail";
	public static final String USER_BASE_URL = "/user/**";

}
