package de.bredex.backendtest.usedcar.data.ad;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a WHERE LOWER(a.make) LIKE CONCAT('%', LOWER(:make), '%') OR LOWER(a.type) LIKE CONCAT('%', LOWER(:type), '%') ")
    List<Ad> findByKeywords(String make, String type);

    long deleteAdByApplicationUserAndId(ApplicationUser applicationUser, Long id);
}
