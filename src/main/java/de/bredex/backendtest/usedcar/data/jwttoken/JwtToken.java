package de.bredex.backendtest.usedcar.data.jwttoken;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "jwt_token")
public class JwtToken {
    @Id
    private String token;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_user_email", referencedColumnName = "email")
    private ApplicationUser applicationUser;
    private Instant issuedTime = Instant.now();
    private Instant expiryTime;
    private boolean isBlacklisted;
}
