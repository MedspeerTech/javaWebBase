package medspeer.tech.repository;

import medspeer.tech.common.TokenType;
import medspeer.tech.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TokenJpaRepository extends JpaRepository<Token,Long>{
	Token findByUsernameAndTokenType(String Username, TokenType emailverification);

    Long deleteByUsernameAndTokenAndTokenType(String Username, String token, TokenType emailverification);

}
