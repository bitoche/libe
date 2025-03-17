package ru.miit.libe.cfg;

import org.springframework.context.annotation.Configuration;
import ru.miit.libe.controllers.ControllersSecurityConfig;

@Configuration
public class SecurityConfig {
    final String[] DEACTIVATED_ROLE_ENDPOINTS = ControllersSecurityConfig.DEACTIVATED_ROLE_ENDPOINTS;
    final String[] ACTIVATED_ROLE_ENDPOINTS = ControllersSecurityConfig.ACTIVATED_ROLE_ENDPOINTS;
    final String[] STUDENT_ENDPOINTS = ControllersSecurityConfig.STUDENT_ENDPOINTS;
    final String[] TEACHER_ENDPOINTS = ControllersSecurityConfig.TEACHER_ENDPOINTS;
    final String[] LIBRARIAN_ENDPOINTS = ControllersSecurityConfig.LIBRARIAN_ENDPOINTS;
    final String[] ADMIN_ENDPOINTS = ControllersSecurityConfig.ADMIN_ENDPOINTS;
    final String[] RESOURCES_ENDPOINTS = {
            "/static/**"
    };
    final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/v*/api-docs/**",
            "/swagger-resources/**"
    };
//    .formLogin(formLogin -> formLogin
//            .loginPage("/auth/login")
//            .loginProcessingUrl("/auth/loginProcessing")
//                        .defaultSuccessUrl("/")
//                        .failureUrl("/login?error=true")
//                        .permitAll()
//                )
//    .logout(logout ->
//            logout.logoutUrl("/auth/logout")
//            .logoutSuccessUrl("/")
//                                .invalidateHttpSession(true)
//                                .deleteCookies("JSESSIONID") // Удалить куки
//                                .permitAll()
//                )
    // .anyRequest().authenticated()
}
