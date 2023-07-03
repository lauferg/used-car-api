package de.bredex.backendtest.usedcar.security.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authenticationProvider(new DaoAuthenticationProvider())
                .authorizeRequests()
                .antMatchers(
                        "/auth/signup",
                        "/auth/login",
                        "/ad/search"
                )
                .permitAll()

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/ad/{id}")
                .permitAll()

                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile("dev")
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .antMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/proxy/**",
                        "/configuration/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                );
    }
}
