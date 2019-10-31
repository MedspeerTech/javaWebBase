package com.piotics.config;

import static com.piotics.config.SecurityConstants.HEADER_STRING;
import static com.piotics.config.SecurityConstants.SECRET;
import static com.piotics.config.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;

import com.piotics.config.BeanUtil;
import com.piotics.constants.UserRoles;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Session;
import com.piotics.service.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	UserDetailsServiceImpl homeController;

	@Autowired
	private JwtTokenProvider tokenProvider;

	public JWTAuthorizationFilter() {
		super();
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		UserDetailsServiceImpl use = BeanUtil.getBean(UserDetailsServiceImpl.class);

		String header = req.getHeader(HEADER_STRING);

		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
		UserDetailsServiceImpl use = BeanUtil.getBean(UserDetailsServiceImpl.class);

		try {
			if (token != null) {
				Claims claims = Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(token).getBody();
				String username = claims.getSubject();
				String tenantId = claims.getId();

				if (username != null) {
					Session sessionUser = new Session(username, claims.get("email").toString(),claims.get("role").toString());
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
							sessionUser, null, sessionUser.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(auth);
					return auth;
				}
//				if (username != null) {
//					Optional<ApplicationUser> applicationUser = use.findById(username);
//					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//							applicationUser.get(), null, applicationUser.get().getAuthorities());
//					SecurityContextHolder.getContext().setAuthentication(auth);
//					return auth;
//				}
			}
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			Date expDate = e.getClaims().get("exp", Date.class);

		} catch (Exception e) {
			e.printStackTrace();
			SecurityContextHolder.clearContext();
		}
		return null;
	}

}
