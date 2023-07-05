package de.bredex.backendtest.usedcar.security.jwt;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtToken;
import de.bredex.backendtest.usedcar.data.jwttoken.JwtTokenRepository;
import de.bredex.backendtest.usedcar.security.jwt.validation.JwtValidationResult;
import de.bredex.backendtest.usedcar.security.jwt.validation.JwtValidator;
import de.bredex.backendtest.usedcar.security.jwt.util.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        claims.put("issued-to", applicationUser.getName());
        claims.put("fingerprint", UUID.randomUUID());
        Date expirationTime = new Date(System.currentTimeMillis() + jwtProperties.getExpirationTimeMillis());

        final String jwt =  Jwts
                .builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationTime)
                .setSubject(applicationUser.getName())
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
