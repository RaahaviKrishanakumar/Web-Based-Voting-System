package org.votingsystem.voting_system_for_award_nominations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    //  Password encoder bean (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //  Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for now (enable later if needed)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register","/about","/winners", "/css/**", "/images/**", "/js/**").permitAll()

                        // Role-based admin sections
                        .requestMatchers("/em/**").hasRole("EM")
                        .requestMatchers("/hr/**").hasRole("HR")
                        .requestMatchers("/coo/**").hasRole("COO")
                        .requestMatchers("/pro/**").hasRole("PRO")
                        .requestMatchers("/fm/**").hasRole("FM")

                        // Normal user
                        .requestMatchers("/userdashboard", "/profile/**").hasRole("USER")

                        // Anything else must be authenticated
                        .anyRequest().authenticated()
                )

                // Login config
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")        // use email instead of username
                        .passwordParameter("password")
                        .successHandler(successHandler)    // redirect users by role
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Logout config
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
