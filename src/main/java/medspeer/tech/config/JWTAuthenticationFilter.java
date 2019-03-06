package medspeer.tech.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import medspeer.tech.common.utils.HttpServletRequestUtils;
import medspeer.tech.constants.MessageType;
import medspeer.tech.controller.UserController;
import medspeer.tech.model.ApplicationUser;
import medspeer.tech.resources.ExceptionResource;
import medspeer.tech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static medspeer.tech.config.SecurityConstants.EXPIRATION_TIME;
import static medspeer.tech.config.SecurityConstants.HEADER_STRING;
import static medspeer.tech.config.SecurityConstants.TOKEN_PREFIX;
import static medspeer.tech.config.SecurityConstants.SECRET;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	UserService userService;

	@Autowired
	HttpServletRequestUtils httpServletRequestUtils;

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		String[] creds = getCredentials(request);
		String pass = creds[1].trim();
		if (pass.equals(null) || pass.equals("")) {

		}
		return pass;
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		String[] creds = getCredentials(request);
		return creds[0];
	}

	protected String[] getCredentials(HttpServletRequest request) {
		final String authorization = request.getHeader(HEADER_STRING);

		String[] values = new String[0];
		if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
			String base64Credentials = authorization.substring("Basic".length()).trim();
			byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
			String credentials = new String(credDecoded, StandardCharsets.UTF_8);
			values = credentials.split(":", 2);
		}
		return values;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, 
											HttpServletResponse res, 
											FilterChain chain,
											Authentication auth) throws IOException, ServletException {

		String clientBrowser = httpServletRequestUtils.getClientBrowser(req);

		Date currentTime = new Date();
		boolean remember = Boolean.parseBoolean(req.getHeader("Remember"));
		Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

		if (remember || clientBrowser.contains("UnKnown, More-Info:")) {
			expirationDate = new Date(2050, 1, 1);
		}

		Claims claims = Jwts.claims().setSubject(((ApplicationUser) auth.getPrincipal()).getId());
		claims.put("scopes", Arrays.asList("ad", ""));
		String token = Jwts.builder().setClaims(claims)
//				.setIssuer(settings.getTokenIssuer())
				.setId(UUID.randomUUID().toString()).setIssuedAt(currentTime).setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes()).compact();

		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		ExceptionResource exceptionResource = new ExceptionResource(HttpStatus.EXPECTATION_FAILED.value(),
				MessageType.FAILURE, failed.getMessage());
		ObjectMapper objectMapper = new ObjectMapper();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(objectMapper.writeValueAsString(exceptionResource));
		response.setHeader("Content-Type", "application/json");
		response.getWriter().flush();
		response.getWriter().close();
//		super.unsuccessfulAuthentication(request,response,failed);
	}

}
