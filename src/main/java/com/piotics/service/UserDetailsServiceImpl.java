package com.piotics.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.piotics.common.utils.UtilityManager;
import com.piotics.model.ApplicationUser;
import com.piotics.repository.UserMongoRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UtilityManager utilityManager;

	private UserMongoRepository userMongoRepository;

	public UserDetailsServiceImpl(UserMongoRepository applicationUserRepository) {
		this.userMongoRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		ApplicationUser applicationUser = new ApplicationUser();
		
		if (utilityManager.isEmail(username)) {
			applicationUser = userMongoRepository.findByEmail(username);
			if (applicationUser == null) {
				throw new UsernameNotFoundException(username);
			}
		}else {
			applicationUser = userMongoRepository.findByPhone(username);
			if (applicationUser == null) {
				throw new UsernameNotFoundException(username);
			}
		}
//		return new User(applicationUser.getUsername(), applicationUser.getPassword(), applicationUser.isEnabled(),applicationUser.isAccountNonExpired(),applicationUser.isCredentialsNonExpired(),applicationUser.isAccountNonLocked(),applicationUser.getAuthorities());
		return applicationUser;
	}

	public Optional<ApplicationUser> findById(String id) {
		// TODO Auto-generated method stub
		return userMongoRepository.findById(id);
	}
}
