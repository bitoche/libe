package ru.miit.libe.cfg;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ru.miit.libe.security.JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, ru.miit.libe.security.JwtAuthenticationFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    final String[] AUTH_ENDPOINTS = ControllersSecurityConfig.AUTH_ENDPOINTS;
    final String[] ACTIVATED_ROLE_ENDPOINTS = ControllersSecurityConfig.ACTIVATED_ROLE_ENDPOINTS;
    final String[] STUDENT_ENDPOINTS = ControllersSecurityConfig.STUDENT_ENDPOINTS;
    final String[] TEACHER_ENDPOINTS = ControllersSecurityConfig.TEACHER_ENDPOINTS;
    final String[] LIBRARIAN_ENDPOINTS = ControllersSecurityConfig.LIBRARIAN_ENDPOINTS;
    final String[] ADMIN_ENDPOINTS = ControllersSecurityConfig.ADMIN_ENDPOINTS;
    final String[] RESOURCES_ENDPOINTS = ControllersSecurityConfig.RESOURCES_ENDPOINTS;
    final String[] SWAGGER_ENDPOINTS = ControllersSecurityConfig.SWAGGER_ENDPOINTS;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/test/login_page")
                            .permitAll()
                        .requestMatchers(SWAGGER_ENDPOINTS)
                            .permitAll()
                        .requestMatchers(RESOURCES_ENDPOINTS)
                            .permitAll()
                        .requestMatchers(AUTH_ENDPOINTS)
                            .permitAll()
                        .requestMatchers(ADMIN_ENDPOINTS).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(LIBRARIAN_ENDPOINTS).hasAuthority("ROLE_LIBRARIAN")
                        .requestMatchers(TEACHER_ENDPOINTS).hasAuthority("ROLE_TEACHER")
                        .requestMatchers(STUDENT_ENDPOINTS).hasAnyAuthority(
                                "ROLE_STUDENT",
                                "ROLE_TEACHER",
                                "ROLE_LIBRARIAN",
                                "ROLE_ADMIN"
                        )
                        .requestMatchers(ACTIVATED_ROLE_ENDPOINTS).hasAnyAuthority(
                                "ROLE_AUTHORIZED",
                                "ROLE_STUDENT",
                                "ROLE_TEACHER",
                                "ROLE_LIBRARIAN",
                                "ROLE_ADMIN"
                        )
                        .anyRequest()
                            .denyAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
