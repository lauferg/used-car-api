package de.bredex.backendtest.usedcar.security.jwt;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUserRepository;
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

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenManager tokenManager;
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
        final String tokenUserName = tokenManager.extractTokenUserName(jwt);

        if (StringUtils.hasText(tokenUserName) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ApplicationUser applicationUser = applicationUserRepository.findById(principal).orElseThrow();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(applicationUser.getName(), applicationUser.getEmail(), Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
