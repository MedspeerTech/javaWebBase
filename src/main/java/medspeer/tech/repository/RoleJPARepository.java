package medspeer.tech.repository;

import medspeer.tech.constants.UserRoles;
import medspeer.tech.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RoleJPARepository  extends JpaRepository<Role,Long> {
    Role findByName(UserRoles name);
}
