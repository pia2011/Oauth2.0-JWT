package oauth2jwt.demo.auth.repository;

import oauth2jwt.demo.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
    public Optional<RefreshToken> findByToken(String token);
}
