package com.piotics.config;

import static com.piotics.config.SecurityConstants.*;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.piotics.constants.UserRoles;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Session;
import com.piotics.service.TenantService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.ExpirationInMs}")
	private int jwtExpirationInMs;

	@Value("${tenant.enabled}")
	boolean tenatEnabled;

	public String generateToken(Authentication authentication) {

		Session session = (Session) authentication.getPrincipal();
		return Jwts.builder().setSubject(session.getId()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes()).compact();
	}

	public String generateJwtToken(Authentication auth, Date expirationDate) {

		if (expirationDate == null)
			expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

		Claims claims = Jwts.claims().setSubject(((ApplicationUser) auth.getPrincipal()).getId());
		claims.putIfAbsent("role", ((ApplicationUser) auth.getPrincipal()).getRole());
		if (tenatEnabled && claims.get("role") != UserRoles.ROLE_POWER_ADMIN) { 
			claims.putIfAbsent("tenantId", ((ApplicationUser) auth.getPrincipal()).getCompany().getId());
		}else {
			claims.putIfAbsent("tenantId", "");
		}
		
		claims.put("scopes", Arrays.asList("ad", ""));
		return Jwts.builder().setClaims(claims)
//				.setIssuer(settings.getTokenIssuer())
				.setId(UUID.randomUUID().toString()).setIssuedAt(new Date()).setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes()).compact();
	}

	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

		return Long.parseLong(claims.getSubject());
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			logger.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		return false;
	}
}
