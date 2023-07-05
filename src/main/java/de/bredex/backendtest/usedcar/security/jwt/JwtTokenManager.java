package de.bredex.backendtest.usedcar.security.jwt;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtToken;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtTokenRepository;
import de.bredex.backendtest.usedcar.security.jwt.util.JwtTokenUtil;
import de.bredex.backendtest.usedcar.security.jwt.validation.JwtValidationResult;
import de.bredex.backendtest.usedcar.security.jwt.validation.JwtValidator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenManager {

    private final JwtProperties jwtProperties;
    private final JwtTokenRepository jwtTokenRepository;
    private final List<JwtValidator> jwtValidators;
    private final JwtTokenUtil jwtTokenUtil;

    public String issueToken(ApplicationUser applicationUser, Map<String, Object> extraClaims) {
        Optional<JwtToken> persistedTokenOptional = jwtTokenRepository.findByApplicationUser(applicationUser);
        return persistedTokenOptional.orElseGet(() ->issueNewToken(applicationUser, extraClaims)).getToken();
    }

    public List<JwtValidationResult> validateToken(String token, ApplicationUser applicationUser) {
        return jwtValidators
                .stream()
                .map(jwtValidator -> jwtValidator.validateToken(token, applicationUser))
                .collect(Collectors.toList());

    }

    public void blacklistToken(ApplicationUser applicationUser) {
        final JwtToken token = jwtTokenRepository
                .findByApplicationUser(applicationUser)
                .orElseThrow();
        token.setBlacklisted(true);
        jwtTokenRepository.save(token);
    }

    public void blacklistToken(String jwt) {
        jwtTokenRepository
                .findById(jwt)
                .ifPresent(this::setBlacklistedFlagAndStore);
    }

    private void setBlacklistedFlagAndStore(JwtToken token) {
        token.setBlacklisted(true);
        jwtTokenRepository.save(token);
    }

    private JwtToken issueNewToken(ApplicationUser applicationUser, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.getExpirationTimeMillis(), ChronoUnit.MILLIS);
        claims.put("issued-to", applicationUser.getEmail());
        claims.put("issued-at", Date.from(now));
        claims.put("expiration", Date.from(expiration));
        claims.put("fingerprint", UUID.randomUUID());
        Date expirationTime = new Date(System.currentTimeMillis() + jwtProperties.getExpirationTimeMillis());

        final String jwt =  Jwts
                .builder()
                .setClaims(claims)
                .signWith(jwtTokenUtil.generateSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtToken jwtTokenEntity = new JwtToken();
        jwtTokenEntity.setToken(jwt);
        jwtTokenEntity.setApplicationUser(applicationUser);
        jwtTokenEntity.setExpiryTime(expirationTime.toInstant());
        jwtTokenRepository.save(jwtTokenEntity);
        return jwtTokenEntity;
    }
}
