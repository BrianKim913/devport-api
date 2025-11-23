package kr.devport.api.repository;

import kr.devport.api.domain.entity.User;
import kr.devport.api.domain.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    Optional<User> findByEmail(String email);

    // Find user by provider and provider ID
    Optional<User> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);

    // Check if email exists
    boolean existsByEmail(String email);
}
