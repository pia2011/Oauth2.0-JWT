package oauth2jwt.demo.user.repository;

import oauth2jwt.demo.user.domain.User;
import oauth2jwt.demo.user.enumerate.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
    Boolean existsByEmail(String email);
}
