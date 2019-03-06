package medspeer.tech.repository;

import medspeer.tech.model.ApplicationUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Integer> {
	ApplicationUser findByUsername(String username);

	Optional<ApplicationUser> findById(String id);
}
