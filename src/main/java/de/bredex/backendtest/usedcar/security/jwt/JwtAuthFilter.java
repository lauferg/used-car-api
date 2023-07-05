package de.bredex.backendtest.usedcar.security.jwt;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUserRepository;
import de.bredex.backendtest.usedcar.security.jwt.util.JwtTokenUtil;
import de.bredex.backendtest.usedcar.security.jwt.validation.AssignedToUserValidator;
import de.bredex.backendtest.usedcar.security.jwt.validation.BlacklistedValidator;
import de.bredex.backendtest.usedcar.security.jwt.validation.JwtValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final ApplicationUserRepository applicationUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            executeFilterActions(request, response);
        }

        filterChain.doFilter(request, response);
    }

    private void executeFilterActions(HttpServletRequest request, HttpServletResponse response) {
        final String jwt = request.getHeader("Authorization").substring(7);
        final String tokenUserId = jwtTokenUtil.extractTokenOwnerId(jwt);

        if (StringUtils.hasText(tokenUserId) && SecurityContextHolder.getContext().getAuthentication() == null) {
            ApplicationUser applicationUser = applicationUserRepository.findById(tokenUserId).orElseThrow();
            List<JwtValidationResult> jwtValidationResults = jwtTokenManager.validateToken(jwt, applicationUser);

            if (jwtValidationResults.stream().allMatch(JwtValidationResult::isTokenValid)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        applicationUser.getEmail(),
                        applicationUser.getName(),
                        Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                if ((!isTokenToBeBlacklisted(jwtValidationResults))) {
                    jwtTokenManager.blacklistToken(jwt);
                    SecurityContextHolder.clearContext();
                } else {
                    SecurityContextHolder.clearContext();
                }
            }
        }
    }

    private boolean isTokenToBeBlacklisted(List<JwtValidationResult> jwtValidationResults) {
        return jwtValidationResults
                .stream()
                .anyMatch(result -> result.getValidatorClass() == AssignedToUserValidator.class && result.isTokenValid())

                &&

                jwtValidationResults
                        .stream()
                        .anyMatch(result -> result.getValidatorClass() == BlacklistedValidator.class && result.isTokenValid());
    }
}
