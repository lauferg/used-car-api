package de.bredex.backendtest.usedcar.data.jwttoken;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, String> {

    Optional<JwtToken> findByApplicationUser(ApplicationUser applicationUser);
    Optional<JwtToken> findByApplicationUserAndToken(ApplicationUser applicationUser, String token);
    boolean existsByTokenAndApplicationUser(String token, ApplicationUser applicationUser);
}
