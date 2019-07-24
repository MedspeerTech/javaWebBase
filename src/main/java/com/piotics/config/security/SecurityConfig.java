package com.piotics.config.security;
import static com.piotics.config.SecurityConstants.USER_BASE_URL;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.piotics.config.JWTAuthenticationFilter;
import com.piotics.config.JWTAuthorizationFilter;
import com.piotics.config.SimpleCORSFilter;
import com.piotics.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


//  @Value("${spring.queries.users-query}")
//  private String usersQuery;
//
//  @Value("${spring.queries.roles-query}")
//  private String rolesQuery;

//  @Autowired
//  private DataSource dataSource;
	
	@Autowired
	  UserDetailsServiceImpl userDetailsServiceImpl ;


  @Autowired
  private AuthSuccess authSuccess;

  @Autowired
  private AuthFailure authFailure;
  
  @Autowired
  LogoutSuccessHandler logoutSuccessHandler;
  
  @Autowired
  private EntryPointUnAuthorizedHandler entryPointUnAuthorizedHandler;

  private UserDetailsService userDetailsService;
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  public SecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
      this.userDetailsService = userDetailsService;
      this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  public static final String JWT_TOKEN_HEADER_PARAM = "X-Authorization";
  public static final String FORM_BASED_LOGIN_ENTRY_POINT = "/api/auth/login";
  public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/api/**";
  public static final String TOKEN_REFRESH_ENTRY_POINT = "/api/auth/token";

	

  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http
      		.cors()
      		.and()
      		.csrf()
      		.disable()
              .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              .and()
              .exceptionHandling()
              .authenticationEntryPoint(entryPointUnAuthorizedHandler)
              .and()

              .formLogin()
              .loginPage("/login")
              .successHandler(authSuccess)
              .failureHandler(authFailure)
              .and()
              .authorizeRequests()

              .antMatchers(HttpMethod.POST, USER_BASE_URL).permitAll()
              .antMatchers(HttpMethod.GET, USER_BASE_URL).permitAll()
              .antMatchers(HttpMethod.GET, "/").permitAll()
              .antMatchers(HttpMethod.GET, "/login").permitAll()
              .antMatchers(HttpMethod.POST, "/admin/**").permitAll()
//              .antMatchers(HttpMethod.POST, "/profile/**").permitAll()
//              .antMatchers(HttpMethod.GET, "/profile/**").permitAll()
              .antMatchers(HttpMethod.GET, "/*.js").permitAll()
              .antMatchers(HttpMethod.GET, "/*.html").permitAll()
              .antMatchers(HttpMethod.GET, "/api/session").permitAll()
              .antMatchers(HttpMethod.GET, "/error").permitAll()
              .antMatchers(HttpMethod.GET, "/constants/**").permitAll()
              .antMatchers(HttpMethod.POST, "/socialAuth/**").permitAll()
              .antMatchers(HttpMethod.GET, "/file/video/stream/**").permitAll()
              .antMatchers(HttpMethod.GET, "/file/getFile").permitAll()
              .antMatchers(HttpMethod.GET, "/file/get*/**").permitAll()
              .antMatchers(HttpMethod.GET, "/learn/**").permitAll()
//              .antMatchers(HttpMethod.GET, "/file/getPreview/**").permitAll()



              .anyRequest().authenticated()
              .and()
              .logout()
                  .deleteCookies("remove")
                  .invalidateHttpSession(true)
                  .logoutUrl("/logout")
                  .logoutSuccessHandler(logoutSuccessHandler)
                  .permitAll()
              .and()
              .addFilter(this.jwtProcessingFilter())
              .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
              http.addFilterAfter(new SimpleCORSFilter(), LogoutFilter.class);

  }

  @Bean
  JWTAuthenticationFilter jwtProcessingFilter() throws Exception {
      JWTAuthenticationFilter tokenProcessingFilter = new JWTAuthenticationFilter();
      tokenProcessingFilter.setAuthenticationManager(authenticationManager());
      return tokenProcessingFilter;
  }

//  @Autowired
//  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//      auth
//              .jdbcAuthentication()
//              .usersByUsernameQuery(usersQuery)
//              .authoritiesByUsernameQuery(rolesQuery)
//              .dataSource(dataSource);
//  }

//  @Bean(name = "AccessFilter")
//  AccessFilter createAccessFilter(){
//      return new AccessFilter();
//  }


  @Bean
  CorsConfigurationSource corsConfigurationSource() {
      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
      return source;
  }

//  @Override
//  public void configure(AuthenticationManagerBuilder auth) throws Exception {
//      auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
//  }
  
  
  @Autowired
  public void configAuthBuilder(AuthenticationManagerBuilder builder) throws Exception {
      builder.userDetailsService(userDetailsService); 
  }
  
}
