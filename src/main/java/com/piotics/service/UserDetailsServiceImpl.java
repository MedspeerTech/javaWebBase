package com.piotics.service;

import java.util.Optional;

import com.piotics.common.utils.UtilityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.piotics.repository.ApplicationUserMongoRepository;
import com.piotics.repository.UserMongoRepository;
import com.piotics.common.MailManager;
import com.piotics.model.ApplicationUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UtilityManager utilityManager;

	private UserMongoRepository applicationUserMongoRepository;

	public UserDetailsServiceImpl(UserMongoRepository applicationUserRepository) {
		this.applicationUserMongoRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		ApplicationUser applicationUser = new ApplicationUser();
		
		if (utilityManager.isEmail(username)) {
			applicationUser = applicationUserMongoRepository.findByEmail(username);
			if (applicationUser == null) {
				throw new UsernameNotFoundException(username);
			}
		}else {
			applicationUser = applicationUserMongoRepository.findByPhone(username);
			if (applicationUser == null) {
				throw new UsernameNotFoundException(username);
			}
		}
//		return new User(applicationUser.getUsername(), applicationUser.getPassword(), applicationUser.isEnabled(),applicationUser.isAccountNonExpired(),applicationUser.isCredentialsNonExpired(),applicationUser.isAccountNonLocked(),applicationUser.getAuthorities());
		return applicationUser;
	}

	public Optional<ApplicationUser> findById(String id) {
		// TODO Auto-generated method stub
		return applicationUserMongoRepository.findById(id);
	}
}
