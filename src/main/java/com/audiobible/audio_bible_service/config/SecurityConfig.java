package com.audiobible.audio_bible_service.config;

import com.audiobible.audio_bible_service.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/books/**").permitAll()
                        .requestMatchers("/api/v1/categories/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/storage/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/progress")
                        .hasAnyRole("USER", "CONTENT_CREATOR", "ADMIN")
                        .requestMatchers("/api/v1/admin/categories/**", "/api/v1/admin/chapters/**")
                        .hasAnyRole("CONTENT_CREATOR", "ADMIN")
                        .requestMatchers("/api/v1/admin/upload/**")
                        .hasAnyRole("CONTENT_CREATOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/books/**")
                        .hasAnyRole("CONTENT_CREATOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/admin/books/**")
                        .hasAnyRole("CONTENT_CREATOR", "ADMIN")
                        .requestMatchers("/api/v1/admin/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/logs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/books/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}