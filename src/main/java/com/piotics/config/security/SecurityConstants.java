package com.piotics.config.security;

public class SecurityConstants {

	public static final String SECRET = "SecretKeyToGenJWTs";
	public static final long EXPIRATION_TIME = 36_000_00; // 432_000_00 = 12 hours // set 21_432_000_00 long time for development //3_153_600_000_00 for 10 years
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/user/signUp";
	public static final String EMAIL_VERIFICATION_URL = "/user/verifyEmail";
	public static final String FORGOT_PASSWORD__URL = "/user/forgotPassword";
	public static final String USER_BASE_URL = "/user/**";

}
