package medspeer.tech.service;

import medspeer.tech.model.ApplicationUser;
import medspeer.tech.repository.ApplicationUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private ApplicationUserRepository applicationUserRepository;

	public UserDetailsServiceImpl(ApplicationUserRepository applicationUserRepository) {
		this.applicationUserRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}
//		return new User(applicationUser.getUsername(), applicationUser.getPassword(), applicationUser.isEnabled(),applicationUser.isAccountNonExpired(),applicationUser.isCredentialsNonExpired(),applicationUser.isAccountNonLocked(),applicationUser.getAuthorities());
		return applicationUser;
	}

	public Optional<ApplicationUser> findById(String id) {
		// TODO Auto-generated method stub
		return applicationUserRepository.findById(id);
	}
}
