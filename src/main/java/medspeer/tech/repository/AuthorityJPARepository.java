package medspeer.tech.repository;

import medspeer.tech.constants.UserAuthorities;
import medspeer.tech.model.Authority;
import medspeer.tech.model.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AuthorityJPARepository  extends JpaRepository<Authority,Long> {

    Authority findByName(UserAuthorities name);
}
