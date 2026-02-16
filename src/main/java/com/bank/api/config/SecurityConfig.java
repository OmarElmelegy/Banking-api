package com.bank.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Step 1: Disable CSRF
        http.csrf(csrfConfigurer -> {
            csrfConfigurer.disable();
        });

        // Step 2: Set up authorization rules
        http.authorizeHttpRequests(authorizationConfigurer -> {
            authorizationConfigurer
                    .requestMatchers("/api/auth/register").permitAll()

                    .anyRequest().authenticated();
        });

        // Step 3: Enable Basic Authentication
        http.httpBasic(basicAuthConfigurer -> {
            // Use defaults, no customization needed
        });

        // Step 4: Build and return
        SecurityFilterChain chain = http.build();
        return chain;
    }
}
