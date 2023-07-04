package de.bredex.backendtest.usedcar.security.jwt;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsedCarUserDetailService implements UserDetailsService {

    private final ApplicationUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser applicationUser = userRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return transformIntoUserDetails(applicationUser);
    }

    private UserDetails transformIntoUserDetails(ApplicationUser applicationUser) {
        return User
                .builder()
                .username(applicationUser.getName())
                .password(applicationUser.getEmail())
                .roles("USER")
                .build();
    }
}
