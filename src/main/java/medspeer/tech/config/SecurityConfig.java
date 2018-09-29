package medspeer.tech.config;

//import com.google.common.collect.ImmutableList;
import medspeer.tech.config.security.AuthFailure;
import medspeer.tech.config.security.AuthSuccess;
import medspeer.tech.config.security.EntryPointUnAuthorizedHandler;
import medspeer.tech.config.security.LogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;

import javax.sql.DataSource;
import static medspeer.tech.config.SecurityConstants.USER_BASE_URL;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthSuccess authSuccess;

    @Autowired
    private AuthFailure authFailure;
    
    @Autowired
    LogoutSuccessHandler logoutSuccessHandler;
    
    @Autowired
    private EntryPointUnAuthorizedHandler entryPointUnAuthorizedHandler;

//    @Autowired
//    private UsersConnectionRepository usersConnectionRepository;

    @Autowired
    private FacebookConnectionSignup facebookConnectionSignup;

//    @Autowired
//    private ConnectionFactoryLocator connectionFactoryLocator;

    @Value("${spring.social.facebook.appSecret}")
    String appSecret;

    @Value("${spring.social.facebook.appId}")
    String appId;

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
                .exceptionHandling()
                .authenticationEntryPoint(entryPointUnAuthorizedHandler)
                .and()

                .formLogin()
                .loginPage("/login")
                .successHandler(authSuccess)
                .and()
                .authorizeRequests()

                .antMatchers(HttpMethod.POST, USER_BASE_URL).permitAll()
                .antMatchers(HttpMethod.GET, USER_BASE_URL).permitAll()

                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/index").permitAll()
                .antMatchers(HttpMethod.GET, "/test").permitAll()
                .antMatchers(HttpMethod.GET, "/*.js").permitAll()
                .antMatchers(HttpMethod.GET, "/*.html").permitAll()
                .antMatchers(HttpMethod.GET, "/assets/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/session").permitAll()
                .antMatchers(HttpMethod.GET, "/backend/api/session").permitAll()
//                .antMatchers(HttpMethod.GET, "/user/setusernamepassword").permitAll()
//                .antMatchers(HttpMethod.GET, "/backend/user/setusernamepassword").permitAll()
                .antMatchers(HttpMethod.GET, "/ui/**").permitAll()
                .antMatchers(HttpMethod.GET, "/error").permitAll()
                .antMatchers("/login*","/signin/**","/signup/**").permitAll()



                //.antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                    .deleteCookies("remove")
                    .invalidateHttpSession(true)
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .permitAll()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));

                http.addFilterAfter(new SimpleCORSFilter(), LogoutFilter.class);

                // this disables session creation on Spring Security
                //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .usersByUsernameQuery(usersQuery)
                .authoritiesByUsernameQuery(rolesQuery)
                .dataSource(dataSource);
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    // @Primary
    public ProviderSignInController providerSignInController() {

//        ((InMemoryUsersConnectionRepository) usersConnectionRepository).setConnectionSignUp(facebookConnectionSignup);

        return new ProviderSignInController(connectionFactoryLocator(), usersConnectionRepository(), new FacebookSignInAdapter());
    }


    @Bean
    @Scope(value="singleton", proxyMode= ScopedProxyMode.INTERFACES)
    public UsersConnectionRepository usersConnectionRepository() {
        return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator(), Encryptors.noOpText());
    }

    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {

        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new FacebookConnectionFactory(appId, appSecret));
        return registry;
    }
}