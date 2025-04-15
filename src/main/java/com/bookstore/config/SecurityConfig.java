package com.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.bookstore.repository.UserRepository;
import com.bookstore.security.CustomUserDetailsService;
import com.bookstore.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(
        UserRepository userRepository,
        @Lazy JwtAuthenticationFilter jwtAuthFilter,
        @Lazy AuthenticationProvider authenticationProvider) {
        this.userRepository = userRepository;
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            SecurityContextHolder.clearContext();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req -> 
                req.requestMatchers("/api/auth/**").permitAll() // Public authentication endpoints
                   .requestMatchers("/api/books").permitAll() // Public book listing
                   .requestMatchers("/api/books/search/**").permitAll() // Public book search
                   .requestMatchers("/api/books/{id}").permitAll() // Public book details
                   .requestMatchers("/api/orders").authenticated() // Order endpoints require authentication
                   .requestMatchers("/api/orders/**").authenticated() // All order endpoints require just authentication
                   .requestMatchers("/api/users/current").authenticated() // Current user endpoint requires authentication
                   .anyRequest().permitAll() // For demonstration, allow other requests
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> 
                logout.logoutUrl("/api/auth/logout")
                .addLogoutHandler(logoutHandler())
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow frontend origins with multiple port options
        configuration.addAllowedOrigin("http://localhost:3000"); // React dev server
        configuration.addAllowedOrigin("http://127.0.0.1:3000"); // Alternative localhost
        configuration.addAllowedOrigin("http://localhost:8080"); // In case frontend runs on 8080
        configuration.addAllowedOrigin("http://localhost:8081"); // In case frontend runs on 8081
        configuration.addAllowedOrigin("http://localhost:8082"); // In case frontend runs on 8082
        configuration.addAllowedOrigin("http://localhost:8083"); // In case frontend runs on 8083
        
        // Allow all methods and headers
        configuration.addAllowedMethod("*"); // Allow all HTTP methods
        configuration.addAllowedHeader("*"); // Allow all headers including Authorization
        
        // Enable credentials for auth
        configuration.setAllowCredentials(true); // Allow credentials for secure requests
        configuration.setMaxAge(3600L); // Cache preflight requests for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 