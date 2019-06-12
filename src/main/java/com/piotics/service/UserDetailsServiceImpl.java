package com.piotics.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.piotics.repository.ApplicationUserMongoRepository;
import com.piotics.model.ApplicationUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private ApplicationUserMongoRepository applicationUserMongoRepository;

	public UserDetailsServiceImpl(ApplicationUserMongoRepository applicationUserRepository) {
		this.applicationUserMongoRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ApplicationUser applicationUser = applicationUserMongoRepository.findByUsername(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}
//		return new User(applicationUser.getUsername(), applicationUser.getPassword(), applicationUser.isEnabled(),applicationUser.isAccountNonExpired(),applicationUser.isCredentialsNonExpired(),applicationUser.isAccountNonLocked(),applicationUser.getAuthorities());
		return applicationUser;
	}

	public Optional<ApplicationUser> findById(String id) {
		// TODO Auto-generated method stub
		return applicationUserMongoRepository.findById(id);
	}
}
