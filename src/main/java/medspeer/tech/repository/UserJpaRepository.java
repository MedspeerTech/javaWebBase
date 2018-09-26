package medspeer.tech.repository;

import medspeer.tech.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<ApplicationUser,Long>{
	ApplicationUser findByUsername(String username);
	
	ApplicationUser findByUsernameAndPassword(String Username,String password);

}
