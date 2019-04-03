package medspeer.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.googlecode.catchexception.CatchException;

import medspeer.tech.exception.UserException;
import medspeer.tech.model.ApplicationUser;
import medspeer.tech.repository.ApplicationUserRepository;
import medspeer.tech.service.UserDetailsServiceImpl;
import medspeer.tech.service.UserService;
import static com.googlecode.catchexception.CatchException.*;

@RunWith(MockitoJUnitRunner.class)

public class UserDetailsServiceImplTest {
	private static final String REGISTRATION_EMAIL_ADDRESS = "dijofrancis@medspeer.com";
	
	@InjectMocks
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Mock
	private ApplicationUserRepository applicationUserRepository;
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		userDetailsServiceImpl = new UserDetailsServiceImpl(applicationUserRepository);
	}
	
	@Test
	public void loadUserByUsername_UnregisteredEmail_ShouldFail() {
		
		when(applicationUserRepository.findByUsername(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);
		
		catchException(userDetailsServiceImpl).loadUserByUsername(REGISTRATION_EMAIL_ADDRESS);
		
		final Throwable thrown = caughtException();
		assertThat(thrown).isExactlyInstanceOf(UsernameNotFoundException.class);
	}
	
	@Test
	public void loadUserByUsername_RegisteredEmail_ShouldReturnUser() {
		
		ApplicationUser applicationUser = new ApplicationUser();
		applicationUser.setUsername(REGISTRATION_EMAIL_ADDRESS);
		
		when(applicationUserRepository.findByUsername(REGISTRATION_EMAIL_ADDRESS)).thenReturn(applicationUser);
		
		UserDetails returnedUser = userDetailsServiceImpl.loadUserByUsername(REGISTRATION_EMAIL_ADDRESS);		
		assertThat(returnedUser.getUsername()).contains(REGISTRATION_EMAIL_ADDRESS);
		
	}
	
}
