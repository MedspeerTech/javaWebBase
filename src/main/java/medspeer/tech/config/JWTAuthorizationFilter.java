package medspeer.tech.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import medspeer.tech.model.ApplicationUser;
import medspeer.tech.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static medspeer.tech.config.SecurityConstants.HEADER_STRING;
import static medspeer.tech.config.SecurityConstants.TOKEN_PREFIX;
import static medspeer.tech.config.SecurityConstants.SECRET;

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

				if (username != null) {
					Optional<ApplicationUser> applicationUser = use.findById(username);
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
							applicationUser.get(), null, applicationUser.get().getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(auth);
					return auth;
				}
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
