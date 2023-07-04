package de.bredex.backendtest.usedcar.security.jwt;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenManager {

    private final JwtProperties jwtProperties;

    public String issueToken(ApplicationUser applicationUser, Map<String, Object> extraClaims) {
        return Jwts
                .builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTimeMillis()))
                .setSubject(applicationUser.getName())
                .setClaims(extraClaims)
                .signWith(generateSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, ApplicationUser applicationUser) {
        final String tokenUserName = extractTokenUserName(token);
        final Date expirationDate = extractClaim(token, Claims::getExpiration);
        final Date currentDate = new Date(System.currentTimeMillis());

        return tokenUserName.equals(applicationUser.getName()) && expirationDate.after(currentDate);
    }

    public String extractTokenUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimExtractor) {
        return claimExtractor.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(generateSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key generateSigningKey() {
        byte[] keyBites = Decoders.BASE64URL.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBites);
    }
}
