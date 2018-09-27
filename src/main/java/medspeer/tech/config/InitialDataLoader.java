package medspeer.tech.config;

import medspeer.tech.constants.UserAuthorities;
import medspeer.tech.constants.UserRoles;
import medspeer.tech.model.Authority;
import medspeer.tech.model.Role;
import medspeer.tech.repository.AuthorityJPARepository;
import medspeer.tech.repository.RoleJPARepository;
import medspeer.tech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    boolean alreadySetup = false;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleJPARepository roleRepository;
    @Autowired
    private AuthorityJPARepository authorityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        Authority readAuthority = createAuthorityIfNotFound(UserAuthorities.READ_AUTHORITY);
        Authority writeAuthority = createAuthorityIfNotFound(UserAuthorities.WRITE_AUTHORITY);

        List<Authority> adminAuthorities = Arrays.asList(
                readAuthority, writeAuthority);
        createRoleIfNotFound(UserRoles.ROLE_ADMIN, adminAuthorities);
        createRoleIfNotFound(UserRoles.ROLE_USER, Arrays.asList(readAuthority));

        alreadySetup = true;
    }

    @Transactional
    Authority createAuthorityIfNotFound(UserAuthorities name) {

        Authority Authority = authorityRepository.findByName(name);
        if (Authority == null) {
            Authority = new Authority(name);
            authorityRepository.save(Authority);
        }
        return Authority;
    }

    @Transactional
    Role createRoleIfNotFound(
            UserRoles name, Collection<Authority> authorities) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }
}