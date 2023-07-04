package de.bredex.backendtest.usedcar.data.applicationuser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, String> {

    @Override
    Optional<ApplicationUser> findById(String email);

    Optional<ApplicationUser> findByEmailAndName(String email, String name);

    Optional<ApplicationUser> findByName(String name);
}
